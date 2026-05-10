package domain;

import java.util.Objects;

/**
 * Représente les 9 combinaisons de poker reconnues dans Balatri.
 * <p>
 * Chaque combinaison est définie par :
 * <ul>
 * <li>un label d'affichage en français</li>
 * <li>un nombre de chips de base</li>
 * <li>un multiplicateur de base</li>
 * </ul>
 * Le score de base d'une combinaison est : {@code baseChips × baseMult}. Ces
 * valeurs peuvent être augmentées par les {@link Planet planètes} au cours de
 * la partie : les valeurs courantes sont stockées dans {@code GameState}, pas
 * ici.
 * </p>
 * <p>
 * Les combinaisons sont déclarées par ordre croissant de valeur, de
 * {@link #HIGH_CARD} (la plus faible) à {@link #STRAIGHT_FLUSH} (la plus
 * forte).
 * </p>
 *
 * @see Planet
 * @see HandEvaluator
 */
public enum HandRank {

	HIGH_CARD("Carte haute", 5, 1), PAIR("Paire", 10, 2), TWO_PAIR("Double paire", 20, 2),
	THREE_OF_A_KIND("Brelan", 30, 3), STRAIGHT("Suite", 30, 4), FLUSH("Couleur", 35, 4), FULL_HOUSE("Full", 40, 4),
	FOUR_OF_A_KIND("Carré", 60, 7), STRAIGHT_FLUSH("Quinte flush", 100, 8);

	/** Label d'affichage en français. */
	private final String label;

	/**
	 * Chips de base de la combinaison. Valeur initiale : peut être augmentée par
	 * les planètes.
	 */
	private final int baseChips;

	/**
	 * Multiplicateur de base de la combinaison. Valeur initiale : peut être
	 * augmentée par les planètes.
	 */
	private final int baseMult;

	/**
	 * @param label     le label d'affichage, non null
	 * @param baseChips les chips de base, doit être positif
	 * @param baseMult  le multiplicateur de base, doit être positif
	 * @throws NullPointerException     si {@code label} est null
	 * @throws IllegalArgumentException si {@code baseChips} ou {@code baseMult} est
	 *                                  négatif ou nul
	 */
	HandRank(String label, int baseChips, int baseMult) {
		this.label = Objects.requireNonNull(label, "label must not be null");
		if (baseChips <= 0)
			throw new IllegalArgumentException("baseChips must be positive");
		if (baseMult <= 0)
			throw new IllegalArgumentException("baseMult must be positive");
		this.baseChips = baseChips;
		this.baseMult = baseMult;
	}

	/**
	 * Retourne le label d'affichage de la combinaison. Exemple : {@code "Couleur"}
	 * pour {@link #FLUSH}.
	 *
	 * @return le label d'affichage
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Retourne les chips de base de la combinaison. Cette valeur est la valeur
	 * initiale définie dans les règles du jeu. La valeur courante (après planètes)
	 * est dans {@code GameState}.
	 *
	 * @return les chips de base
	 */
	public int getBaseChips() {
		return baseChips;
	}

	/**
	 * Retourne le multiplicateur de base de la combinaison. Cette valeur est la
	 * valeur initiale définie dans les règles du jeu. La valeur courante (après
	 * planètes) est dans {@code GameState}.
	 *
	 * @return le multiplicateur de base
	 */
	public int getBaseMult() {
		return baseMult;
	}

	/**
	 * Retourne le label d'affichage de la combinaison.
	 * 
	 * @return le label d'affichage
	 */
	@Override
	public String toString() {
		return label;
	}
}