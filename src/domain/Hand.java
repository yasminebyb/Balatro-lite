package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente une main composée exactement de {@value #SIZE} cartes.
 *
 * Une fois créée, la main est immuable. La combinaison ({@link HandRank}) est
 * détectée automatiquement à la création via {@link HandEvaluator}.
 *
 * @see HandRank
 * @see HandEvaluator
 */
public final class Hand {

	/** Nombre de cartes que contient toute main de poker. */
	public static final int SIZE = 5;

	// Les SIZE cartes de la main, immuables
	private final List<Card> cards;

	// La combinaison de poker détectée automatiquement à la création
	private final HandRank handRank;

	/**
	 * Crée une main de poker à partir d'une liste de {@value #SIZE} cartes.
	 *
	 * La combinaison est détectée automatiquement via {@link HandEvaluator}. Une
	 * copie défensive est effectuée pour garantir l'immuabilité.
	 *
	 * @param cards la liste de {@value #SIZE} cartes à jouer, non null
	 * @throws NullPointerException     si {@code cards} est null
	 * @throws IllegalArgumentException si {@code cards} ne contient pas exactement
	 *                                  {@value #SIZE} cartes, ou contient un
	 *                                  élément null
	 */
	public Hand(List<Card> cards) {
		Objects.requireNonNull(cards, "cards must not be null");
		if (cards.size() != SIZE) {
			throw new IllegalArgumentException(
					"A hand must contain exactly " + SIZE + " cards, got: " + cards.size());
		}
		if (cards.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Cards must not contain null");
		}
		this.cards = Collections.unmodifiableList(new ArrayList<>(cards));
		this.handRank = HandEvaluator.evaluate(this.cards);
	}

	/**
	 * @return la liste immuable des {@value #SIZE} cartes, jamais null
	 */
	public List<Card> getCards() {
		return cards;
	}

	/**
	 * @return la combinaison détectée pour cette main, jamais null
	 */
	public HandRank getHandRank() {
		return handRank;
	}

	/**
	 * @return ex : {@code "[A♥, K♠, Q♦, J♣, 10♥] -> STRAIGHT_FLUSH"}
	 */
	@Override
	public String toString() {
		return cards + " -> " + handRank;
	}
}