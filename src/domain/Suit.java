package domain;

import java.util.Objects;

public enum Suit {
    CLUBS("\u2663"), DIAMONDS("\u2666"),
    HEARTS("\u2665"), SPADES("\u2660");

    private final String symbol;

    Suit(String symbol) {
        this.symbol = Objects.requireNonNull(symbol);
    }

    public String getSymbol() { return symbol; }

    @Override
    public String toString() { return symbol; }
}