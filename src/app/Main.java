package app;

import java.io.IOException;
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

		IO.println("=== BALATRI ===");
		IO.println("1 - Console");
		IO.println("2 - Interface graphique");

		var choice = scanner.nextInt();

		View view;
		try {
			switch (choice) {

			case 1 -> view = new ConsoleView();

			case 2 -> view = new Zen6View();

			default -> {
				System.out.println("Choix invalide.");
				scanner.close();
				return;
			}
			}

		} catch (IOException e) {

			System.err.println(e.getMessage());
			scanner.close();
			System.exit(1);
			return;
		}
		List<Blind> blinds = List.of(new StandardBlind("Petit aveugle", 300), new StandardBlind("Grand aveugle", 800),
				new StandardBlind("Boss", 2000));

		var state = new GameState(blinds, 4);

		var controller = new GameController(state, view);

		controller.run();
		scanner.close();
	}
}