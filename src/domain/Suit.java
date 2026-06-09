package domain;

import java.util.Objects;

/**
 * Représente l'enseigne d'une carte à jouer.
 *
 * Les quatre enseignes sont Trèfle, Carreau, Cœur et Pique. Dans Balatri, les
 * enseignes servent uniquement à détecter les couleurs ({@link HandRank#FLUSH})
 * et les quintes flush ({@link HandRank#STRAIGHT_FLUSH}).
 */
public enum Suit {

	CLUBS("\u2663"), // ♣ Trèfle
	DIAMONDS("\u2666"), // ♦ Carreau
	HEARTS("\u2665"), // ♥ Cœur
	SPADES("\u2660"); // ♠ Pique

	// Symbole Unicode de l'enseigne
	private final String symbol;

	/**
	 * @param symbol le symbole Unicode de l'enseigne, non null
	 * @throws NullPointerException si {@code symbol} est null
	 */
	Suit(String symbol) {
		this.symbol = Objects.requireNonNull(symbol, "symbol must not be null");
	}

	/** @return le symbole Unicode, ex : {@code "♥"} pour Cœur */
	public String getSymbol() {
		return symbol;
	}

	/** @return le symbole Unicode */
	@Override
	public String toString() {
		return symbol;
	}
}