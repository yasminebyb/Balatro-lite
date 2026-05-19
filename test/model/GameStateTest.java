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
            new StandardBlind("Boss", 2000)
        ), 4);
    }

    // ===================== CONSTRUCTEUR =====================

    @Test
    void constructor_initialScore_isZero() {
        assertEquals(0, state.getCurrentScore());
    }

    @Test
    void constructor_initialHands_isFour() {
        assertEquals(4, state.getHandsRemaining());
    }

    @Test
    void constructor_firstBlind_isPetitAveugle() {
        assertEquals("Petit aveugle", state.getCurrentBlind().name());
    }

    @Test
    void constructor_nullBlinds_throwsNPE() {
        assertThrows(NullPointerException.class, () ->
            new GameState(null, 4)
        );
    }

    @Test
    void constructor_emptyBlinds_throwsIAE() {
        assertThrows(IllegalArgumentException.class, () ->
            new GameState(List.of(), 4)
        );
    }

    @Test
    void constructor_invalidHands_throwsIAE() {
        assertThrows(IllegalArgumentException.class, () ->
            new GameState(List.of(new StandardBlind("test", 100)), 0)
        );
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
            assertEquals(hr.getBaseMult(),  state.getMult(hr));
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
        assertThrows(IllegalArgumentException.class, () ->
            state.addScore(-10)
        );
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
        assertEquals(3, state.getHandsRemaining());
    }

    @Test
    void decrementHands_toZero() {
        state.decrementHands();
        state.decrementHands();
        state.decrementHands();
        state.decrementHands();
        assertEquals(0, state.getHandsRemaining());
    }

    @Test
    void decrementHands_belowZero_throwsISE() {
        state.decrementHands();
        state.decrementHands();
        state.decrementHands();
        state.decrementHands();
        assertThrows(IllegalStateException.class, () ->
            state.decrementHands()
        );
    }

    // ===================== ISGAMEOVER =====================

    @Test
    void isGameOver_false_whenHandsRemaining() {
        assertFalse(state.isGameOver());
    }

    @Test
    void isGameOver_true_whenNoHandsAndBlindNotBeaten() {
        state.decrementHands();
        state.decrementHands();
        state.decrementHands();
        state.decrementHands();
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
        assertEquals(8,  state.getMult(HandRank.FLUSH));  // 4+2+2
    }

    @Test
    void applyPlanet_null_throwsNPE() {
        assertThrows(NullPointerException.class, () ->
            state.applyPlanet(null)
        );
    }

    // ===================== NEXTBLIND =====================

    @Test
    void nextBlind_resetsScore() {
        state.addScore(350);
        state.nextBlind(4);
        assertEquals(0, state.getCurrentScore());
    }

    @Test
    void nextBlind_resetsHands() {
        state.decrementHands();
        state.nextBlind(4);
        assertEquals(4, state.getHandsRemaining());
    }

    @Test
    void nextBlind_advancesToNextBlind() {
        state.nextBlind(4);
        assertEquals("Grand aveugle", state.getCurrentBlind().name());
    }

    @Test
    void nextBlind_invalidHands_throwsIAE() {
        assertThrows(IllegalArgumentException.class, () ->
            state.nextBlind(0)
        );
    }

    // ===================== ISGAMEWON =====================

    @Test
    void isGameWon_false_atStart() {
        assertFalse(state.isGameWon());
    }

    @Test
    void isGameWon_true_afterAllBlinds() {
        state.nextBlind(4);
        state.nextBlind(4);
        state.nextBlind(4);
        assertTrue(state.isGameWon());
    }
}