package domain;

import java.util.Objects;

/**
 * Blind standard sans contrainte particulière.
 *
 * Implémentation de base de {@link Blind} : un nom et un score cible.
 *
 * @param name        le nom du blind, non null et non vide
 * @param targetScore le score cible à atteindre, strictement positif
 * @see Blind
 */
public record StandardBlind(String name, int targetScore) implements Blind {

	/**
	 * @throws NullPointerException     si {@code name} est null
	 * @throws IllegalArgumentException si {@code name} est vide ou si
	 *                                  {@code targetScore} est inférieur ou égal à
	 *                                  zéro
	 */
	public StandardBlind {
		Objects.requireNonNull(name, "name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("name must not be blank");
		}
		if (targetScore <= 0) {
			throw new IllegalArgumentException("targetScore must be positive");
		}
	}

	/**
	 * @return Resprésentation textuelle du blind. ex :
	 *         {@code "blind1 (cible : 300)"}
	 */
	@Override
	public String toString() {
		return name + " (cible : " + targetScore + ")";
	}
}