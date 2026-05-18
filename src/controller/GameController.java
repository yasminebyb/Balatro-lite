package controller;

import domain.Card;
import domain.Hand;
import domain.Planet;
import model.GameState;
import view.View;
import java.util.List;
import java.util.Objects;

/**
 * Contrôleur principal du jeu Balatri.
 * <p>
 * Elle supporte deux paradigmes d'exécution :
 * <ul>
 * <li><b>Mode Console (Impératif) :</b> La méthode {@link #run()} bloque le fil
 * d'exécution et gère la boucle de jeu de manière séquentielle.</li>
 * <li><b>Mode Graphique Zen6 (Événementiel) :</b> Les méthodes
 * {@link #initTurn()} et {@link #onSelectionComplete(List)} sont appelées en
 * réaction aux interactions de l'utilisateur, sans bloquer la boucle de rendu
 * de l'interface.</li>
 * </ul>
 * </p>
 */
public class GameController {

	private final GameState state;
	private final View view;
	private List<Card> currentDraw;

	/**
	 * @param state l'état initial de la partie, non null
	 * @param view  la vue à utiliser, non null
	 */
	public GameController(GameState state, View view) {
		this.state = Objects.requireNonNull(state, "state must not be null");
		this.view = Objects.requireNonNull(view, "view must not be null");
	}

	// ===================== MODE CONSOLE =====================

	/**
	 * Lance la partie complète en mode console.
	 * <p>
	 * Cette méthode contient la boucle de jeu principale.
	 * </p>
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
	 * Gère la boucle de jeu pour un "Blind" complet en mode console.
	 * <p>
	 * Fait jouer des tours au joueur tant qu'il lui reste des mains et que le score
	 * cible n'est pas atteint. Gère ensuite la victoire ou la défaite du Blind.
	 * </p>
	 */
	private void playBlind() {
		view.showMessage("\n=== " + state.getCurrentBlind().name().toUpperCase() + " — Cible : "
				+ state.getCurrentBlind().targetScore() + " pts ===");

		while (state.hasHandsRemaining() && !state.isBlindBeaten()) {
			view.showGameState(state);
			jouerUnTour();
		}

		if (state.isBlindBeaten()) {
			handleBeatenBlind();
		} else {
			view.showDefeat();
		}
	}

	/**
	 * Gère le déroulement d'un unique tour de jeu (Mode Console). Pioche les
	 * cartes, les affiche, demande le choix au joueur et applique le score.
	 */
	private void jouerUnTour() {
		var drawnCards = state.getDeck().draw(8);
		view.showHand(drawnCards);
		var indices = view.askCardSelection(drawnCards);
		applySelection(drawnCards, indices);
	}

	// ===================== MODE ZEN6 =====================

	/**
	 * Initialise un nouveau tour de jeu en mode graphique (Zen6).
	 * <p>
	 * Pioche 8 cartes et les transmet à la vue pour affichage. Appelé par
	 * {@code Zen6View} au démarrage du jeu et après chaque tour validé.
	 * </p>
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
		currentDraw = state.getDeck().draw(8);
		view.showHand(currentDraw);
	}

	/**
	 * Appelé par {@code Zen6View} quand le joueur a sélectionné 5 cartes. Calcule
	 * le score de la main, met à jour l'état de la partie, vérifie les conditions
	 * de victoire/défaite, puis prépare le tour suivant.
	 * 
	 * @param indices la liste des 5 indices (de 0 à 7) correspondant aux cartes
	 *                choisies
	 * @throws NullPointerException si la liste {@code indices} est {@code null}
	 */

	public void onSelectionComplete(List<Integer> indices) {
		Objects.requireNonNull(indices, "indices must not be null");
		applySelection(currentDraw, indices);

		if (state.isBlindBeaten()) {
			handleBeatenBlind();
		}
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

	// ===================== COMMUN =====================

	/**
	 * Applique la sélection du joueur, évalue la main et met à jour le score
	 * global. Méthode partagée entre le mode Console et le mode Zen6.
	 *
	 * @param drawnCards la liste des 8 cartes piochées pour ce tour
	 * @param indices    la liste des indices des 5 cartes sélectionnées
	 */
	private void applySelection(List<Card> drawnCards, List<Integer> indices) {
		var selectedCards = indices.stream().map(drawnCards::get).toList();

		var hand = new Hand(selectedCards);
		int chips = state.getChips(hand.getHandRank());
		int mult = state.getMult(hand.getHandRank());
		int score = chips * mult;
		view.showHandResult(hand, score);

		state.addScore(score);
		state.decrementHands();
		state.getDeck().discard(drawnCards);
	}

	/**
	 * Applique les récompenses (Planètes) lorsqu'un Blind est battu et fait passer
	 * le jeu au Blind suivant.
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