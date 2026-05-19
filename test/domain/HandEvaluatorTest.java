package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class HandEvaluatorTest {

    // ===================== HELPERS =====================

    private static Card c(Rank r, Suit s) {
        return new Card(r, s);
    }

    // ===================== QUINTE FLUSH =====================

    @Test
    void straightFlush_7_to_J() {
        assertEquals(HandRank.STRAIGHT_FLUSH, HandEvaluator.evaluate(List.of(
            c(Rank.SEVEN, Suit.SPADES), c(Rank.EIGHT,  Suit.SPADES),
            c(Rank.NINE,  Suit.SPADES), c(Rank.TEN,    Suit.SPADES),
            c(Rank.JACK,  Suit.SPADES)
        )));
    }

    @Test
    void straightFlush_royal_10_to_A() {
        assertEquals(HandRank.STRAIGHT_FLUSH, HandEvaluator.evaluate(List.of(
            c(Rank.TEN,   Suit.HEARTS), c(Rank.JACK,  Suit.HEARTS),
            c(Rank.QUEEN, Suit.HEARTS), c(Rank.KING,  Suit.HEARTS),
            c(Rank.ACE,   Suit.HEARTS)
        )));
    }

    // ===================== CARRÉ =====================

    @Test
    void fourOfAKind_aces() {
        assertEquals(HandRank.FOUR_OF_A_KIND, HandEvaluator.evaluate(List.of(
            c(Rank.ACE, Suit.SPADES),   c(Rank.ACE, Suit.HEARTS),
            c(Rank.ACE, Suit.DIAMONDS), c(Rank.ACE, Suit.CLUBS),
            c(Rank.KING, Suit.SPADES)
        )));
    }

    @Test
    void fourOfAKind_twos() {
        assertEquals(HandRank.FOUR_OF_A_KIND, HandEvaluator.evaluate(List.of(
            c(Rank.TWO, Suit.SPADES),   c(Rank.TWO, Suit.HEARTS),
            c(Rank.TWO, Suit.DIAMONDS), c(Rank.TWO, Suit.CLUBS),
            c(Rank.KING, Suit.SPADES)
        )));
    }

    // ===================== FULL =====================

    @Test
    void fullHouse_aces_over_kings() {
        assertEquals(HandRank.FULL_HOUSE, HandEvaluator.evaluate(List.of(
            c(Rank.ACE,  Suit.HEARTS), c(Rank.ACE,  Suit.SPADES),
            c(Rank.ACE,  Suit.CLUBS),  c(Rank.KING, Suit.HEARTS),
            c(Rank.KING, Suit.SPADES)
        )));
    }

    @Test
    void fullHouse_not_confused_with_threeOfAKind() {
        HandRank result = HandEvaluator.evaluate(List.of(
            c(Rank.ACE,  Suit.HEARTS), c(Rank.ACE,  Suit.SPADES),
            c(Rank.ACE,  Suit.CLUBS),  c(Rank.KING, Suit.HEARTS),
            c(Rank.KING, Suit.SPADES)
        ));
        assertNotEquals(HandRank.THREE_OF_A_KIND, result);
    }

    // ===================== COULEUR =====================

    @Test
    void flush_all_hearts() {
        assertEquals(HandRank.FLUSH, HandEvaluator.evaluate(List.of(
            c(Rank.TWO,  Suit.HEARTS), c(Rank.SIX,  Suit.HEARTS),
            c(Rank.NINE, Suit.HEARTS), c(Rank.JACK, Suit.HEARTS),
            c(Rank.ACE,  Suit.HEARTS)
        )));
    }

    @Test
    void flush_all_spades() {
        assertEquals(HandRank.FLUSH, HandEvaluator.evaluate(List.of(
            c(Rank.TWO,   Suit.SPADES), c(Rank.FIVE,  Suit.SPADES),
            c(Rank.EIGHT, Suit.SPADES), c(Rank.JACK,  Suit.SPADES),
            c(Rank.KING,  Suit.SPADES)
        )));
    }

    // ===================== SUITE =====================

    @Test
    void straight_5_to_9() {
        assertEquals(HandRank.STRAIGHT, HandEvaluator.evaluate(List.of(
            c(Rank.FIVE,  Suit.HEARTS),   c(Rank.SIX,   Suit.SPADES),
            c(Rank.SEVEN, Suit.DIAMONDS), c(Rank.EIGHT, Suit.CLUBS),
            c(Rank.NINE,  Suit.HEARTS)
        )));
    }

    @Test
    void straight_10_to_A_ace_high() {
        assertEquals(HandRank.STRAIGHT, HandEvaluator.evaluate(List.of(
            c(Rank.TEN,   Suit.HEARTS),   c(Rank.JACK,  Suit.SPADES),
            c(Rank.QUEEN, Suit.DIAMONDS), c(Rank.KING,  Suit.CLUBS),
            c(Rank.ACE,   Suit.HEARTS)
        )));
    }

    @Test
    void straight_A_to_5_ace_low_wheel() {
        assertEquals(HandRank.STRAIGHT, HandEvaluator.evaluate(List.of(
            c(Rank.ACE,   Suit.HEARTS),   c(Rank.TWO,  Suit.CLUBS),
            c(Rank.THREE, Suit.DIAMONDS), c(Rank.FOUR, Suit.SPADES),
            c(Rank.FIVE,  Suit.HEARTS)
        )));
    }

    // ===================== BRELAN =====================

    @Test
    void threeOfAKind_queens() {
        assertEquals(HandRank.THREE_OF_A_KIND, HandEvaluator.evaluate(List.of(
            c(Rank.QUEEN, Suit.HEARTS), c(Rank.QUEEN, Suit.SPADES),
            c(Rank.QUEEN, Suit.CLUBS),  c(Rank.TWO,   Suit.HEARTS),
            c(Rank.EIGHT, Suit.SPADES)
        )));
    }

    // ===================== DOUBLE PAIRE =====================

    @Test
    void twoPair_kings_and_fours() {
        assertEquals(HandRank.TWO_PAIR, HandEvaluator.evaluate(List.of(
            c(Rank.KING, Suit.HEARTS), c(Rank.KING, Suit.SPADES),
            c(Rank.FOUR, Suit.CLUBS),  c(Rank.FOUR, Suit.HEARTS),
            c(Rank.NINE, Suit.SPADES)
        )));
    }

    @Test
    void twoPair_not_confused_with_pair() {
        HandRank result = HandEvaluator.evaluate(List.of(
            c(Rank.KING, Suit.HEARTS), c(Rank.KING, Suit.SPADES),
            c(Rank.FOUR, Suit.CLUBS),  c(Rank.FOUR, Suit.HEARTS),
            c(Rank.NINE, Suit.SPADES)
        ));
        assertNotEquals(HandRank.PAIR, result);
    }

    // ===================== PAIRE =====================

    @Test
    void pair_aces() {
        assertEquals(HandRank.PAIR, HandEvaluator.evaluate(List.of(
            c(Rank.ACE,  Suit.HEARTS), c(Rank.ACE,  Suit.SPADES),
            c(Rank.TWO,  Suit.CLUBS),  c(Rank.FIVE, Suit.DIAMONDS),
            c(Rank.NINE, Suit.HEARTS)
        )));
    }

    // ===================== CARTE HAUTE =====================

    @Test
    void highCard_no_combination() {
        assertEquals(HandRank.HIGH_CARD, HandEvaluator.evaluate(List.of(
            c(Rank.TWO,   Suit.HEARTS),  c(Rank.FIVE,  Suit.SPADES),
            c(Rank.EIGHT, Suit.CLUBS),   c(Rank.JACK,  Suit.DIAMONDS),
            c(Rank.ACE,   Suit.HEARTS)
        )));
    }

    // ===================== NULL SAFETY =====================

    @Test
    void evaluate_null_throws_NPE() {
        assertThrows(NullPointerException.class, () ->
            HandEvaluator.evaluate(null)
        );
    }

    @Test
    void evaluate_wrong_size_throws_IAE() {
        assertThrows(IllegalArgumentException.class, () ->
            HandEvaluator.evaluate(List.of(
                c(Rank.ACE, Suit.HEARTS),
                c(Rank.TWO, Suit.CLUBS)
            ))
        );
    }

    @Test
    void evaluate_too_many_cards_throws_IAE() {
        assertThrows(IllegalArgumentException.class, () ->
            HandEvaluator.evaluate(List.of(
                c(Rank.ACE,   Suit.HEARTS), c(Rank.TWO,  Suit.CLUBS),
                c(Rank.THREE, Suit.DIAMONDS), c(Rank.FOUR, Suit.SPADES),
                c(Rank.FIVE,  Suit.HEARTS), c(Rank.SIX,  Suit.CLUBS)
            ))
        );
    }
}