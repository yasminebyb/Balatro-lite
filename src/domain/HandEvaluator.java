package domain;

import java.util.List;

public final class HandEvaluator {

    private HandEvaluator() {}

    public static HandRank evaluate(List<Card> cards) {
    	/*
        boolean flush = isFlush(cards);
        boolean straight = isStraight(cards);

        if (flush && straight)       return HandRank.STRAIGHT_FLUSH;
        if (isFourOfAKind(cards))    return HandRank.FOUR_OF_A_KIND;
        if (isFullHouse(cards))      return HandRank.FULL_HOUSE;
        if (flush)                   return HandRank.FLUSH;
        if (straight)                return HandRank.STRAIGHT;
        if (isThreeOfAKind(cards))   return HandRank.THREE_OF_A_KIND;
        if (isTwoPair(cards))        return HandRank.TWO_PAIR;
        if (isPair(cards))           return HandRank.PAIR;
        return HandRank.HIGH_CARD;
        */
    	return null; 
    }
}