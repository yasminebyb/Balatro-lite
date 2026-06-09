package model;

import domain.Blind;
import domain.Deck;
import domain.HandLevel;
import domain.HandRank;
import domain.Planet;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Représente l'état complet d'une partie de Balatri à un instant T.
 *
 * Contient toutes les informations nécessaires pour décrire la partie : blind
 * courant, score cumulé, mains restantes, niveaux des combinaisons et état de
 * la pioche. Ne contient aucune logique de jeu — celle-ci appartient à
 * {@code GameController}.
 *
 * Les niveaux de combinaisons sont stockés dans un
 * {@link EnumMap}{@code <HandRank, HandLevel>}. {@link HandLevel} étant
 * immuable, {@link #applyPlanet} remplace l'entrée par une nouvelle instance
 * plutôt que de muter l'existante.
 */
public final class GameState {

	// === Constantes === 

	// Nombre de mains accordées par blind
	public static final int HANDS_PER_BLIND = 4;

	// Nombre de cartes distribuées en début de tour
	public static final int DRAW_SIZE = 8;

	// Nombre de défausses actives autorisées par blind (Extension B)
	private static final int DISCARDS_PER_BLIND = 3;

	// === Champs === 

	// Liste immuable de tous les blinds de la partie
	private final List<Blind> blinds;

	// Index du blind courant dans blinds
	private int currentBlindIndex;

	// Score cumulé sur le blind courant
	private int currentScore;

	// Nombre de mains restantes pour le blind courant
	private int handsRemaining;

	// Niveaux courants (chips + mult) de chaque combinaison, après planètes
	private final Map<HandRank, HandLevel> levels;

	// La pioche partagée sur toute la partie
	private final Deck deck;

	// Nombre de défausses restantes pour le blind courant
	private int discardsRemaining;

	/**
	 * Crée un état de partie initial.
	 *
	 * @param blinds la liste des blinds, non null et non vide
	 * @throws NullPointerException     si {@code blinds} est null
	 * @throws IllegalArgumentException si {@code blinds} est vide
	 */
	public GameState(List<Blind> blinds) {
		Objects.requireNonNull(blinds, "blinds must not be null");
		if (blinds.isEmpty()) {
			throw new IllegalArgumentException("blinds must not be empty");
		}

		this.blinds = List.copyOf(blinds);
		this.currentBlindIndex = 0;
		this.currentScore = 0;
		this.handsRemaining = HANDS_PER_BLIND;
		this.discardsRemaining = DISCARDS_PER_BLIND;
		this.deck = new Deck();

		// Initialisation des niveaux de base pour chaque combinaison
		this.levels = new EnumMap<>(HandRank.class);
		for (HandRank hr : HandRank.values()) {
			levels.put(hr, new HandLevel(hr.getBaseChips(), hr.getBaseMult()));
		}
	}

	// ── Accesseurs — blind & score ────────────────────────────────────────

	/**
	 * @return le blind courant, jamais null
	 * @throws IndexOutOfBoundsException si tous les blinds ont déjà été battus
	 */
	public Blind getCurrentBlind() {
		// Précondition : ne pas appeler si isGameWon() est vrai
		return blinds.get(currentBlindIndex);
	}

	/**
	 * @return le numéro du blind courant (1-based, pour l'affichage)
	 */
	public int getCurrentBlindNumber() {
		return currentBlindIndex + 1;
	}

	/**
	 * @return le nombre total de blinds de la partie
	 */
	public int getTotalBlinds() {
		return blinds.size();
	}

	/**
	 * @return le score cumulé sur le blind courant, remis à zéro par
	 *         {@link #nextBlind}
	 */
	public int getCurrentScore() {
		return currentScore;
	}

	/**
	 * @return le nombre de mains restantes pour le blind courant, positif ou nul
	 */
	public int getHandsRemaining() {
		return handsRemaining;
	}

	/**
	 * @return le nombre de mains accordées au début de chaque blind
	 */
	public int getHandsPerBlind() {
		return HANDS_PER_BLIND;
	}

	//  Accesseurs — niveaux 
	/**
	 * @param hr la combinaison ciblée, non null
	 * @return les chips courants (après planètes), strictement positifs
	 * @throws NullPointerException si {@code hr} est null
	 */
	public int getChips(HandRank hr) {
		Objects.requireNonNull(hr, "HandRank must not be null");
		return levels.get(hr).chips();
	}

	/**
	 * @param hr la combinaison ciblée, non null
	 * @return le multiplicateur courant (après planètes), strictement positif
	 * @throws NullPointerException si {@code hr} est null
	 */
	public int getMult(HandRank hr) {
		Objects.requireNonNull(hr, "HandRank must not be null");
		return levels.get(hr).mult();
	}

	// Accesseurs — pioche & défausses

	/**
	 * @return la pioche partagée de la partie
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * @return le nombre de défausses restantes pour le blind courant, positif ou
	 *         nul
	 */
	public int getDiscardsRemaining() {
		return discardsRemaining;
	}

	/**
	 * @return {@code true} s'il reste au moins une défausse disponible
	 */
	public boolean hasDiscardsRemaining() {
		return discardsRemaining > 0;
	}

	// Prédicats 
	/**
	 * @return {@code true} si le score cumulé atteint ou dépasse la cible
	 */
	public boolean isBlindBeaten() {
		// Précondition : ne pas appeler si isGameWon() est vrai
		return currentScore >= getCurrentBlind().targetScore();
	}

	/**
	 * @return {@code true} s'il reste au moins une main à jouer
	 */
	public boolean hasHandsRemaining() {
		return handsRemaining > 0;
	}

	/**
	 * @return {@code true} si tous les blinds ont été battus (partie gagnée)
	 */
	public boolean isGameWon() {
		return currentBlindIndex >= blinds.size();
	}

	/**
	 * @return {@code true} si la partie est terminée en défaite
	 */
	public boolean isGameOver() {
		if (isGameWon()) {
			return false;
		} // guard : évite IndexOutOfBoundsException sur getCurrentBlind()
		return !isBlindBeaten() && !hasHandsRemaining();
	}

	// Mutations d'état 
	/**
	 * Ajoute des points au score cumulé du blind courant.
	 *
	 * @param points les points à ajouter, positif ou nul
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
	 * Décrémente le compteur de défausses restantes d'une unité.
	 *
	 * @throws IllegalStateException si {@code discardsRemaining} est déjà à zéro
	 */
	public void decrementDiscards() {
		if (discardsRemaining <= 0) {
			throw new IllegalStateException("No discards remaining");
		}
		discardsRemaining--;
	}

	/**
	 * Applique l'effet d'une planète sur la combinaison ciblée.
	 *
	 * @param planet la planète à appliquer, non null
	 * @throws NullPointerException si {@code planet} est null
	 */
	public void applyPlanet(Planet planet) {
		Objects.requireNonNull(planet, "planet must not be null");
		HandRank hr = planet.getTarget();
		levels.put(hr, levels.get(hr).withBonus(planet.getBonusChips(), planet.getBonusMult()));
	}

	/**
	 * Passe au blind suivant et réinitialise le score, les mains et les défausses.
	 */
	public void nextBlind() {
		currentBlindIndex++;
		currentScore = 0;
		handsRemaining = HANDS_PER_BLIND;
		discardsRemaining = DISCARDS_PER_BLIND;
	}

	/**
	 * @return ex :
	 *         {@code "Blind 1/3 (cible : 300) | Score: 150 | Mains: 3 | Défausses: 2"}
	 *         ou {@code "Partie gagnée | Score final : 1450"} si la partie est
	 *         terminée
	 */
	@Override
	public String toString() {
		if (isGameWon()) {
			return "Partie gagnée | Score final : " + currentScore;
		}
		return getCurrentBlind()
				+ " | Score: " + currentScore
				+ " | Mains: " + handsRemaining
				+ " | Défausses: " + discardsRemaining;
	}
}