package model;

import domain.HandRank;
import domain.Planet;
import domain.StandardBlind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

	private GameState state;

	@BeforeEach
	void setUp() {
		state = new GameState(List.of(
				new StandardBlind("Petit aveugle", 300),
				new StandardBlind("Grand aveugle", 800),
				new StandardBlind("Boss", 2000)));
	}

	// ===================== CONSTRUCTEUR =====================

	@Test
	void constructor_initialScore_isZero() {
		assertEquals(0, state.getCurrentScore());
	}

	@Test
	void constructor_initialHands_matchesConstant() {
		assertEquals(GameState.HANDS_PER_BLIND, state.getHandsRemaining());
	}

	@Test
	void constructor_firstBlind_isPetitAveugle() {
		assertEquals("Petit aveugle", state.getCurrentBlind().name());
	}

	@Test
	void constructor_nullBlinds_throwsNPE() {
		assertThrows(NullPointerException.class, () -> new GameState(null));
	}

	@Test
	void constructor_emptyBlinds_throwsIAE() {
		assertThrows(IllegalArgumentException.class, () -> new GameState(List.of()));
	}

	// ===================== LEVELS INITIAUX =====================

	@Test
	void initialLevels_flush_chips() {
		assertEquals(35, state.getChips(HandRank.FLUSH));
	}

	@Test
	void initialLevels_flush_mult() {
		assertEquals(4, state.getMult(HandRank.FLUSH));
	}

	@Test
	void initialLevels_straightFlush_chips() {
		assertEquals(100, state.getChips(HandRank.STRAIGHT_FLUSH));
	}

	@Test
	void initialLevels_allHandRanks_matchBaseValues() {
		for (HandRank hr : HandRank.values()) {
			assertEquals(hr.getBaseChips(), state.getChips(hr));
			assertEquals(hr.getBaseMult(), state.getMult(hr));
		}
	}

	// ===================== ADDSCORE =====================

	@Test
	void addScore_increasesCurrentScore() {
		state.addScore(150);
		assertEquals(150, state.getCurrentScore());
	}

	@Test
	void addScore_cumulative() {
		state.addScore(100);
		state.addScore(200);
		assertEquals(300, state.getCurrentScore());
	}

	@Test
	void addScore_negative_throwsIAE() {
		assertThrows(IllegalArgumentException.class, () -> state.addScore(-10));
	}

	// ===================== ISBLINDBEATEN =====================

	@Test
	void isBlindBeaten_false_whenScoreBelowTarget() {
		state.addScore(299);
		assertFalse(state.isBlindBeaten());
	}

	@Test
	void isBlindBeaten_true_whenScoreEqualsTarget() {
		state.addScore(300);
		assertTrue(state.isBlindBeaten());
	}

	@Test
	void isBlindBeaten_true_whenScoreAboveTarget() {
		state.addScore(500);
		assertTrue(state.isBlindBeaten());
	}

	// ===================== DECREMENTHANDS =====================

	@Test
	void decrementHands_reducesHandsRemaining() {
		state.decrementHands();
		assertEquals(GameState.HANDS_PER_BLIND - 1, state.getHandsRemaining());
	}

	@Test
	void decrementHands_toZero() {
		for (int i = 0; i < GameState.HANDS_PER_BLIND; i++) {
			state.decrementHands();
		}
		assertEquals(0, state.getHandsRemaining());
	}

	@Test
	void decrementHands_belowZero_throwsISE() {
		for (int i = 0; i < GameState.HANDS_PER_BLIND; i++) {
			state.decrementHands();
		}
		assertThrows(IllegalStateException.class, () -> state.decrementHands());
	}

	// ===================== ISGAMEOVER =====================

	@Test
	void isGameOver_false_whenHandsRemaining() {
		assertFalse(state.isGameOver());
	}

	@Test
	void isGameOver_true_whenNoHandsAndBlindNotBeaten() {
		for (int i = 0; i < GameState.HANDS_PER_BLIND; i++) {
			state.decrementHands();
		}
		assertTrue(state.isGameOver());
	}

	@Test
	void isGameOver_false_whenBlindBeaten() {
		state.addScore(300);
		assertFalse(state.isGameOver());
	}

	// ===================== APPLYPLANET =====================

	@Test
	void applyPlanet_jupiter_increasesFlushChips() {
		state.applyPlanet(Planet.JUPITER);
		assertEquals(50, state.getChips(HandRank.FLUSH)); // 35+15
	}

	@Test
	void applyPlanet_jupiter_increasesFlushMult() {
		state.applyPlanet(Planet.JUPITER);
		assertEquals(6, state.getMult(HandRank.FLUSH)); // 4+2
	}

	@Test
	void applyPlanet_twice_accumulates() {
		state.applyPlanet(Planet.JUPITER);
		state.applyPlanet(Planet.JUPITER);
		assertEquals(65, state.getChips(HandRank.FLUSH)); // 35+15+15
		assertEquals(8, state.getMult(HandRank.FLUSH)); // 4+2+2
	}

	@Test
	void applyPlanet_null_throwsNPE() {
		assertThrows(NullPointerException.class, () -> state.applyPlanet(null));
	}

	// ===================== NEXTBLIND =====================

	@Test
	void nextBlind_resetsScore() {
		state.addScore(350);
		state.nextBlind();
		assertEquals(0, state.getCurrentScore());
	}

	@Test
	void nextBlind_resetsHands() {
		state.decrementHands();
		state.nextBlind();
		assertEquals(GameState.HANDS_PER_BLIND, state.getHandsRemaining());
	}

	@Test
	void nextBlind_advancesToNextBlind() {
		state.nextBlind();
		assertEquals("Grand aveugle", state.getCurrentBlind().name());
	}

	// ===================== ISGAMEWON =====================

	@Test
	void isGameWon_false_atStart() {
		assertFalse(state.isGameWon());
	}

	@Test
	void isGameWon_true_afterAllBlinds() {
		state.nextBlind();
		state.nextBlind();
		state.nextBlind();
		assertTrue(state.isGameWon());
	}

	// ===================== EXTENSION B — DÉFAUSSES =====================

	@Test
	void initialDiscards_isThree() {
		assertEquals(3, state.getDiscardsRemaining());
	}

	@Test
	void hasDiscardsRemaining_true_atStart() {
		assertTrue(state.hasDiscardsRemaining());
	}

	@Test
	void decrementDiscards_reducesCount() {
		state.decrementDiscards();
		assertEquals(2, state.getDiscardsRemaining());
	}

	@Test
	void decrementDiscards_toZero() {
		state.decrementDiscards();
		state.decrementDiscards();
		state.decrementDiscards();
		assertEquals(0, state.getDiscardsRemaining());
		assertFalse(state.hasDiscardsRemaining());
	}

	@Test
	void decrementDiscards_belowZero_throwsISE() {
		state.decrementDiscards();
		state.decrementDiscards();
		state.decrementDiscards();
		assertThrows(IllegalStateException.class, () -> state.decrementDiscards());
	}

	@Test
	void nextBlind_resetsDiscards() {
		state.decrementDiscards();
		state.decrementDiscards();
		state.nextBlind();
		assertEquals(3, state.getDiscardsRemaining());
	}

	@Test
	void toString_includesDiscards() {
		var result = state.toString();
		assertTrue(result.contains("Défausses"), "toString doit mentionner les défausses");
	}
}