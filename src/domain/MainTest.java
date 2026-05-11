package domain;

import java.util.List;

public class MainTest {
	public static void main(String[] args) {

		// ===================== RANK =====================
		IO.println("=== TEST RANK ===");
		// labels
		IO.println(Rank.TWO.getLabel()); // 2
		IO.println(Rank.JACK.getLabel()); // V
		IO.println(Rank.QUEEN.getLabel()); // D
		IO.println(Rank.KING.getLabel()); // R
		IO.println(Rank.ACE.getLabel()); // A
		IO.println(Rank.TEN.getLabel()); // 10

		// values
		IO.println(Rank.TWO.getValue()); // 2
		IO.println(Rank.NINE.getValue()); // 9
		IO.println(Rank.TEN.getValue()); // 10
		IO.println(Rank.JACK.getValue()); // 10 — figure
		IO.println(Rank.QUEEN.getValue()); // 10 — figure
		IO.println(Rank.KING.getValue()); // 10 — figure
		IO.println(Rank.ACE.getValue()); // 11

		// ordinals — important pour la détection des suites
		IO.println(Rank.TWO.ordinal()); // 0
		IO.println(Rank.THREE.ordinal()); // 1
		IO.println(Rank.TEN.ordinal()); // 8
		IO.println(Rank.JACK.ordinal()); // 9
		IO.println(Rank.QUEEN.ordinal()); // 10
		IO.println(Rank.KING.ordinal()); // 11
		IO.println(Rank.ACE.ordinal()); // 12

		// toString
		IO.println(Rank.ACE.toString()); // A
		IO.println(Rank.JACK.toString()); // V

		// ===================== SUIT =====================
		IO.println("\n=== TEST SUIT ===");
		IO.println(Suit.CLUBS.getSymbol()); // ♣
		IO.println(Suit.DIAMONDS.getSymbol()); // ♦
		IO.println(Suit.HEARTS.getSymbol()); // ♥
		IO.println(Suit.SPADES.getSymbol()); // ♠

		// toString
		IO.println(Suit.HEARTS.toString()); // ♥
		IO.println(Suit.SPADES.toString()); // ♠

		// ===================== CARD =====================
		IO.println("\n=== TEST CARD ===");
		Card c1 = new Card(Rank.ACE, Suit.HEARTS);
		Card c2 = new Card(Rank.KING, Suit.SPADES);
		Card c3 = new Card(Rank.TEN, Suit.DIAMONDS);
		Card c4 = new Card(Rank.TWO, Suit.CLUBS);
		Card c5 = new Card(Rank.JACK, Suit.HEARTS);

		IO.println(c1); // A♥
		IO.println(c2); // R♠
		IO.println(c3); // 10♦
		IO.println(c4); // 2♣
		IO.println(c5); // V♥

		// getters record
		IO.println(c1.rank()); // A
		IO.println(c1.suit()); // ♥

		// equals — deux cartes identiques
		Card c1bis = new Card(Rank.ACE, Suit.HEARTS);
		IO.println(c1.equals(c1bis)); // true
		IO.println(c1.equals(c2)); // false

		// null safety
		try {
			new Card(null, Suit.HEARTS);
			IO.println("ERREUR — aurait dû lever NullPointerException");
		} catch (NullPointerException e) {
			IO.println("OK — Card(null, suit) lève NullPointerException");
		}
		try {
			new Card(Rank.ACE, null);
			IO.println("ERREUR — aurait dû lever NullPointerException");
		} catch (NullPointerException e) {
			IO.println("OK — Card(rank, null) lève NullPointerException");
		}

		// ===================== HANDRANK =====================
		IO.println("\n=== TEST HANDRANK ===");
		// toutes les combinaisons avec leurs valeurs
		for (HandRank hr : HandRank.values()) {
			IO.println(hr.getLabel() + " : " + hr.getBaseChips() + " chips x " + hr.getBaseMult() + " mult = "
					+ (hr.getBaseChips() * hr.getBaseMult()) + " pts");
		}
		// vérifications ciblées du sujet
		IO.println(HandRank.HIGH_CARD.getBaseChips() == 5); // true
		IO.println(HandRank.PAIR.getBaseChips() == 10); // true
		IO.println(HandRank.STRAIGHT_FLUSH.getBaseChips() == 100); // true
		IO.println(HandRank.STRAIGHT_FLUSH.getBaseMult() == 8); // true
		IO.println(HandRank.FOUR_OF_A_KIND.getBaseChips() == 60); // true
		IO.println(HandRank.FOUR_OF_A_KIND.getBaseMult() == 7); // true

		// ===================== HANDEVALUATOR =====================
		IO.println("\n=== TEST HANDEVALUATOR ===");

		// 1. Quinte flush
		test(List.of(new Card(Rank.SEVEN, Suit.SPADES), new Card(Rank.EIGHT, Suit.SPADES),
				new Card(Rank.NINE, Suit.SPADES), new Card(Rank.TEN, Suit.SPADES), new Card(Rank.JACK, Suit.SPADES)),
				HandRank.STRAIGHT_FLUSH);

		// 2. Quinte flush royale (10-V-D-R-A même enseigne)
		test(List.of(new Card(Rank.TEN, Suit.HEARTS), new Card(Rank.JACK, Suit.HEARTS),
				new Card(Rank.QUEEN, Suit.HEARTS), new Card(Rank.KING, Suit.HEARTS), new Card(Rank.ACE, Suit.HEARTS)),
				HandRank.STRAIGHT_FLUSH);

		// 3. Carré
		test(List.of(new Card(Rank.ACE, Suit.SPADES), new Card(Rank.ACE, Suit.HEARTS),
				new Card(Rank.ACE, Suit.DIAMONDS), new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.KING, Suit.SPADES)),
				HandRank.FOUR_OF_A_KIND);

