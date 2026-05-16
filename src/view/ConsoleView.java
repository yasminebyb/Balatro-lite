package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import domain.Card;
import model.GameState;

public class ConsoleView implements View {

	private final Scanner scanner;

	public ConsoleView() {
		this.scanner = new Scanner(System.in);
	}

	@Override
	public void showMessage(String message) {
		IO.println(message);
	}

	@Override
	public void showState(GameState state) {
		IO.println("\nScore : " + state.getCurrentScore() + " / " + state.getCurrentBlind().targetScore()
				+ " | Mains restantes : " + state.getHandsRemaining() + " | Pioche : "
				+ state.getDeck().drawPileSize());
	}

	@Override
	public void showCards(List<Card> cards) {
		IO.println("\nTes cartes :");

		for (int i = 0; i < cards.size(); i++) {
			IO.println(i + " : " + cards.get(i));
		}

	}

	@Override
	public List<Integer> askCardSelection(List<Card> cards) {
		while (true) {
			IO.println("Choisis 5 cartes (ex: 0 1 2 3 4) :");

			String line = scanner.nextLine().trim();
			
			if (line.equalsIgnoreCase("q")) {

			    IO.println("Partie quittée.");
			    scanner.close();
			    System.exit(0);
			}

			String[] parts = line.split("\\s+");

			if (parts.length != 5) {
				IO.println("Erreur : tu dois choisir exactement 5 cartes.");
				continue;
			}

			List<Integer> indices = new ArrayList<>();

			boolean valid = true;

			for (String part : parts) {

				try {

					int idx = Integer.parseInt(part);

					if (idx < 0 || idx >= cards.size()) {
						IO.println("Erreur : indice invalide.");
						valid = false;
						break;
					}

					if (indices.contains(idx)) {
						IO.println("Erreur : doublon.");
						valid = false;
						break;
					}

					indices.add(idx);

				} catch (NumberFormatException e) {

					IO.println("Erreur : entrée invalide.");
					valid = false;
					break;
				}
			}
			if (valid) {
				return indices;
			}

		}
	}

}
