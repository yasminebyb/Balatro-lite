package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente le paquet de cartes du jeu.
 *
 * Gère deux piles : la pioche ({@code drawPile}) depuis laquelle le joueur tire
 * ses cartes, et la défausse ({@code discardPile}) où vont les cartes non
 * jouées. Lorsque la pioche est insuffisante, la défausse est automatiquement
 * remélangée et réintégrée.
 */
public final class Deck {

	// Cartes disponibles à piocher
	private final List<Card> drawPile;

	// Cartes défaussées, réintégrées dans la pioche si nécessaire
	private final List<Card> discardPile;

	/**
	 * Crée un paquet de 52 cartes mélangées aléatoirement.
	 */
	public Deck() {
		drawPile = new ArrayList<>(52);
		discardPile = new ArrayList<>(52);
		initializeDeck();
		shuffle();
	}

	/**
	 * Génère les 52 cartes (13 rangs × 4 enseignes) et les place dans la pioche.
	 */
	private void initializeDeck() {
		for (Rank rank : Rank.values()) {
			for (Suit suit : Suit.values()) {
				drawPile.add(new Card(rank, suit));
			}
		}
	}

	/**
	 * Mélange aléatoirement la pioche.
	 */
	private void shuffle() {
		Collections.shuffle(drawPile);
	}

	/**
	 * Pioche {@code count} cartes depuis la pioche.
	 *
	 * Si la pioche ne contient pas assez de cartes, la défausse est automatiquement
	 * remélangée et réintégrée avant de piocher.
	 *
	 * @param count le nombre de cartes à piocher, strictement positif
	 * @return une liste immuable de {@code count} cartes piochées
	 * @throws IllegalArgumentException si {@code count} est négatif ou nul
	 * @throws IllegalStateException    si le paquet ne contient toujours pas assez
	 *                                  de cartes après réintégration
	 */
	public List<Card> draw(int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("Count must be > 0, got: " + count);
		}
		if (drawPile.size() < count) {
			refill();
		}
		if (drawPile.size() < count) {
			throw new IllegalStateException("Not enough cards in the deck");
		}
		var startIndex = drawPile.size() - count;
		var view = drawPile.subList(startIndex, drawPile.size());
		List<Card> drawn = List.copyOf(view);
		view.clear();
		return drawn;
	}

	/**
	 * Place les cartes données en défausse.
	 *
	 * @param cards les cartes à défausser, non null
	 * @throws NullPointerException si {@code cards} est null
	 */
	public void discard(List<Card> cards) {
		Objects.requireNonNull(cards, "cards must not be null");
		discardPile.addAll(cards);
	}

	/**
	 * Réintègre la défausse dans la pioche et mélange. Appelé automatiquement par
	 * {@link #draw} si nécessaire.
	 */
	private void refill() {
		drawPile.addAll(discardPile);
		discardPile.clear();
		shuffle();
	}

	/**
	 * @return le nombre de cartes restantes dans la pioche
	 */
	public int drawPileSize() {
		return drawPile.size();
	}

	/**
	 * @return le nombre de cartes dans la défausse
	 */
	public int discardPileSize() {
		return discardPile.size();
	}

	/**
	 * @return représentation textuelle du paquet ex :
	 *         {@code "Pioche: 44 | Défausse: 3"}
	 */
	@Override
	public String toString() {
		return "Pioche: " + drawPile.size() + " | Défausse: " + discardPile.size();
	}
}