package domain;

import java.util.Objects;

/**
 * Blind standard sans contrainte particulière.
 * <p>
 * Implémentation de base de {@link Blind} : un nom et un score cible. C'est le
 * type de blind utilisé dans la phase 1 du projet.
 * </p>
 *
 * @param name        le nom du blind, non null et non vide
 * @param targetScore le score cible à atteindre, strictement positif
 * @see Blind
 */
public record StandardBlind(String name, int targetScore) implements Blind {

	/**
	 * Constructeur compact : vérifie la validité des paramètres.
	 *
	 * @throws NullPointerException     si {@code name} est null
	 * @throws IllegalArgumentException si {@code name} est vide ou si
	 *                                  {@code targetScore} est négatif ou nul
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
	 * Retourne une représentation textuelle du blind. Format :
	 * {@code "nom (cible : score)"} Exemple : {@code "blind1 (cible : 300)"}
	 *
	 * @return la représentation textuelle
	 */
	@Override
	public String toString() {
		return name + " (cible : " + targetScore + ")";
	}
}