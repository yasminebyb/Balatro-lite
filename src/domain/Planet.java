package domain;

import java.util.Objects;
import java.util.Random;

/**
 * Représente une planète - modificateur permanent de score.
 * <p>
 * Chaque planète est associée à une combinaison ({@link HandRank}). Lorsqu'une
 * planète est obtenue, elle augmente définitivement les chips et le
 * multiplicateur de la combinaison ciblée pour le reste de la partie. Ces
 * valeurs sont stockées et appliquées dans {@code GameState}.
 * </p>
 * <p>
 * À chaque blind battu, le joueur reçoit une planète choisie aléatoirement via
 * {@link #random()}. Il est possible d'obtenir plusieurs fois la même planète —
 * les bonus s'accumulent.
 * </p>
 *
 * @see HandRank
 * @see model.GameState
 */
public enum Planet {

	PLUTO("Pluton", HandRank.HIGH_CARD, 10, 1), MERCURY("Mercure", HandRank.PAIR, 15, 1),
	URANUS("Uranus", HandRank.TWO_PAIR, 20, 1), VENUS("Vénus", HandRank.THREE_OF_A_KIND, 20, 2),
	SATURN("Saturne", HandRank.STRAIGHT, 30, 3), JUPITER("Jupiter", HandRank.FLUSH, 15, 2),
	EARTH("Terre", HandRank.FULL_HOUSE, 25, 2), MARS("Mars", HandRank.FOUR_OF_A_KIND, 30, 3),
	NEPTUNE("Neptune", HandRank.STRAIGHT_FLUSH, 40, 4);

	/** Label d'affichage en français. */
	private final String label;

	/** La combinaison de poker ciblée par cette planète. */
	private final HandRank target;

	/** Bonus de chips ajouté à la combinaison ciblée. */
	private final int bonusChips;

	/** Bonus de multiplicateur ajouté à la combinaison ciblée. */
	private final int bonusMult;

	/**
	 * @param label      le label d'affichage, non null
	 * @param target     la combinaison ciblée, non null
	 * @param bonusChips le bonus de chips, strictement positif
	 * @param bonusMult  le bonus de multiplicateur, strictement positif
	 * @throws NullPointerException     si {@code label} ou {@code target} est null
	 * @throws IllegalArgumentException si {@code bonusChips} ou {@code bonusMult}
	 *                                  est négatif ou nul
	 */
	Planet(String label, HandRank target, int bonusChips, int bonusMult) {
		this.label = Objects.requireNonNull(label, "label must not be null");
		this.target = Objects.requireNonNull(target, "target must not be null");
		if (bonusChips <= 0)
			throw new IllegalArgumentException("bonusChips must be positive");
		if (bonusMult <= 0)
			throw new IllegalArgumentException("bonusMult must be positive");
		this.bonusChips = bonusChips;
		this.bonusMult = bonusMult;
	}

	/**
	 * Retourne le label d'affichage de la planète.
	 *
	 * @return le label d'affichage
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Retourne la combinaison de poker ciblée par cette planète.
	 *
	 * @return le {@link HandRank} ciblé
	 */
	public HandRank getTarget() {
		return target;
	}

	/**
	 * Retourne le bonus de chips apporté par cette planète.
	 *
	 * @return le bonus de chips
	 */
	public int getBonusChips() {
		return bonusChips;
	}

	/**
	 * Retourne le bonus de multiplicateur apporté par cette planète.
	 *
	 * @return le bonus de multiplicateur
	 */
	public int getBonusMult() {
		return bonusMult;
	}

	/**
	 * Retourne une planète choisie aléatoirement parmi les 9 disponibles. Appelée
	 * par {@code GameController} à chaque blind battu.
	 *
	 * @return une planète aléatoire
	 */
	public static Planet random() {
		Planet[] planets = Planet.values();
		return planets[new Random().nextInt(planets.length)];
	}

	/**
	 * Retourne le label d'affichage de la planète.
	 *
	 * @return le label d'affichage
	 */
	@Override
	public String toString() {
		return label;
	}
}