		// 4. Full
		test(List.of(new Card(Rank.ACE, Suit.HEARTS), new Card(Rank.ACE, Suit.SPADES), new Card(Rank.ACE, Suit.CLUBS),
				new Card(Rank.KING, Suit.HEARTS), new Card(Rank.KING, Suit.SPADES)), HandRank.FULL_HOUSE);

		// 5. Couleur
		test(List.of(new Card(Rank.TWO, Suit.HEARTS), new Card(Rank.SIX, Suit.HEARTS), new Card(Rank.NINE, Suit.HEARTS),
				new Card(Rank.JACK, Suit.HEARTS), new Card(Rank.ACE, Suit.HEARTS)), HandRank.FLUSH);

		// 6. Suite normale
		test(List.of(new Card(Rank.FIVE, Suit.HEARTS), new Card(Rank.SIX, Suit.SPADES),
				new Card(Rank.SEVEN, Suit.DIAMONDS), new Card(Rank.EIGHT, Suit.CLUBS),
				new Card(Rank.NINE, Suit.HEARTS)), HandRank.STRAIGHT);

		// 7. Suite A-2-3-4-5 (cas spécial As bas)
		test(List.of(new Card(Rank.ACE, Suit.HEARTS), new Card(Rank.TWO, Suit.CLUBS),
				new Card(Rank.THREE, Suit.DIAMONDS), new Card(Rank.FOUR, Suit.SPADES),
				new Card(Rank.FIVE, Suit.HEARTS)), HandRank.STRAIGHT);

		// 8. Suite 10-V-D-R-A (As haut)
		test(List.of(new Card(Rank.TEN, Suit.HEARTS), new Card(Rank.JACK, Suit.SPADES),
				new Card(Rank.QUEEN, Suit.DIAMONDS), new Card(Rank.KING, Suit.CLUBS), new Card(Rank.ACE, Suit.HEARTS)),
				HandRank.STRAIGHT);

		// 9. Brelan
		test(List.of(new Card(Rank.QUEEN, Suit.HEARTS), new Card(Rank.QUEEN, Suit.SPADES),
				new Card(Rank.QUEEN, Suit.CLUBS), new Card(Rank.TWO, Suit.HEARTS), new Card(Rank.EIGHT, Suit.SPADES)),
				HandRank.THREE_OF_A_KIND);

		// 10. Double paire
		test(List.of(new Card(Rank.KING, Suit.HEARTS), new Card(Rank.KING, Suit.SPADES),
				new Card(Rank.FOUR, Suit.CLUBS), new Card(Rank.FOUR, Suit.HEARTS), new Card(Rank.NINE, Suit.SPADES)),
				HandRank.TWO_PAIR);

		// 11. Paire
		test(List.of(new Card(Rank.ACE, Suit.HEARTS), new Card(Rank.ACE, Suit.SPADES), new Card(Rank.TWO, Suit.CLUBS),
				new Card(Rank.FIVE, Suit.DIAMONDS), new Card(Rank.NINE, Suit.HEARTS)), HandRank.PAIR);

		// 12. Carte haute
		test(List.of(new Card(Rank.TWO, Suit.HEARTS), new Card(Rank.FIVE, Suit.SPADES),
				new Card(Rank.EIGHT, Suit.CLUBS), new Card(Rank.JACK, Suit.DIAMONDS), new Card(Rank.ACE, Suit.HEARTS)),
				HandRank.HIGH_CARD);

		// null safety
		try {
			HandEvaluator.evaluate(null);
			IO.println("ERREUR — aurait dû lever NullPointerException");
		} catch (NullPointerException e) {
			IO.println("OK — evaluate(null) lève NullPointerException");
		}
		try {
			HandEvaluator.evaluate(List.of(c1, c2));
			IO.println("ERREUR — aurait dû lever IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			IO.println("OK — evaluate(2 cartes) lève IllegalArgumentException");
		}

		// ===================== HAND =====================
		IO.println("\n=== TEST HAND ===");
		Hand hand = new Hand(List.of(new Card(Rank.ACE, Suit.HEARTS), new Card(Rank.ACE, Suit.SPADES),
				new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.KING, Suit.HEARTS), new Card(Rank.KING, Suit.SPADES)));
		IO.println(hand); // [A♥, A♠, A♣, R♥, R♠] -> Full
		IO.println(hand.getHandRank()); // Full
		IO.println(hand.getCards().size()); // 5

