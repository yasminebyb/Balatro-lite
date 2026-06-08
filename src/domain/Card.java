package domain;

import java.util.Objects;

/**
 * Représente une carte à jouer, définie par son rang ({@link Rank}) et son
 * enseigne ({@link Suit}).
 *
 * Deux cartes sont égales si elles ont le même rang et la même enseigne.
 *
 * @param rank le rang de la carte, non null
 * @param suit l'enseigne de la carte, non null
 * @see Rank
 * @see Suit
 */

public record Card(Rank rank, Suit suit) {

	/**
	 * Vérifie que les composants de la carte sont non null.
	 *
	 * @throws NullPointerException si {@code rank} ou {@code suit} est null
	 */

	public Card {
		Objects.requireNonNull(rank, "rank must not be null");
		Objects.requireNonNull(suit, "suit must not be null");
	}

	/**
	 * @return la représentation textuelle rang + enseigne, ex : {@code A♥},
	 *         {@code R♠}, {@code 10♦}
	 */

	@Override
	public String toString() {
		return rank + "" + suit;
	}
}