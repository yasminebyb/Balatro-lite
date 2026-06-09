package domain;

import java.util.Objects;

/**
 * Représente les 9 combinaisons de poker reconnues dans Balatri.
 *
 * Chaque combinaison est définie par un label d'affichage, un nombre de chips
 * de base et un multiplicateur de base. Le score de base est :
 * {@code baseChips × baseMult}.
 *
 * <ul>
 * <li>Ces valeurs peuvent être augmentées par les {@link Planet planètes} au
 * cours de la partie.</li>
 * <li>Les valeurs courantes (après planètes) sont stockées dans
 * {@code GameState}, pas ici.</li>
 * <li>Les combinaisons sont déclarées par ordre croissant de valeur, de
 * {@link #HIGH_CARD} (la plus faible) à {@link #STRAIGHT_FLUSH} (la plus
 * forte).</li>
 * </ul>
 *
 * @see Planet
 * @see HandEvaluator
 */
public enum HandRank {

	HIGH_CARD("Carte haute", 5, 1),
	PAIR("Paire", 10, 2),
	TWO_PAIR("Double paire", 20, 2),
	THREE_OF_A_KIND("Brelan", 30, 3),
	STRAIGHT("Suite", 30, 4),
	FLUSH("Couleur", 35, 4),
	FULL_HOUSE("Full", 40, 4),
	FOUR_OF_A_KIND("Carré", 60, 7),
	STRAIGHT_FLUSH("Quinte flush", 100, 8);

	// Label d'affichage en français
	private final String label;

	// Chips de base (valeur initiale, avant application des planètes)
	private final int baseChips;

	// Multiplicateur de base (valeur initiale, avant application des planètes)
	private final int baseMult;

	/**
	 * @param label     le label d'affichage, non null
	 * @param baseChips les chips de base, strictement positif
	 * @param baseMult  le multiplicateur de base, strictement positif
	 * @throws NullPointerException     si {@code label} est null
	 * @throws IllegalArgumentException si {@code baseChips} ou {@code baseMult} est
	 *                                  inférieur ou égal à zéro
	 */
	HandRank(String label, int baseChips, int baseMult) {
		this.label = Objects.requireNonNull(label, "label must not be null");
		if (baseChips <= 0) {
			throw new IllegalArgumentException("baseChips must be positive");
		}
		if (baseMult <= 0) {
			throw new IllegalArgumentException("baseMult must be positive");
		}
		this.baseChips = baseChips;
		this.baseMult = baseMult;
	}

	/**
	 * @return le label d'affichage, ex : {@code "Couleur"} pour {@link #FLUSH}
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return les chips de base de la combinaison
	 */
	public int getBaseChips() {
		return baseChips;
	}

	/**
	 * @return le multiplicateur de base de la combinaison
	 */
	public int getBaseMult() {
		return baseMult;
	}

	/** @return le label d'affichage */
	@Override
	public String toString() {
		return label;
	}
}