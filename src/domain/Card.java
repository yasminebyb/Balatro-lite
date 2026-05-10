package domain;

import java.util.Objects;

/**
 * Représente une carte à jouer.
 * <p>
 * Une carte est définie par son rang ({@link Rank}) et son enseigne
 * ({@link Suit}). Deux cartes sont égales si elles ont le même rang et la même
 * enseigne.
 * </p>
 *
 * @param rank le rang de la carte, non null
 * @param suit l'enseigne de la carte, non null
 * @see Rank
 * @see Suit
 */
public record Card(Rank rank, Suit suit) {

	/**
	 * Constructeur compact : vérifie que rank et suit ne sont pas null.
	 *
	 * @throws NullPointerException si {@code rank} ou {@code suit} est null
	 */
	public Card {
		Objects.requireNonNull(rank, "rank must not be null");
		Objects.requireNonNull(suit, "suit must not be null");
	}

	/**
	 * Retourne la représentation textuelle de la carte. Format : rang + enseigne,
	 * ex: {@code A♥}, {@code R♠}, {@code 10♦}
	 *
	 * @return la représentation textuelle
	 */
	@Override
	public String toString() {
		return rank + "" + suit;
	}
}