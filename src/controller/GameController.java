package controller;

import domain.Card;
import domain.Hand;
import domain.HandEvaluator;
import domain.Planet;
import model.GameState;
import view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Contrôleur principal du jeu Balatri.
 *
 * <p>
 * Supporte deux paradigmes d'exécution :
 * </p>
 * <ul>
 * <li><b>Mode Console (impératif) :</b> {@link #run()} orchestre la boucle de
 * jeu de manière séquentielle et bloquante.</li>
 * <li><b>Mode Graphique Zen6 (événementiel) :</b> {@link #initTurn()},
 * {@link #onDiscardComplete(List)} et {@link #onSelectionComplete(List)} sont
 * appelées en réaction aux interactions de l'utilisateur.</li>
 * </ul>
 *
 * <p>
 * Extension B — Défausse active : avant de jouer ses 5 cartes, le joueur peut
 * défausser et remplacer certaines cartes. Le nombre de défausses disponibles
 * par blind est défini dans {@link GameState}.
 * </p>
 */
public class GameController {

	private final GameState state;
	private final View view;

	/**
	 * Main courante en mode Zen6, mutable pour intégrer les remplacements lors des
	 * défausses actives (Extension B).
	 */
	private ArrayList<Card> currentDraw;

	/**
	 * @param state l'état initial de la partie, non null
	 * @param view  la vue à utiliser, non null
	 */
	public GameController(GameState state, View view) {
		this.state = Objects.requireNonNull(state, "state must not be null");
		this.view = Objects.requireNonNull(view, "view must not be null");
	}

	// === Mode Console ===

	/**
	 * Lance la partie complète en mode console (boucle bloquante).
	 */
	public void run() {
		view.showMessage("=== BALATRI === Bonne chance !\n");
		while (!state.isGameWon() && !state.isGameOver()) {
			playBlind();
		}
		if (state.isGameWon()) {
			view.showVictory();
		}
	}

	/**
	 * Gère un blind complet : joue des tours jusqu'à victoire ou épuisement des
	 * mains.
	 */
	private void playBlind() {
		view.showMessage("\n=== " + state.getCurrentBlind().name().toUpperCase()
				+ " — Cible : " + state.getCurrentBlind().targetScore() + " pts ===");

		while (state.hasHandsRemaining() && !state.isBlindBeaten()) {
			view.showGameState(state);
			playTurn();
		}

		if (state.isBlindBeaten()) {
			handleBeatenBlind();
		} else {
			view.showDefeat();
		}
	}

	/**
	 * Gère un tour complet en mode console.
	 *
	 * <p>
	 * Flux : pioche {@value GameState#DRAW_SIZE} cartes → phase de défausse active
	 * (Extension B) → sélection de 5 cartes → calcul du score.
	 * </p>
	 */
	private void playTurn() {
		var hand = new ArrayList<>(state.getDeck().draw(GameState.DRAW_SIZE));
		view.showHand(Collections.unmodifiableList(hand));

		// Extension B — phase de défausse active
		while (state.hasDiscardsRemaining()) {
			var toDiscard = view.askDiscardSelection(
					Collections.unmodifiableList(hand),
					state.getDiscardsRemaining());

			if (toDiscard.isEmpty())
				break;
			processDiscard(hand, toDiscard);
			view.showDiscardResult(Collections.unmodifiableList(hand),
					state.getDiscardsRemaining());
		}

		var indices = view.askCardSelection(Collections.unmodifiableList(hand));
		applySelection(Collections.unmodifiableList(hand), indices);
	}

	// === Mode Zen6 ===

	/**
	 * Initialise un nouveau tour en mode graphique : pioche
	 * {@value GameState#DRAW_SIZE} cartes et transmet une vue immuable à la vue.
	 */
	public void initTurn() {
		if (state.isGameWon()) {
			view.showVictory();
			return;
		}
		if (state.isGameOver()) {
			view.showDefeat();
			return;
		}
		view.showGameState(state);
		currentDraw = new ArrayList<>(state.getDeck().draw(GameState.DRAW_SIZE));
		view.showHand(Collections.unmodifiableList(currentDraw));
	}

	/**
	 * Appelé par {@code Zen6View} quand le joueur confirme une défausse active
	 * (Extension B).
	 *
	 * @param indices indices (base 0) des cartes à défausser, non null, au plus 5
	 *                éléments
	 * @throws NullPointerException     si {@code indices} est null
	 * @throws IllegalArgumentException si {@code indices} contient plus de 5
	 *                                  éléments
	 */
	public void onDiscardComplete(List<Integer> indices) {
		Objects.requireNonNull(indices, "indices must not be null");
		if (indices.isEmpty() || !state.hasDiscardsRemaining())
			return;
		if (indices.size() > 5) {
			throw new IllegalArgumentException("Cannot discard more than 5 cards, got: " + indices.size());
		}
		processDiscard(currentDraw, indices);
		view.showDiscardResult(Collections.unmodifiableList(currentDraw),
				state.getDiscardsRemaining());
	}

	/**
	 * Appelé par {@code Zen6View} quand le joueur valide sa sélection de 5 cartes.
	 *
	 * @param indices les 5 indices (base 0) des cartes choisies, non null
	 * @throws NullPointerException      si {@code indices} est null
	 * @throws IllegalArgumentException  si {@code indices} ne contient pas
	 *                                   exactement 5 éléments distincts
	 * @throws IndexOutOfBoundsException si un indice est hors des bornes de la main
	 *                                   courante
	 */
	public void onSelectionComplete(List<Integer> indices) {
		Objects.requireNonNull(indices, "indices must not be null");
		validateSelectionIndices(indices, currentDraw.size());

		applySelection(Collections.unmodifiableList(currentDraw), indices);

		if (state.isBlindBeaten())
			handleBeatenBlind();
		if (state.isGameWon()) {
			view.showVictory();
			return;
		}
		if (state.isGameOver()) {
			view.showDefeat();
			return;
		}

		initTurn();
	}

	// === Commun ===
	/**
	 * Valide les indices de sélection reçus depuis l'interface graphique.
	 *
	 * <p>
	 * En mode console, la validation est assurée par {@code ConsoleView}. En mode
	 * Zen6, {@code onSelectionComplete} étant publique, cette vérification est
	 * indispensable.
	 * </p>
	 *
	 * @param indices  indices à valider, non null
	 * @param handSize taille de la main courante
	 * @throws NullPointerException      si {@code indices} est null
	 * @throws IllegalArgumentException  si la taille ≠ 5 ou s'il y a des doublons
	 * @throws IndexOutOfBoundsException si un indice est hors bornes
	 */
	private void validateSelectionIndices(List<Integer> indices, int handSize) {
		Objects.requireNonNull(indices, "indices must not be null");

		if (indices.size() != 5)
			throw new IllegalArgumentException(
					"Exactly 5 indices required, got: " + indices.size());

		if (new HashSet<>(indices).size() != 5)
			throw new IllegalArgumentException(
					"Indices must be distinct, got: " + indices);

		for (int idx : indices) {
			if (idx < 0 || idx >= handSize)
				throw new IndexOutOfBoundsException(
						"Index " + idx + " out of bounds for hand size " + handSize);
		}
	}

	/**
	 * Effectue la défausse active : retire les cartes aux indices donnés, les
	 * envoie en défausse, tire des remplaçantes et décrémente le compteur.
	 *
	 * <p>
	 * Les indices sont traités du plus grand au plus petit pour éviter le décalage
	 * lors des suppressions successives sur {@code ArrayList}.
	 * </p>
	 *
	 * @param hand             la main mutable à modifier, non null
	 * @param indicesToDiscard indices des cartes à retirer, non null
	 * @throws NullPointerException si {@code hand} ou {@code indicesToDiscard} est
	 *                              null
	 */
	private void processDiscard(ArrayList<Card> hand, List<Integer> indicesToDiscard) {
		Objects.requireNonNull(hand, "hand must not be null");
		Objects.requireNonNull(indicesToDiscard, "indicesToDiscard must not be null");

		var sorted = indicesToDiscard.stream()
				.sorted(Comparator.reverseOrder())
				.toList();

		var discarded = new ArrayList<Card>(sorted.size());
		for (int idx : sorted) {
			discarded.add(hand.remove(idx));
		}

		state.getDeck().discard(discarded);
		hand.addAll(state.getDeck().draw(discarded.size()));
		state.decrementDiscards();
	}

	/**
	 * Évalue la main sélectionnée, calcule le score (Extension A), met à jour
	 * l'état et défausse toutes les cartes du tour.
	 *
	 * <p>
	 * {@code score = (chips + bonus cartes actives) × multiplicateur}
	 * </p>
	 *
	 * @param drawnCards la main complète du tour (immuable), non null
	 * @param indices    les 5 indices des cartes jouées, non null
	 * @throws NullPointerException si {@code drawnCards} ou {@code indices} est
	 *                              null
	 */
	private void applySelection(List<Card> drawnCards, List<Integer> indices) {
		Objects.requireNonNull(drawnCards, "drawnCards must not be null");
		Objects.requireNonNull(indices, "indices must not be null");

		var selectedCards = indices.stream().map(drawnCards::get).toList();
		var hand = new Hand(selectedCards);

		var active = HandEvaluator.activeCards(selectedCards);
		int cardBonus = active.stream().mapToInt(c -> c.rank().getValue()).sum();
		view.showActiveCards(active, cardBonus);

		int chips = state.getChips(hand.getHandRank());
		int mult = state.getMult(hand.getHandRank());
		int score = (chips + cardBonus) * mult;
		view.showHandResult(hand, score);

		state.addScore(score);
		state.decrementHands();
		state.getDeck().discard(drawnCards);
	}

	/**
	 * Attribue une planète aléatoire et avance au blind suivant.
	 *
	 * La récompense est affichée avant l'avancement au blind suivant afin que la
	 * vue reflète l'état du blind qui vient d'être battu.
	 */
	private void handleBeatenBlind() {
		var planet = Planet.random();
		state.applyPlanet(planet);
		view.showPlanetReward(planet, state);
		state.nextBlind();
	}
}