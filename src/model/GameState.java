package model;

import domain.Blind;
import domain.Deck;
import domain.HandRank;
import domain.Planet;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Représente l'état complet d'une partie de Balatri à un instant T.
 * <p>
 * Contient toutes les informations nécessaires pour décrire la partie : blind
 * courant, score cumulé, mains restantes, niveaux des combinaisons et état de
 * la pioche.
 * </p>
 * <p>
 * {@code GameState} ne contient aucune logique de jeu : il expose uniquement
 * des méthodes de lecture et de modification d'état. La logique appartient à
 * {@code GameController}.
 * </p>
 *
 * @see domain.Blind
 * @see domain.Planet
 * @see domain.HandRank
 */
public class GameState {

	/** Liste de tous les blinds de la partie, dans l'ordre. */
	private final List<Blind> blinds;

	/** Index du blind courant dans la liste. */
	private int currentBlindIndex;

	/** Score cumulé sur le blind courant. */
	private int currentScore;

	/** Nombre de mains restantes pour le blind courant. */
	private int handsRemaining;

	/**
	 * Niveaux courants de chips et mult pour chaque combinaison. int[0] = chips
	 * courants, int[1] = mult courant. Initialisés aux valeurs de base de HandRank,
	 * modifiés par les planètes.
	 */
	private final Map<HandRank, int[]> levels;

	/** La pioche courante partagée sur toute la partie. */
	private final Deck deck;

	/**
	 * Crée un état de partie initial.
	 *
	 * @param blinds        la liste des blinds de la partie, non null et non vide
	 * @param handsPerBlind le nombre de mains autorisées par blind
	 * @throws NullPointerException     si {@code blinds} est null
	 * @throws IllegalArgumentException si {@code blinds} est vide ou si
	 *                                  {@code handsPerBlind} est négatif ou nul
	 */
	public GameState(List<Blind> blinds, int handsPerBlind) {
		Objects.requireNonNull(blinds, "blinds must not be null");
		if (blinds.isEmpty()) {
			throw new IllegalArgumentException("blinds must not be empty");
		}
		if (handsPerBlind <= 0) {
			throw new IllegalArgumentException("handsPerBlind must be positive");
		}
		this.blinds = blinds;
		this.currentBlindIndex = 0;
		this.currentScore = 0;
		this.handsRemaining = handsPerBlind;
		this.deck = new Deck();
		this.levels = new EnumMap<>(HandRank.class);

		// initialise chaque combinaison avec ses valeurs de base
		for (HandRank hr : HandRank.values()) {
			levels.put(hr, new int[] { hr.getBaseChips(), hr.getBaseMult() });
		}
	}

	/**
	 * Retourne le blind actuellement en cours.
	 *
	 * @return le blind courant
	 */
	public Blind getCurrentBlind() {
		return blinds.get(currentBlindIndex);
	}

	/**
	 * Retourne le score cumulé sur le blind courant.
	 *
	 * @return le score courant
	 */
	public int getCurrentScore() {
		return currentScore;
	}

	/**
	 * Retourne le nombre de mains restantes pour le blind courant.
	 *
	 * @return les mains restantes
	 */
	public int getHandsRemaining() {
		return handsRemaining;
	}

	/**
	 * Retourne les chips courants d'une combinaison (après planètes).
	 *
	 * @param hr la combinaison ciblée, non null
	 * @return les chips courants
	 * @throws NullPointerException si {@code hr} est null
	 */
	public int getChips(HandRank hr) {
		Objects.requireNonNull(hr, "HandRank must not be null");
		return levels.get(hr)[0];
	}

	/**
	 * Retourne le mult courant d'une combinaison (après planètes).
	 *
	 * @param hr la combinaison ciblée, non null
	 * @return le mult courant
	 * @throws NullPointerException si {@code hr} est null
	 */
	public int getMult(HandRank hr) {
		Objects.requireNonNull(hr, "HandRank must not be null");
		return levels.get(hr)[1];
	}

	/**
	 * Retourne la pioche courante.
	 *
	 * @return le deck
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * Indique si le score cumulé atteint la cible du blind courant.
	 *
	 * @return {@code true} si le blind est battu
	 */
	public boolean isBlindBeaten() {
		return currentScore >= getCurrentBlind().targetScore();
	}

	/**
	 * Indique s'il reste des mains à jouer.
	 *
	 * @return {@code true} s'il reste au moins une main
	 */
	public boolean hasHandsRemaining() {
		return handsRemaining > 0;
	}

	/**
	 * Indique si la partie est perdue. La partie est perdue quand le blind n'est
	 * pas battu et qu'il ne reste plus de mains.
	 *
	 * @return {@code true} si la partie est terminée en défaite
	 */
	public boolean isGameOver() {
		return !isBlindBeaten() && !hasHandsRemaining();
	}

	/**
	 * Indique si tous les blinds ont été battus.
	 *
	 * @return {@code true} si la partie est gagnée
	 */
	public boolean isGameWon() {
		return currentBlindIndex >= blinds.size();
	}

	/**
	 * Ajoute des points au score cumulé du blind courant.
	 *
	 * @param points les points à ajouter
	 * @throws IllegalArgumentException si {@code points} est négatif
	 */
	public void addScore(int points) {
		if (points < 0) {
			throw new IllegalArgumentException("points must not be negative");
		}
		currentScore += points;
	}

	/**
	 * Décrémente le nombre de mains restantes d'une unité.
	 *
	 * @throws IllegalStateException si plus aucune main n'est disponible
	 */
	public void decrementHands() {
		if (handsRemaining <= 0) {
			throw new IllegalStateException("No hands remaining");
		}
		handsRemaining--;
	}

	/**
	 * Applique l'effet d'une planète sur la combinaison ciblée. Augmente
	 * définitivement les chips et le mult de la combinaison.
	 *
	 * @param planet la planète à appliquer, non null
	 * @throws NullPointerException si {@code planet} est null
	 */
	public void applyPlanet(Planet planet) {
		Objects.requireNonNull(planet, "planet must not be null");
		int[] level = levels.get(planet.getTarget());
		level[0] += planet.getBonusChips();
		level[1] += planet.getBonusMult();
	}

	/**
	 * Passe au blind suivant et réinitialise le score et les mains.
	 *
	 * @param handsPerBlind le nombre de mains pour le nouveau blind
	 * @throws IllegalArgumentException si {@code handsPerBlind} est négatif ou nul
	 */
	public void nextBlind(int handsPerBlind) {
		if (handsPerBlind <= 0) {
			throw new IllegalArgumentException("handsPerBlind must be positive");
		}
		currentBlindIndex++;
		currentScore = 0;
		handsRemaining = handsPerBlind;
	}

	/**
	 * Retourne une représentation textuelle de l'état courant.
	 *
	 * @return ex: {@code "Blind1 (cible : 300) | Score: 150 | Mains: 3"}
	 */
	@Override
	public String toString() {
		return getCurrentBlind() + " | Score: " + currentScore + " | Mains: " + handsRemaining;
	}
}