		// liste immuable
		try {
			hand.getCards().add(new Card(Rank.TWO, Suit.CLUBS));
			IO.println("ERREUR — liste aurait dû être immuable");
		} catch (UnsupportedOperationException e) {
			IO.println("OK — liste immuable");
		}

		// taille incorrecte
		try {
			new Hand(List.of(c1, c2));
			IO.println("ERREUR — aurait dû lever IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			IO.println("OK — Hand(2 cartes) lève IllegalArgumentException");
		}

		// null safety
		try {
			new Hand(null);
			IO.println("ERREUR — aurait dû lever NullPointerException");
		} catch (NullPointerException e) {
			IO.println("OK — Hand(null) lève NullPointerException");
		}

		// ===================== DECK =====================
		IO.println("\n=== TEST DECK ===");
		Deck deck = new Deck();
		IO.println(deck); // Pioche: 52 | Défausse: 0
		IO.println(deck.drawPileSize() == 52); // true
		IO.println(deck.discardPileSize() == 0); // true

		List<Card> drawn = deck.draw(8);
		IO.println(drawn.size() == 8); // true
		IO.println(deck.drawPileSize() == 44); // true

		deck.discard(drawn.subList(5, 8));
		IO.println(deck.discardPileSize() == 3); // true
		IO.println(deck); // Pioche: 44 | Défausse: 3

		// test refill automatique — vider presque toute la pioche
		Deck deck2 = new Deck();
		deck2.draw(48); // reste 4 cartes
		deck2.discard(deck2.draw(4)); // pioche vide, défausse 4
		IO.println(deck2.drawPileSize() == 0); // true
		IO.println(deck2.discardPileSize() == 4);// true
		List<Card> afterRefill = deck2.draw(4); // déclenche refill
		IO.println(afterRefill.size() == 4); // true
		IO.println(deck2.discardPileSize() == 0);// true — défausse vidée

		// count invalide
		try {
			deck.draw(0);
			IO.println("ERREUR — aurait dû lever IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			IO.println("OK — draw(0) lève IllegalArgumentException");
		}

		// ===================== PLANET =====================
		IO.println("\n=== TEST PLANET ===");
		// toutes les planètes
		for (Planet p : Planet.values()) {
			IO.println(p.getLabel() + " -> " + p.getTarget().getLabel() + " | +" + p.getBonusChips() + " chips" + " | +"
					+ p.getBonusMult() + " mult");
		}

		// vérifications ciblées du sujet
		IO.println(Planet.PLUTO.getTarget() == HandRank.HIGH_CARD); // true
		IO.println(Planet.MERCURY.getBonusChips() == 15); // true
		IO.println(Planet.NEPTUNE.getTarget() == HandRank.STRAIGHT_FLUSH); // true
		IO.println(Planet.NEPTUNE.getBonusMult() == 4); // true

		// random — juste vérifier qu'il retourne une planète non null
		Planet random = Planet.random();
		IO.println(random != null); // true

		// ===================== BLIND =====================
		IO.println("\n=== TEST BLIND ===");
		StandardBlind blind = new StandardBlind("Petit aveugle", 300);
		IO.println(blind); // Petit aveugle (cible : 300)
		IO.println(blind.name()); // Petit aveugle
		IO.println(blind.targetScore()); // 300
		IO.println(blind.targetScore() == 300); // true

		// score négatif ou nul
		try {
			new StandardBlind("Test", 0);
			IO.println("ERREUR — aurait dû lever IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			IO.println("OK — Blind(score=0) lève IllegalArgumentException");
		}
		try {
			new StandardBlind("Test", -100);
			IO.println("ERREUR — aurait dû lever IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			IO.println("OK — Blind(score=-100) lève IllegalArgumentException");
		}

		// nom null ou vide
		try {
			new StandardBlind(null, 300);
			IO.println("ERREUR — aurait dû lever NullPointerException");
		} catch (NullPointerException e) {
			IO.println("OK — Blind(null, 300) lève NullPointerException");
		}
		try {
			new StandardBlind("", 300);
			IO.println("ERREUR — aurait dû lever IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			IO.println("OK — Blind('', 300) lève IllegalArgumentException");
		}
	}

	private static void test(List<Card> cards, HandRank expected) {
		HandRank result = HandEvaluator.evaluate(cards);
		String status = result == expected ? "OK" : "ERREUR";
		IO.println(status + " " + cards + " -> " + result);
	}
}