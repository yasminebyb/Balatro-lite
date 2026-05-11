package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente une main composée de exactement 5 cartes.
 * <p>
 * Une fois créée, la main est immuable : les cartes ne peuvent pas être
 * modifiées. La combinaison ({@link HandRank}) est détectée automatiquement à
 * la création via {@link HandEvaluator}.
 * </p>
 *
 * @see HandRank
 * @see HandEvaluator
 */
public final class Hand {

	/** Les 5 cartes de la main, immuables. */
	private final List<Card> cards;

	/** La combinaison de poker détectée automatiquement à la création. */
	private final HandRank handRank;

	/**
	 * Crée une main de poker à partir d'une liste de 5 cartes.
	 * <p>
	 * La combinaison est détectée automatiquement via {@link HandEvaluator}. Une
	 * copie défensive de la liste est effectuée pour garantir l'immuabilité.
	 * </p>
	 *
	 * @param cards la liste de 5 cartes à jouer
	 * @throws NullPointerException     si {@code cards} est null ou contient un
	 *                                  élément null
	 * @throws IllegalArgumentException si la liste ne contient pas exactement 5
	 *                                  cartes
	 */
	public Hand(List<Card> cards) {
		Objects.requireNonNull(cards, "cards must not be null");
		if (cards.size() != 5) {
			throw new IllegalArgumentException("A hand must contain exactly 5 cards");
		}
		if (cards.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Cards must not contain null");
		}
		this.cards = Collections.unmodifiableList(new ArrayList<>(cards));
		this.handRank = HandEvaluator.evaluate(this.cards);
	}

	/**
	 * Retourne la liste immuable des 5 cartes de la main.
	 *
	 * @return les cartes de la main
	 */
	public List<Card> getCards() {
		return cards;
	}

	/**
	 * Retourne la combinaison détectée pour cette main.
	 *
	 * @return le {@link HandRank} correspondant
	 */
	public HandRank getHandRank() {
		return handRank;
	}

	/**
	 * Retourne une représentation textuelle de la main.
	 * <p>
	 * Format : {@code [cartes] -> combinaison}
	 * </p>
	 *
	 * @return la représentation textuelle
	 */
	@Override
	public String toString() {
		return cards + " -> " + handRank;
	}
}