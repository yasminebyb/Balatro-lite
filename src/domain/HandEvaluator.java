package domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Évalue une main de 5 cartes et retourne la combinaison (de pocker)
 * correspondante.
 * <p>
 * Cette classe est un utilitaire statique : elle ne peut pas être instanciée.
 * L'évaluation se fait du rang le plus fort au plus faible pour éviter les faux
 * positifs (ex: un Full contient un Brelan, donc Full doit être testé en
 * premier).
 * </p>
 *
 * @see HandRank
 * @see Hand
 */
public final class HandEvaluator {

	/** Empêche l'instanciation : classe utilitaire statique. */
	private HandEvaluator() {
	}

	/**
	 * Évalue une main de 5 cartes et retourne son {@link HandRank}.
	 * <p>
	 * L'As peut être utilisé comme carte basse (A-2-3-4-5) ou comme carte haute
	 * (10-V-D-R-A).
	 * </p>
	 *
	 * @param cards liste de 5 cartes à évaluer
	 * @return le {@link HandRank} correspondant à la meilleure combinaison
	 * @throws NullPointerException     si {@code cards} est null
	 * @throws IllegalArgumentException si la liste ne contient pas exactement 5
	 *                                  cartes
	 */
	public static HandRank evaluate(List<Card> cards) {
		Objects.requireNonNull(cards, "cards must not be null");
		if (cards.size() != 5) {
			throw new IllegalArgumentException("A hand must contain exactly 5 cards");
		}

		boolean flush = isFlush(cards);
		boolean straight = isStraight(cards);
		Map<Rank, Long> groups = groupByRank(cards);

		if (flush && straight)
			return HandRank.STRAIGHT_FLUSH;
		if (isFourOfAKind(groups))
			return HandRank.FOUR_OF_A_KIND;
		if (isFullHouse(groups))
			return HandRank.FULL_HOUSE;
		if (flush)
			return HandRank.FLUSH;
		if (straight)
			return HandRank.STRAIGHT;
		if (isThreeOfAKind(groups))
			return HandRank.THREE_OF_A_KIND;
		if (isTwoPair(groups))
			return HandRank.TWO_PAIR;
		if (isPair(groups))
			return HandRank.PAIR;
		return HandRank.HIGH_CARD;
	}

	/**
	 * Regroupe les cartes par rang et compte les occurrences de chaque rang.
	 * Exemple : [A♥ A♠ A♣ R♥ R♠] → {ACE=3, KING=2}
	 *
	 * @param cards les cartes à regrouper
	 * @return une map rang → nombre d'occurrences
	 */
	private static Map<Rank, Long> groupByRank(List<Card> cards) {
		return cards.stream().collect(Collectors.groupingBy(Card::rank, Collectors.counting()));
	}

	/**
	 * Vérifie que toutes les cartes ont la même enseigne.
	 *
	 * @param cards les cartes à vérifier
	 * @return {@code true} si toutes les cartes ont la même enseigne
	 */
	private static boolean isFlush(List<Card> cards) {
		return cards.stream().map(Card::suit).distinct().count() == 1;
	}

	/**
	 * Vérifie que les rangs des cartes sont consécutifs.
	 * <p>
	 * Gère le cas particulier A-2-3-4-5 où l'As joue comme carte basse. Repose sur
	 * {@link Rank#ordinal()} : l'ordre de déclaration dans l'enum doit donc rester
	 * TWO(0) ... ACE(12).
	 * </p>
	 *
	 * @param cards les cartes à vérifier
	 * @return {@code true} si les cartes forment une suite
	 */
	private static boolean isStraight(List<Card> cards) {
		List<Integer> ordinals = cards.stream().map(c -> c.rank().ordinal()).sorted().toList();

		// cas normal : écart de 4 entre le plus petit et le plus grand, tous distincts
		boolean normal = ordinals.get(4) - ordinals.get(0) == 4 && ordinals.stream().distinct().count() == 5;

		// cas spécial : A-2-3-4-5 → ordinals [0,1,2,3,12] après tri
		boolean wheel = ordinals.equals(List.of(0, 1, 2, 3, 12));

		return normal || wheel;
	}

	private static boolean isFourOfAKind(Map<Rank, Long> groups) {
		return groups.containsValue(4L);
	}

	private static boolean isFullHouse(Map<Rank, Long> groups) {
		return groups.containsValue(3L) && groups.containsValue(2L);
	}

	private static boolean isThreeOfAKind(Map<Rank, Long> groups) {
		return groups.containsValue(3L);
	}

	private static boolean isTwoPair(Map<Rank, Long> groups) {
		return groups.values().stream().filter(count -> count == 2L).count() == 2;
	}

	private static boolean isPair(Map<Rank, Long> groups) {
		return groups.containsValue(2L);
	}
}