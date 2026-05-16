package app;

import java.util.List;
import java.util.Scanner;

import controller.GameController;
import domain.Blind;
import domain.StandardBlind;
import model.GameState;
import view.ConsoleView;
import view.View;
import view.Zen6View;

public class Main {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		System.out.println("=== BALATRI ===");
		System.out.println("1 - Console");
		System.out.println("2 - Interface graphique");

		int choice = scanner.nextInt();

		View view;

		switch (choice) {

			case 1 -> view = new ConsoleView();

			case 2 -> view = new Zen6View();

			default -> {
				System.out.println("Choix invalide.");
				scanner.close();
				return;
			}
		}

		List<Blind> blinds = List.of(
				new StandardBlind("Petit aveugle", 300),
				new StandardBlind("Grand aveugle", 800),
				new StandardBlind("Boss", 2000)
		);

		GameState state =
				new GameState(blinds, 4);

		GameController controller =
				new GameController(state, view);

		controller.run();

		scanner.close();
	}
}