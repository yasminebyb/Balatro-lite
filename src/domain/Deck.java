package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente le paquet de cartes du jeu.
 * <p>
 * Gère deux piles : la pioche ({@code drawPile}) depuis laquelle le joueur tire
 * ses cartes, et la défausse ({@code discardPile}) où vont les cartes non
 * jouées. Lorsque la pioche ne contient plus assez de cartes, la défausse est
 * automatiquement remélangée et réintégrée dans la pioche.
 * </p>
 */
public final class Deck {

	/** La pioche : Cartes disponibles à piocher. */
	private final List<Card> drawPile;

	/**
	 * La défausse : Cartes défaussées, réintégrées dans la pioche si nécessaire.
	 */
	private final List<Card> discardPile;

	/**
	 * Crée un paquet de 52 cartes mélangées aléatoirement.
	 */
	public Deck() {
		drawPile = new ArrayList<>();
		discardPile = new ArrayList<>();
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
	 * <p>
	 * Si la pioche ne contient pas assez de cartes, la défausse est remélangée et
	 * réintégrée automatiquement.
	 * </p>
	 *
	 * @param count le nombre de cartes à piocher
	 * @return une liste immuable de {@code count} cartes
	 * @throws IllegalArgumentException si {@code count} est négatif ou nul
	 * @throws IllegalStateException    si le paquet ne contient pas assez de cartes
	 */
	public List<Card> draw(int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("Count must be positive");
		}
		if (drawPile.size() < count) {
			refill();
		}
		if (drawPile.size() < count) {
			throw new IllegalStateException("Not enough cards in deck");
		}
		List<Card> drawn = new ArrayList<>(drawPile.subList(0, count));
		drawPile.subList(0, count).clear();
		return Collections.unmodifiableList(drawn);
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
	 * Retourne le nombre de cartes restantes dans la pioche.
	 *
	 * @return taille de la pioche
	 */
	public int drawPileSize() {
		return drawPile.size();
	}

	/**
	 * Retourne le nombre de cartes dans la défausse.
	 *
	 * @return taille de la défausse
	 */
	public int discardPileSize() {
		return discardPile.size();
	}

	/**
	 * Retourne une représentation textuelle de l'état du paquet.
	 *
	 * @return ex: {@code "Pioche: 44 | Défausse: 3"}
	 */
	@Override
	public String toString() {
		return "Pioche: " + drawPile.size() + " | Défausse: " + discardPile.size();
	}
}