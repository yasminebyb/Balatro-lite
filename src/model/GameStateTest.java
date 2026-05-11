package model;

import domain.Blind;
import domain.HandRank;
import domain.Planet;
import domain.StandardBlind;

import java.util.List;

public class GameStateTest {

	public static void main(String[] args) {

		List<Blind> blinds = List.of(new StandardBlind("Petit aveugle", 300), new StandardBlind("Grand aveugle", 800),
				new StandardBlind("Boss", 2000));

		// ===================== CONSTRUCTEUR =====================
		IO.println("=== TEST CONSTRUCTEUR ===");
		GameState state = new GameState(blinds, 4);
		IO.println(state); // Petit aveugle (cible : 300) | Score: 0 | Mains: 4
		IO.println(state.getCurrentBlind().name().equals("Petit aveugle")); // true
		IO.println(state.getCurrentBlind().targetScore() == 300); // true
		IO.println(state.getCurrentScore() == 0); // true
		IO.println(state.getHandsRemaining() == 4); // true
		IO.println(state.getDeck() != null); // true

		// null safety
		try {
			new GameState(null, 4);
			IO.println("ERREUR");
		} catch (NullPointerException e) {
			IO.println("OK — GameState(null) lève NullPointerException");
		}

		// liste vide
		try {
			new GameState(List.of(), 4);
			IO.println("ERREUR");
		} catch (IllegalArgumentException e) {
			IO.println("OK — GameState(vide) lève IllegalArgumentException");
		}

		// handsPerBlind invalide
		try {
			new GameState(blinds, 0);
			IO.println("ERREUR");
		} catch (IllegalArgumentException e) {
			IO.println("OK — GameState(handsPerBlind=0) lève IllegalArgumentException");
		}
		try {
			new GameState(blinds, -1);
			IO.println("ERREUR");
		} catch (IllegalArgumentException e) {
			IO.println("OK — GameState(handsPerBlind=-1) lève IllegalArgumentException");
		}

		// ===================== LEVELS INITIAUX =====================
		IO.println("\n=== TEST LEVELS INITIAUX ===");
		// vérifie que les valeurs de base sont bien celles de HandRank
		for (HandRank hr : HandRank.values()) {
			boolean chipsOk = state.getChips(hr) == hr.getBaseChips();
			boolean multOk = state.getMult(hr) == hr.getBaseMult();
			IO.println(hr.getLabel() + " chips=" + state.getChips(hr) + (chipsOk ? " OK" : " Pas OK") + " mult="
					+ state.getMult(hr) + (multOk ? " OK" : " Pas OK"));
		}

		// ===================== ADDSCORE =====================
		IO.println("\n=== TEST ADDSCORE ===");
		state.addScore(150);
		IO.println(state.getCurrentScore() == 150); // true
		IO.println(state.isBlindBeaten()); // false — 150 < 300

		state.addScore(100);
		IO.println(state.getCurrentScore() == 250); // true
		IO.println(state.isBlindBeaten()); // false — 250 < 300

		state.addScore(100);
		IO.println(state.getCurrentScore() == 350); // true
		IO.println(state.isBlindBeaten()); // true — 350 >= 300

		// score négatif
		try {
			state.addScore(-10);
			IO.println("ERREUR");
		} catch (IllegalArgumentException e) {
			IO.println("OK — addScore(-10) lève IllegalArgumentException");
		}

		// ===================== DECREMENTHANDS =====================
		IO.println("\n=== TEST DECREMENTHANDS ===");
		GameState state2 = new GameState(blinds, 4);
		IO.println(state2.getHandsRemaining() == 4); // true
		state2.decrementHands();
		IO.println(state2.getHandsRemaining() == 3); // true
		state2.decrementHands();
		state2.decrementHands();
		state2.decrementHands();
		IO.println(state2.getHandsRemaining() == 0); // true
		IO.println(state2.hasHandsRemaining()); // false

		// plus de mains disponibles
		try {
			state2.decrementHands();
			IO.println("ERREUR");
		} catch (IllegalStateException e) {
			IO.println("OK — decrementHands() lève IllegalStateException");
		}

		// ===================== GAMEOVER =====================
		IO.println("\n=== TEST GAMEOVER ===");
		GameState state3 = new GameState(blinds, 2);
		IO.println(state3.isGameOver()); // false — mains restantes
		state3.decrementHands();
		state3.decrementHands();
		IO.println(state3.isGameOver()); // true — 0 mains, score < cible

		// ===================== APPLYPLANET =====================
		IO.println("\n=== TEST APPLYPLANET ===");
		GameState state4 = new GameState(blinds, 4);

		// valeurs avant planète
		IO.println(state4.getChips(HandRank.FLUSH) == 35); // true
		IO.println(state4.getMult(HandRank.FLUSH) == 4); // true

		// application de Jupiter (+15 chips, +2 mult sur FLUSH)
		state4.applyPlanet(Planet.JUPITER);
		IO.println(state4.getChips(HandRank.FLUSH) == 50); // true — 35+15
		IO.println(state4.getMult(HandRank.FLUSH) == 6); // true — 4+2

		// deuxième application — les bonus s'accumulent
		state4.applyPlanet(Planet.JUPITER);
		IO.println(state4.getChips(HandRank.FLUSH) == 65); // true — 50+15
		IO.println(state4.getMult(HandRank.FLUSH) == 8); // true — 6+2

		// toutes les planètes
		GameState state5 = new GameState(blinds, 4);
		for (Planet p : Planet.values()) {
			int chipsBefore = state5.getChips(p.getTarget());
			int multBefore = state5.getMult(p.getTarget());
			state5.applyPlanet(p);
			IO.println(p.getLabel() + " chips: " + chipsBefore + " -> " + state5.getChips(p.getTarget()) + " mult: "
					+ multBefore + " -> " + state5.getMult(p.getTarget()));
		}

		// null safety
		try {
			state4.applyPlanet(null);
			IO.println("ERREUR");
		} catch (NullPointerException e) {
			IO.println("OK — applyPlanet(null) lève NullPointerException");
		}

		// ===================== NEXTBLIND =====================
		IO.println("\n=== TEST NEXTBLIND ===");
		GameState state6 = new GameState(blinds, 4);
		IO.println(state6.getCurrentBlind().name().equals("Petit aveugle")); // true

		state6.addScore(350); // bat le blind
		state6.nextBlind(4);
		IO.println(state6.getCurrentBlind().name().equals("Grand aveugle")); // true
		IO.println(state6.getCurrentScore() == 0); // true — remis à zéro
		IO.println(state6.getHandsRemaining() == 4); // true — remis à 4

		state6.nextBlind(4);
		IO.println(state6.getCurrentBlind().name().equals("Boss")); // true

		// handsPerBlind invalide
		try {
			state6.nextBlind(0);
			IO.println("ERREUR");
		} catch (IllegalArgumentException e) {
			IO.println("OK — nextBlind(0) lève IllegalArgumentException");
		}

		// ===================== ISGAMEWON =====================
		IO.println("\n=== TEST ISGAMEWON ===");
		GameState state7 = new GameState(blinds, 4);
		IO.println(state7.isGameWon()); // false — on est au blind 0

		state7.nextBlind(4);
		IO.println(state7.isGameWon()); // false — on est au blind 1

		state7.nextBlind(4);
		IO.println(state7.isGameWon()); // false — on est au blind 2

		state7.nextBlind(4);
		IO.println(state7.isGameWon()); // true — on a dépassé le dernier blind

		// ===================== GETTERS HANDRANK =====================
		IO.println("\n=== TEST GETTERS HANDRANK ===");
		GameState state8 = new GameState(blinds, 4);

		// null safety
		try {
			state8.getChips(null);
			IO.println("ERREUR");
		} catch (NullPointerException e) {
			IO.println("OK — getChips(null) lève NullPointerException");
		}
		try {
			state8.getMult(null);
			IO.println("ERREUR");
		} catch (NullPointerException e) {
			IO.println("OK — getMult(null) lève NullPointerException");
		}
	}
}