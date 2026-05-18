package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class HandTest {

    private static Card c(Rank r, Suit s) {
        return new Card(r, s);
    }

    // ===================== CRÉATION VALIDE =====================

    @Test
    void valid_hand_calculates_rank_on_creation() {
        var cards = List.of(
            c(Rank.ACE,   Suit.SPADES),
            c(Rank.KING,  Suit.SPADES),
            c(Rank.QUEEN, Suit.SPADES),
            c(Rank.JACK,  Suit.SPADES),
            c(Rank.TEN,   Suit.SPADES)
        );
        var hand = new Hand(cards);
        
        assertEquals(5, hand.getCards().size());
        assertEquals(HandRank.STRAIGHT_FLUSH, hand.getHandRank());
    }

    // ===================== IMMUABILITÉ =====================

    @Test
    void getCards_returns_unmodifiable_list() {
        var cards = List.of(
            c(Rank.TWO, Suit.HEARTS), c(Rank.THREE, Suit.HEARTS),
            c(Rank.FOUR, Suit.HEARTS), c(Rank.FIVE, Suit.HEARTS),
            c(Rank.SIX, Suit.HEARTS)
        );
        var hand = new Hand(cards);
     
        assertThrows(UnsupportedOperationException.class, () -> 
            hand.getCards().clear()
        );
    }

    // ===================== VALIDATIONS (NULL & TAILLE) =====================

    @Test
    void constructor_null_throws_NPE() {
        assertThrows(NullPointerException.class, () -> new Hand(null));
    }

    @Test
    void constructor_wrong_size_throws_IAE() {
        var tooFew = List.of(c(Rank.ACE, Suit.HEARTS));
        assertThrows(IllegalArgumentException.class, () -> new Hand(tooFew));

        var tooMany = List.of(
            c(Rank.TWO, Suit.HEARTS), c(Rank.THREE, Suit.HEARTS),
            c(Rank.FOUR, Suit.HEARTS), c(Rank.FIVE, Suit.HEARTS),
            c(Rank.SIX, Suit.HEARTS), c(Rank.SEVEN, Suit.HEARTS)
        );
        assertThrows(IllegalArgumentException.class, () -> new Hand(tooMany));
    }
}