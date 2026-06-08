package app;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import controller.GameController;
import domain.Blind;
import domain.StandardBlind;
import model.GameState;
import view.ConsoleView;
import view.Zen6View;

public class Main {

	public static void main(String[] args) {
		try {
			startGame();
		} catch (IOException e) {
			System.err.println("Erreur : " + e.getMessage());
			System.exit(1);
		}
	}

	private static void startGame() throws IOException {
		var blinds = List.<Blind>of(new StandardBlind("Little Blind", 300), new StandardBlind("Big Blind", 800),
				new StandardBlind("Boss Blind", 2000));

		var scanner = new Scanner(System.in);
		boolean shouldContinue = true;

		while (shouldContinue) {
			var state = new GameState(blinds);

			IO.println("\n=== BALATRI ===");
			IO.println("1 - Console");
			IO.println("2 - Interface graphique");
			IO.println("3 - Quitter");
			IO.println("Votre choix : ");

			var choice = scanner.nextInt();

			switch (choice) {
			case 1 -> {
				new GameController(state, new ConsoleView()).run();
			}
			case 2 -> {
				var view = new Zen6View();
				var controller = new GameController(state, view);
				view.start(controller);
				IO.println("Fermeture de l'interface graphique. Retour au menu...");
			}
			case 3 -> {
				IO.println("Fermeture du jeu. À bientôt !");
				shouldContinue = false;
			}
			default -> {
				System.err.println("Choix invalide. Veuillez réessayer.");
			}
			}
		}
		scanner.close();
		System.exit(0);
	}
}