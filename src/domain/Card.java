package domain;

import java.util.Objects;

public record Card(Rank rank, Suit suit) {

    public Card {
        Objects.requireNonNull(rank);
        Objects.requireNonNull(suit);
    }

    @Override
    public String toString() {
        return rank.getLabel() + suit.getSymbol();
    }
}