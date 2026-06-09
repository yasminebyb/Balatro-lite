package domain;

import java.util.Objects;

/**
 * Représente le rang d'une carte à jouer.
 *
 * Les 13 rangs sont déclarés du plus faible (TWO) au plus fort (ACE). L'ordre
 * de déclaration est important : {@link #ordinal()} est utilisé par
 * {@link HandEvaluator} pour détecter les suites.
 */
public enum Rank {

	TWO("2", 2),
	THREE("3", 3),
	FOUR("4", 4),
	FIVE("5", 5),
	SIX("6", 6),
	SEVEN("7", 7),
	EIGHT("8", 8),
	NINE("9", 9),
	TEN("10", 10),
	JACK("V", 10),
	QUEEN("D", 10),
	KING("R", 10),
	ACE("A", 11);

	// Label d'affichage (ex : "V" pour Valet, "A" pour As)
	private final String label;

	// Valeur en chips pour le calcul du score par cartes (Extension A)
	private final int value;

	/**
	 * @param label le label d'affichage, non null
	 * @param value la valeur en chips de la carte
	 * @throws NullPointerException si {@code label} est null
	 */
	Rank(String label, int value) {
		this.label = Objects.requireNonNull(label, "label must not be null");
		this.value = value;
	}

	/** @return le label d'affichage, ex : {@code "V"} pour Valet */
	public String getLabel() {
		return label;
	}

	/**
	 * @return la valeur en chips du rang. tilisée pour le calcul du score par
	 *         cartes (extension A).
	 */
	public int getValue() {
		return value;
	}

	/** @return le label d'affichage */
	@Override
	public String toString() {
		return label;
	}
}