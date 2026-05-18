package view;

import domain.Card;
import domain.Hand;
import domain.HandRank;
import domain.Planet;
import model.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Vue console de Balatri.
 * <p>
 * Affiche le jeu dans le terminal et lit les entrées clavier du joueur.
 * Implémente {@link View} — peut être remplacée par {@code Zen6View} sans
 * modifier {@code GameController}.
 * </p>
 */
public class ConsoleView implements View {

	private final Scanner scanner = new Scanner(System.in);

	private static final String DOUBLE = "=".repeat(47);
	private static final String SINGLE = "-".repeat(47);

	// ===================== AFFICHAGE =====================

	/**
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException si {@code cards} est null
	 */
	@Override
	public void showHand(List<Card> cards) {
		Objects.requireNonNull(cards, "cards must not be null");
		IO.println("\n  +" + SINGLE + "+");
		IO.println("  |" + center("TES CARTES", 47) + "|");
		IO.println("  +" + SINGLE + "+");
		var sb = new StringBuilder();
		for (int i = 0; i < cards.size(); i++) {
			sb.setLength(0);
			sb.append("[").append(i).append("]  ").append(cards.get(i));
			IO.println("  |  " + pad(sb.toString(), 45) + "|");
		}
		IO.println("  +" + SINGLE + "+");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException     si {@code hand} est null
	 * @throws IllegalArgumentException si {@code score} est négatif
	 */
	@Override
	public void showHandResult(Hand hand, int score) {
		Objects.requireNonNull(hand, "hand must not be null");
		if (score < 0) {
			throw new IllegalArgumentException("score must not be negative");
		}
		IO.println("\n  +" + SINGLE + "+");
		IO.println("  |" + center("RESULTAT", 47) + "|");
		IO.println("  +" + SINGLE + "+");
		IO.println("  |  " + pad("Main  : " + hand.getCards(), 45) + "|");
		IO.println("  |  " + pad("Combo : " + hand.getHandRank().getLabel(), 45) + "|");
		IO.println("  |  " + pad("Score : +" + score + " pts", 45) + "|");
		IO.println("  +" + SINGLE + "+");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException si {@code state} est null
	 */
	@Override
	public void showGameState(GameState state) {
		Objects.requireNonNull(state, "state must not be null");
		IO.println("\n  +" + DOUBLE + "+");
		IO.println("  |" + center("B A L A T R I", 47) + "|");
		IO.println("  +" + DOUBLE + "+");
		IO.println("  |  " + pad("Blind  : " + state.getCurrentBlind().name(), 45) + "|");
		IO.println("  |  " + pad("Cible  : " + state.getCurrentBlind().targetScore() + " pts", 45) + "|");
		IO.println("  |  "
				+ pad("Score  : " + state.getCurrentScore() + " / " + state.getCurrentBlind().targetScore() + " pts",
						45)
				+ "|");
		IO.println("  |  " + pad("Mains  : " + handsBar(state.getHandsRemaining()), 45) + "|");
		IO.println("  |  " + pad("Pioche : " + state.getDeck().drawPileSize() + " cartes", 45) + "|");
		IO.println("  |  " + pad("Défausse : " + state.getDeck().discardPileSize() + " cartes", 45) + "|");
		IO.println("  +" + DOUBLE + "+");
		IO.println("  |" + center("Niveaux des combinaisons", 47) + "|");
		IO.println("  +" + SINGLE + "+");
		var sb = new StringBuilder();
		for (HandRank hr : HandRank.values()) {
			var pts = state.getChips(hr) * state.getMult(hr);
			sb.setLength(0);
			sb.append(hr.getLabel()).append(" : ").append(state.getChips(hr)).append(" chips").append(" x ")
					.append(state.getMult(hr)).append(" mult").append(" = ").append(pts).append(" pts");
			IO.println("  |  " + pad(sb.toString(), 45) + "|");
		}
		IO.println("  +" + DOUBLE + "+");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException si {@code planet} ou {@code state} est null
	 */
	@Override
	public void showPlanetReward(Planet planet, GameState state) {
		Objects.requireNonNull(planet, "planet must not be null");
		Objects.requireNonNull(state, "state must not be null");
		IO.println("\n  +" + DOUBLE + "+");
		IO.println("  |" + center("PLANETE OBTENUE", 47) + "|");
		IO.println("  +" + DOUBLE + "+");
		IO.println("  |  " + pad("Planete : " + planet.getLabel(), 45) + "|");
		IO.println("  |  " + pad("Cible   : " + planet.getTarget().getLabel(), 45) + "|");
		IO.println("  |  "
				+ pad("Bonus   : +" + planet.getBonusChips() + " chips" + " / +" + planet.getBonusMult() + " mult", 45)
				+ "|");
		IO.println("  +" + SINGLE + "+");
		var chips = state.getChips(planet.getTarget());
		var mult = state.getMult(planet.getTarget());
		IO.println("  |  " + pad("Nouveau : " + planet.getTarget().getLabel() + " = " + chips + " chips" + " x " + mult
				+ " mult" + " = " + (chips * mult) + " pts", 45) + "|");
		IO.println("  +" + DOUBLE + "+");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException si {@code message} est null
	 */
	@Override
	public void showMessage(String message) {
		Objects.requireNonNull(message, "message must not be null");
		IO.println(message);
	}

	/** {@inheritDoc} */
	@Override
	public void showVictory() {
		IO.println("\n  +" + DOUBLE + "+");
		IO.println("  |" + center("", 47) + "|");
		IO.println("  |" + center("VICTOIRE !", 47) + "|");
		IO.println("  |" + center("Tous les blinds ont ete battus !", 47) + "|");
		IO.println("  |" + center("", 47) + "|");
		IO.println("  +" + DOUBLE + "+");
	}

	/** {@inheritDoc} */
	@Override
	public void showDefeat() {
		IO.println("\n  +" + DOUBLE + "+");
		IO.println("  |" + center("", 47) + "|");
		IO.println("  |" + center("DEFAITE", 47) + "|");
		IO.println("  |" + center("Score insuffisant pour ce blind.", 47) + "|");
		IO.println("  |" + center("", 47) + "|");
		IO.println("  +" + DOUBLE + "+");
	}

	// ===================== SAISIE =====================

	/**
	 * {@inheritDoc}
	 *
	 * @throws NullPointerException     si {@code cards} est null
	 * @throws IllegalArgumentException si {@code cards} est vide
	 */
	@Override
	public List<Integer> askCardSelection(List<Card> cards) {
		Objects.requireNonNull(cards, "cards must not be null");
		if (cards.isEmpty()) {
			throw new IllegalArgumentException("cards must not be empty");
		}
		while (true) {
			IO.println("\n  Choisis 5 cartes parmi 0-7 (ex: 0 1 2 3 4) :");
			IO.print("  > ");
			var line = scanner.nextLine().trim();
			var parts = line.split("\\s+");

			if (parts.length != 5) {
				IO.println("  Erreur : choisis exactement 5 cartes.");
				continue;
			}

			var indices = new ArrayList<Integer>();
			var isValid = true;

			for (var part : parts) {
				try {
					var idx = Integer.parseInt(part);
					if (idx < 0 || idx >= cards.size()) {
						IO.println("  Erreur : indice " + idx + " invalide (0 a " + (cards.size() - 1) + ").");
						isValid = false;
						break;
					}
					if (indices.contains(idx)) {
						IO.println("  Erreur : indice " + idx + " choisi deux fois.");
						isValid = false;
						break;
					}
					indices.add(idx);
				} catch (NumberFormatException e) {
					IO.println("  Erreur : " + part + " n'est pas un nombre.");
					isValid = false;
					break;
				}
			}

			if (isValid) {
				return indices;
			}
		}
	}

	/**
	 * Centre un texte dans une largeur donnée.
	 *
	 * @param text  le texte à centrer, non null
	 * @param width la largeur totale, strictement positive
	 * @return le texte centré avec des espaces
	 */
	private static String center(String text, int width) {
		if (text.length() >= width)
			return text;
		var total = width - text.length();
		var left = total / 2;
		var right = total - left;
		return " ".repeat(left) + text + " ".repeat(right);
	}

	/**
	 * Complète un texte avec des espaces pour atteindre la largeur donnée.
	 *
	 * @param text  le texte à compléter, non null
	 * @param width la largeur cible, strictement positive
	 * @return le texte complété
	 */
	private static String pad(String text, int width) {
		if (text.length() >= width)
			return text.substring(0, width);
		return text + " ".repeat(width - text.length());
	}

	/**
	 * Affiche une barre visuelle des mains restantes. Exemple : [*][*][*][*] pour 4
	 * mains, [*][*][ ][ ] pour 2 mains.
	 *
	 * @param remaining le nombre de mains restantes, positif ou nul
	 * @return la barre visuelle
	 */
	private static String handsBar(int remaining) {
		int stars = Math.min(remaining, 4); 
		var empty = 4 - stars; 
		var bar = "[*]".repeat(stars) + "[ ]".repeat(empty);
		return bar + "  (" + remaining + " restante(s))";
	}

}