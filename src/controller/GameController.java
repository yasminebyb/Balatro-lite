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
import java.util.List;
import java.util.Objects;

/**
 * Contrôleur principal du jeu Balatri.
 * <p>
 * Supporte deux paradigmes d'exécution :
 * </p>
 * <ul>
 *   <li><b>Mode Console (impératif) :</b> {@link #run()} bloque le thread et
 *       orchestre la boucle de jeu de manière séquentielle.</li>
 *   <li><b>Mode Graphique Zen6 (événementiel) :</b> {@link #initTurn()},
 *       {@link #onDiscardComplete(List)} et {@link #onSelectionComplete(List)}
 *       sont appelées en réaction aux interactions de l'utilisateur, sans
 *       bloquer la boucle de rendu.</li>
 * </ul>
 *
 * <h2>Extension B — Défausse active</h2>
 * <p>
 * Avant de jouer ses 5 cartes, le joueur peut défausser une ou plusieurs
 * cartes de sa main et les remplacer par de nouvelles. Le nombre de défausses
 * disponibles par blind est défini dans {@link GameState}.
 * </p>
 */
public class GameController {

	private final GameState state;
	private final View view;

	/**
	 * Main courante en mode Zen6 : {@code ArrayList} mutable pour pouvoir
	 * intégrer les remplacements lors des défausses actives (Extension B).
	 * En mode console, la liste mutable est locale à {@link #playTurn()}.
	 */
	private ArrayList<Card> currentDraw;

	/**
	 * @param state l'état initial de la partie, non null
	 * @param view  la vue à utiliser, non null
	 */
	public GameController(GameState state, View view) {
		this.state = Objects.requireNonNull(state, "state must not be null");
		this.view  = Objects.requireNonNull(view,  "view must not be null");
	}

