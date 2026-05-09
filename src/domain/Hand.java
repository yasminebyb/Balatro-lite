package domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Hand {

    private final List<Card> cards;
    private final HandRank handRank;

    public Hand(List<Card> cards) {
        Objects.requireNonNull(cards);
        if (cards.size() != 5) {
            throw new IllegalArgumentException("A hand must contain exactly 5 cards");
        }
        this.cards = Collections.unmodifiableList(cards);
        this.handRank = HandEvaluator.evaluate(cards);
    }

    public List<Card> getCards() { return cards; }
    public HandRank getHandRank() { return handRank; }

    public int getScore(int chips, int mult) {
        return chips * mult;
    }

    @Override
    public String toString() {
        return cards + " -> " + handRank;
    }
}