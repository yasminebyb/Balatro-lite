package domain;

import java.util.Objects;

/**
 * Représente le rang d'une carte à jouer.
 * <p>
 * Les 13 rangs sont déclarés du plus faible (TWO) au plus fort (ACE). L'ordre
 * de déclaration est important : {@link #ordinal()} est utilisé par
 * {@link HandEvaluator} pour détecter les suites.
 * </p>
 */
public enum Rank {

	TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9),
	TEN("10", 10), JACK("V", 10), QUEEN("D", 10), KING("R", 10), ACE("A", 11);

	/** Label d'affichage en français (ex: "V" pour Valet, "A" pour As). */
	private final String label;

	/**
	 * Valeur en chips de la carte. Utilisée dans l'extension A (score par cartes
	 * individuelles). Valet, Dame et Roi valent tous 10 par convention du poker.
	 * L'As vaut 11.
	 */
	private final int value;

	/**
	 * @param label : le label d'affichage, non null
	 * @param value : la valeur en chips de la carte
	 */
	Rank(String label, int value) {
		this.label = Objects.requireNonNull(label, "label must not be null");
		this.value = value;
	}

	/**
	 * Retourne le label d'affichage du rang. Exemple : {@code "V"} pour Valet,
	 * {@code "A"} pour As.
	 *
	 * @return le label d'affichage
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Retourne la valeur en chips du rang. Utilisée pour le calcul du score par
	 * cartes (extension A).
	 *
	 * @return la valeur en chips
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Retourne le label d'affichage.
	 *
	 * @return le label d'affichage
	 */
	@Override
	public String toString() {
		return label;
	}
}