	// ===================== MODE CONSOLE =====================

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
	 * Gère un blind complet en mode console : joue des tours jusqu'à victoire
	 * ou épuisement des mains.
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
	 * <p>
	 * Flux : pioche 8 cartes → <em>phase de défausse active</em> (Extension B,
	 * tant qu'il reste des défausses et que le joueur le souhaite) →
	 * sélection de 5 cartes → calcul du score.
	 * </p>
	 */
	private void playTurn() {
		// La main est un ArrayList mutable pour accueillir les remplacements
		var hand = new ArrayList<>(state.getDeck().draw(8));
		view.showHand(Collections.unmodifiableList(hand));

		// ── Phase de défausse active (Extension B) ──────────────────────────
		while (state.hasDiscardsRemaining()) {
			var toDiscard = view.askDiscardSelection(
					Collections.unmodifiableList(hand),
					state.getDiscardsRemaining());

			if (toDiscard.isEmpty()) break; // le joueur passe

			processDiscard(hand, toDiscard);
			view.showDiscardResult(Collections.unmodifiableList(hand),
					state.getDiscardsRemaining());
		}
		// ────────────────────────────────────────────────────────────────────

		var indices = view.askCardSelection(Collections.unmodifiableList(hand));
		applySelection(Collections.unmodifiableList(hand), indices);
	}

	// ===================== MODE ZEN6 =====================

	/**
	 * Initialise un nouveau tour en mode graphique.
	 * <p>
	 * Pioche 8 cartes dans un {@code ArrayList} mutable (nécessaire pour les
	 * remplacements de la défausse active) et transmet une vue immuable à la vue.
	 * </p>
	 */
	public void initTurn() {
		if (state.isGameWon()) { view.showVictory(); return; }
		if (state.isGameOver()) { view.showDefeat(); return; }
		view.showGameState(state);
		currentDraw = new ArrayList<>(state.getDeck().draw(8));
		view.showHand(Collections.unmodifiableList(currentDraw));
	}

	/**
	 * Appelé par {@code Zen6View} quand le joueur confirme une défausse active
	 * (Extension B).
	 * <p>
	 * Les cartes aux indices donnés sont retirées de la main courante, envoyées
	 * en défausse, et remplacées par de nouvelles cartes piochées. La vue est
	 * notifiée via {@link View#showDiscardResult}.
	 * </p>
	 *
	 * @param indices indices (base 0) des cartes à défausser, non null, non vide
	 * @throws NullPointerException si {@code indices} est null
	 */
	public void onDiscardComplete(List<Integer> indices) {
		Objects.requireNonNull(indices, "indices must not be null");
		if (indices.isEmpty() || !state.hasDiscardsRemaining()) return;

		processDiscard(currentDraw, indices);
		view.showDiscardResult(Collections.unmodifiableList(currentDraw),
				state.getDiscardsRemaining());
	}

	/**
	 * Appelé par {@code Zen6View} quand le joueur valide sa sélection de 5 cartes.
	 *
	 * @param indices les 5 indices (base 0) des cartes choisies, non null
	 * @throws NullPointerException si {@code indices} est null
	 */
	public void onSelectionComplete(List<Integer> indices) {
		Objects.requireNonNull(indices, "indices must not be null");
		applySelection(Collections.unmodifiableList(currentDraw), indices);

		if (state.isBlindBeaten()) handleBeatenBlind();
		if (state.isGameWon())     { view.showVictory(); return; }
		if (state.isGameOver())    { view.showDefeat();  return; }

		initTurn();
	}

	// ===================== COMMUN =====================

	/**
	 * Effectue la défausse active : retire les cartes aux indices donnés de la
	 * main, les envoie dans la défausse du paquet, tire le même nombre de cartes
	 * de remplacement, et décrémente le compteur de défausses dans l'état.
	 *
	 * <p><strong>Précondition :</strong> {@code hand} doit être une liste mutable
	 * ({@code ArrayList}) — ce contrat est garanti par les appelants internes.</p>
	 *
	 * <p>Les indices sont traités du plus grand au plus petit pour éviter le
	 * décalage des indices lors des suppressions successives ({@code remove(int)}
	 * sur {@code ArrayList} est O(n) mais correct à condition de supprimer en
	 * ordre décroissant).</p>
	 *
	 * @param hand            la main mutable à modifier
	 * @param indicesToDiscard indices des cartes à retirer, non null
	 */
	private void processDiscard(ArrayList<Card> hand, List<Integer> indicesToDiscard) {
		// Tri décroissant : supprimer index 7 avant 3 avant 0, sinon les indices
		// des éléments restants seraient décalés après chaque remove().
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
	 * Évalue la main sélectionnée, calcule le score (Extension A intégrée),
	 * met à jour l'état et défausse toutes les cartes du tour.
	 * <p>
	 * Le score final tient compte du bonus des cartes actives :
	 * {@code score = (chips + cardBonus) × mult}.
	 * </p>
	 *
	 * @param drawnCards la main complète du tour (immuable), après défausses
	 * @param indices    les 5 indices des cartes jouées
	 */
	private void applySelection(List<Card> drawnCards, List<Integer> indices) {
		var selectedCards = indices.stream().map(drawnCards::get).toList();
		var hand = new Hand(selectedCards);

		// Extension A — bonus des cartes actives
		var active    = HandEvaluator.activeCards(selectedCards);
		int cardBonus = active.stream().mapToInt(c -> c.rank().getValue()).sum();
		view.showActiveCards(active, cardBonus);

		int chips = state.getChips(hand.getHandRank());
		int mult  = state.getMult(hand.getHandRank());
		int score = (chips + cardBonus) * mult;   // Extension A intégrée
		view.showHandResult(hand, score);

		state.addScore(score);
		state.decrementHands();
		state.getDeck().discard(drawnCards);       // défausse toute la main du tour
	}

	/**
	 * Attribue une planète aléatoire et avance au blind suivant.
	 */
	private void handleBeatenBlind() {
		var planet = Planet.random();
		state.applyPlanet(planet);
		state.nextBlind(4);
		if (!state.isGameWon()) {
			view.showPlanetReward(planet, state);
		}
	}
}