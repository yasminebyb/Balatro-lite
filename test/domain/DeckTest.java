package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

	// ===================== INITIALISATION =====================

	@Test
	void newDeck_contains_52_cards() {
		var deck = new Deck();
		assertEquals(52, deck.drawPileSize());
		assertEquals(0, deck.discardPileSize());
	}

	// ===================== PIOCHE (DRAW) =====================

	@Test
	void draw_reduces_drawPile_size() {
		var deck = new Deck();
		var drawn = deck.draw(8);

		assertEquals(8, drawn.size());
		assertEquals(44, deck.drawPileSize());
	}

	@Test
	void draw_negative_or_zero_throws_IAE() {
		var deck = new Deck();
		assertThrows(IllegalArgumentException.class, () -> deck.draw(0));
		assertThrows(IllegalArgumentException.class, () -> deck.draw(-5));
	}

	// ===================== DÉFAUSSE (DISCARD) =====================

	@Test
	void discard_increases_discardPile_size() {
		var deck = new Deck();
		var drawn = deck.draw(5);
		deck.discard(drawn);

		assertEquals(47, deck.drawPileSize());
		assertEquals(5, deck.discardPileSize());
	}

	// ===================== REMÉLANGE AUTOMATIQUE =====================

	@Test
	void draw_triggers_reshuffle_when_drawPile_is_empty() {
		var deck = new Deck();
		var cards1 = deck.draw(50);
		deck.discard(cards1);

		assertEquals(2, deck.drawPileSize());
		assertEquals(50, deck.discardPileSize());
		var cards2 = deck.draw(8);

		assertEquals(8, cards2.size());
		assertEquals(44, deck.drawPileSize());
		assertEquals(0, deck.discardPileSize());
	}

	@Test
	void draw_more_than_total_cards_throws_ISE() {
		var deck = new Deck();
		assertThrows(IllegalStateException.class, () -> deck.draw(60));
	}
}