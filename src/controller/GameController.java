package controller;

import domain.Card;
import domain.Hand;
import domain.Planet;
import model.GameState;
import view.View;
import view.Zen6View;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur principal du jeu Balatri.
 * <p>
 * Orchestre la boucle de jeu : gestion des blinds, des tours, du score et des
 * planètes. Interagit avec le joueur via la console provisoirement — sera
 * branché sur {@code View} ultérieurement.
 * </p>
 */
public class GameController {

	private final View view;
	private final GameState state;

	/**
	 * @param state l'état initial de la partie, non null
	 */
	public GameController(GameState state, View view) {
		this.state = state;
		this.view = view;
	}

	/**
	 * Lance et orchestre la partie complète.
	 */
	public void run() {
		view.showMessage("=== BALATRI ===");
		view.showMessage("Bonne chance !\n");

		while (!state.isGameWon() && !state.isGameOver()) {
			jouerUnBlind();
		}

		if (state.isGameWon()) {
			view.showMessage("\n=== VICTOIRE ! Vous avez battu tous les blinds ! ===");
		}
	}

	/**
	 * Gère le déroulement complet d'un blind.
	 */
	private void jouerUnBlind() {
		view.showMessage("=== " + state.getCurrentBlind().name().toUpperCase() + " — Cible : "
				+ state.getCurrentBlind().targetScore() + " pts ===");

		while (state.hasHandsRemaining() && !state.isBlindBeaten()) {
			view.showState(state);
			jouerUnTour();
		}

		if (state.isBlindBeaten()) {
			view.showMessage("\nBlind battu ! Score : " + state.getCurrentScore() + " / "
					+ state.getCurrentBlind().targetScore());
			gererBlindBattu();
		} else {
			String[] insults = { "T'es éclaté au sol.", "Retourne jouer au Uno.", "Même un chimpanzé joue mieux.",
					"Le blind t'a humilié.", "Poker et toi ça fait 12.", "Tu viens de te faire démonter.",
					"Le casino te rembourse par pitié.", "Même la pioche a honte.", "Supprime le jeu.",
					"Ton niveau est criminel." };

			String randomInsult = insults[(int) (Math.random() * insults.length)];

			if (view instanceof Zen6View zenView) {

				zenView.showLoseMessage(randomInsult);

			} else {

				view.showMessage(randomInsult);
			}
		}
	}

	/**
	 * Joue un tour complet : pioche 8 cartes, sélectionne 5, calcule le score.
	 */
	private void jouerUnTour() {
		// 1. piocher 8 cartes
		List<Card> pioche = state.getDeck().draw(8);

		// 2. afficher les cartes
		view.showCards(pioche);

		// 3. demander la sélection de 5 cartes
		List<Integer> indices = view.askCardSelection(pioche);

		// 4. construire la main avec les 5 cartes choisies
		List<Card> cartesChoisies = new ArrayList<>();
		for (int i : indices) {
			cartesChoisies.add(pioche.get(i));
		}
		Hand hand = new Hand(cartesChoisies);

		// 5. défausser les 3 cartes restantes
		List<Card> cartesDefaussees = new ArrayList<>(pioche);
		cartesDefaussees.removeAll(cartesChoisies);
		state.getDeck().discard(cartesDefaussees);

		// 6. calculer le score
		int chips = state.getChips(hand.getHandRank());
		int mult = state.getMult(hand.getHandRank());
		int score = chips * mult;

		// 7. afficher le résultat
		view.showMessage("\n" + hand);
		view.showMessage(
				hand.getHandRank().getLabel() + " → " + chips + " chips × " + mult + " mult = " + score + " pts");

		// 8. mettre à jour l'état
		state.addScore(score);
		state.decrementHands();
	}

	/**
	 * Gère la récompense après un blind battu : donne une planète aléatoire et
	 * passe au blind suivant.
	 */
	private void gererBlindBattu() {
		Planet planet = Planet.random();
		state.applyPlanet(planet);
		view.showMessage("Planète obtenue : " + planet.getLabel() + " → " + planet.getTarget().getLabel() + " +"
				+ planet.getBonusChips() + " chips" + " / +" + planet.getBonusMult() + " mult");

		view.showMessage("Nouveaux niveaux — " + planet.getTarget().getLabel() + " : "
				+ state.getChips(planet.getTarget()) + " chips × " + state.getMult(planet.getTarget()) + " mult\n");

		state.nextBlind(4);
	}
}