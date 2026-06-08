package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente une main composée de exactement {@value #SIZE} cartes.
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

    /** Nombre de cartes que contient toute main de poker. */
    public static final int SIZE = 5;

    /** Les {@value #SIZE} cartes de la main, immuables. */
    private final List<Card> cards;

    /** La combinaison de poker détectée automatiquement à la création. */
    private final HandRank handRank;

    /**
     * Crée une main de poker à partir d'une liste de {@value #SIZE} cartes.
     * <p>
     * La combinaison est détectée automatiquement via {@link HandEvaluator}.
     * Une copie défensive de la liste est effectuée pour garantir l'immuabilité.
     * </p>
     *
     * @param cards la liste de {@value #SIZE} cartes à jouer, non null
     * @throws NullPointerException     si {@code cards} est null
     * @throws IllegalArgumentException si {@code cards} ne contient pas exactement
     *                                  {@value #SIZE} cartes, ou contient un élément null
     */
    public Hand(List<Card> cards) {
        Objects.requireNonNull(cards, "cards must not be null");
        if (cards.size() != SIZE)
            throw new IllegalArgumentException(
                    "A hand must contain exactly " + SIZE + " cards, got: " + cards.size());
        if (cards.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Cards must not contain null");

        this.cards    = Collections.unmodifiableList(new ArrayList<>(cards));
        this.handRank = HandEvaluator.evaluate(this.cards);
    }

    /**
     * Retourne la liste immuable des {@value #SIZE} cartes de la main.
     *
     * @return les cartes de la main, jamais null
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Retourne la combinaison détectée pour cette main.
     *
     * @return le {@link HandRank} correspondant, jamais null
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