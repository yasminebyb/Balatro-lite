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
 * <p>
 * Contient toutes les informations nécessaires pour décrire la partie : blind
 * courant, score cumulé, mains restantes, niveaux des combinaisons et état de
 * la pioche.
 * </p>
 *
 * <p>
 * {@code GameState} ne contient aucune logique de jeu : il expose uniquement
 * des méthodes de lecture et de modification d'état. La logique appartient à
 * {@code GameController}.
 * </p>
 *
 * <h2>Immuabilité des niveaux</h2>
 * <p>
 * Les niveaux de combinaisons sont stockés dans un
 * {@link EnumMap}{@code <HandRank, HandLevel>}. {@link HandLevel} est un
 * {@code record} immuable : {@link #applyPlanet} remplace l'entrée par une
 * nouvelle instance plutôt que de muter l'existante.
 * </p>
 *
 * <h2>Performance JVM</h2>
 * <ul>
 *   <li>{@link EnumMap} est indexé par ordinal — accès O(1) sans hachage,
 *       très favorable au cache CPU.</li>
 *   <li>Les champs {@code int} de {@link HandLevel} sont des primitifs —
 *       pas de boxing, accesseurs inlinés par le JIT.</li>
 *   <li>La création d'un {@code HandLevel} dans {@link #applyPlanet} est
 *       négligeable : au plus {@code n_blinds × n_ranks} objets/partie,
 *       collectés en young generation.</li>
 * </ul>
 */
public final class GameState {

    // ── Règles du jeu ─────────────────────────────────────────────────────

    /**
     * Nombre de cartes que le joueur doit sélectionner pour jouer une main,
     * et nombre maximum de cartes défaussables en une seule action.
     * Toute règle liée à ce chiffre doit lire cette constante.
     */
    public static final int HAND_SIZE = 5;

    // ── Champs ────────────────────────────────────────────────────────────

    /** Liste immuable de tous les blinds de la partie. */
    private final List<Blind> blinds;

    /** Index du blind courant dans {@link #blinds}. */
    private int currentBlindIndex;

    /** Score cumulé sur le blind courant. */
    private int currentScore;

    /** Nombre de mains restantes pour le blind courant. */
    private int handsRemaining;

    /**
     * Niveaux courants (chips + mult) de chaque combinaison, après planètes.
     * <p>
     * {@link EnumMap} est backed par un tableau de taille fixe indexé par
     * {@link HandRank#ordinal()} — accès O(1), très cache-friendly.
     * Chaque valeur est un {@link HandLevel} immuable remplacé par
     * {@link #applyPlanet} plutôt que muté en place.
     * </p>
     */
    private final Map<HandRank, HandLevel> levels;

    /** La pioche partagée sur toute la partie. */
    private final Deck deck;

    /**
     * Nombre de mains accordées au début de chaque blind.
     * Valeur fixe pour toute la partie ; utilisée pour l'affichage
     * (jauge de cœurs) et pour réinitialiser {@link #handsRemaining}
     * à chaque appel à {@link #nextBlind}.
     */
    private final int handsPerBlind;

    /**
     * Nombre de défausses actives autorisées par blind (Extension B).
     * Valeur fixe pour toute la partie ; réinitialisée à chaque blind.
     */
    private final int discardsPerBlind;

    /**
     * Nombre de défausses actives restantes pour le blind courant.
     * Décrémenté par {@link #decrementDiscards()}, remis à
     * {@link #discardsPerBlind} par {@link #nextBlind}.
     */
    private int discardsRemaining;


    /**
     * Crée un état de partie avec 3 défausses par blind par défaut (Extension B).
     *
     * @param blinds        la liste des blinds, non null et non vide
     * @param handsPerBlind le nombre de mains autorisées par blind, strictement positif
     */
    public GameState(List<Blind> blinds, int handsPerBlind) {
        this(blinds, handsPerBlind, 3);
    }

    /**
     * Crée un état de partie initial complet.
     *
     * @param blinds           la liste des blinds, non null et non vide
     * @param handsPerBlind    le nombre de mains autorisées par blind, strictement positif
     * @param discardsPerBlind le nombre de défausses actives par blind, positif ou nul
     * @throws NullPointerException     si {@code blinds} est null
     * @throws IllegalArgumentException si {@code blinds} est vide, si
     *                                  {@code handsPerBlind} est négatif ou nul,
     *                                  ou si {@code discardsPerBlind} est négatif
     */
    public GameState(List<Blind> blinds, int handsPerBlind, int discardsPerBlind) {
        Objects.requireNonNull(blinds, "blinds must not be null");
        if (blinds.isEmpty())
            throw new IllegalArgumentException("blinds must not be empty");
        if (handsPerBlind <= 0)
            throw new IllegalArgumentException("handsPerBlind must be positive");
        if (discardsPerBlind < 0)
            throw new IllegalArgumentException("discardsPerBlind must not be negative");

        this.blinds            = List.copyOf(blinds);
        this.currentBlindIndex = 0;
        this.currentScore      = 0;
        this.handsPerBlind     = handsPerBlind;
        this.handsRemaining    = handsPerBlind;
        this.discardsPerBlind  = discardsPerBlind;
        this.discardsRemaining = discardsPerBlind;
        this.deck              = new Deck();

        // EnumMap pré-alloué à la taille exacte de l'enum — aucun rehash possible
        this.levels = new EnumMap<>(HandRank.class);
        for (HandRank hr : HandRank.values())
            levels.put(hr, new HandLevel(hr.getBaseChips(), hr.getBaseMult()));
    }

    /**
     * Retourne le blind actuellement en cours.
     * <p>
     * <strong>Précondition :</strong> ne doit pas être appelé si
     * {@link #isGameWon()} retourne {@code true}.
     * </p>
     *
     * @return le blind courant, jamais null
     * @throws IndexOutOfBoundsException si tous les blinds ont déjà été battus
     */
    public Blind getCurrentBlind() {
        return blinds.get(currentBlindIndex);
    }

    /**
     * Retourne le numéro du blind courant (1-based, pour l'affichage).
     *
     * @return le numéro du blind courant
     */
    public int getCurrentBlindNumber() {
        return currentBlindIndex + 1;
    }

    /**
     * Retourne le nombre total de blinds de la partie.
     *
     * @return le nombre total de blinds
     */
    public int getTotalBlinds() {
        return blinds.size();
    }

    /**
     * Retourne le score cumulé sur le blind courant, remis à zéro par
     * {@link #nextBlind}.
     *
     * @return le score courant, positif ou nul
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Retourne le nombre de mains restantes pour le blind courant.
     *
     * @return les mains restantes, positif ou nul
     */
    public int getHandsRemaining() {
        return handsRemaining;
    }

    /**
     * Retourne le nombre de mains accordées au début de chaque blind.
     * Utilisé par la vue pour afficher la jauge de cœurs complète.
     *
     * @return le nombre de mains par blind, strictement positif
     */
    public int getHandsPerBlind() {
        return handsPerBlind;
    }

    // ── Accesseurs — niveaux ──────────────────────────────────────────────

    /**
     * Retourne les chips courants d'une combinaison (après planètes).
     *
     * @param hr la combinaison ciblée, non null
     * @return les chips courants, strictement positifs
     * @throws NullPointerException si {@code hr} est null
     */
    public int getChips(HandRank hr) {
        Objects.requireNonNull(hr, "HandRank must not be null");
        return levels.get(hr).chips();
    }

    /**
     * Retourne le multiplicateur courant d'une combinaison (après planètes).
     *
     * @param hr la combinaison ciblée, non null
     * @return le mult courant, strictement positif
     * @throws NullPointerException si {@code hr} est null
     */
    public int getMult(HandRank hr) {
        Objects.requireNonNull(hr, "HandRank must not be null");
        return levels.get(hr).mult();
    }

    // ── Accesseurs — pioche & défausses ───────────────────────────────────

    /**
     * Retourne la pioche partagée de la partie.
     *
     * @return le deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Retourne le nombre de défausses actives restantes pour le blind courant.
     *
     * @return les défausses restantes, positif ou nul
     */
    public int getDiscardsRemaining() {
        return discardsRemaining;
    }

    /**
     * Indique si le joueur peut encore défausser des cartes ce blind.
     *
     * @return {@code true} s'il reste au moins une défausse disponible
     */
    public boolean hasDiscardsRemaining() {
        return discardsRemaining > 0;
    }


    /**
     * Indique si le score cumulé atteint ou dépasse la cible du blind courant.
     * <p>
     * <strong>Précondition :</strong> ne doit pas être appelé si
     * {@link #isGameWon()} retourne {@code true}.
     * </p>
     *
     * @return {@code true} si le blind est battu
     */
    public boolean isBlindBeaten() {
        return currentScore >= getCurrentBlind().targetScore();
    }

    /**
     * Indique s'il reste des mains à jouer pour le blind courant.
     *
     * @return {@code true} s'il reste au moins une main
     */
    public boolean hasHandsRemaining() {
        return handsRemaining > 0;
    }

    /**
     * Indique si tous les blinds ont été battus (partie gagnée).
     *
     * @return {@code true} si la partie est gagnée
     */
    public boolean isGameWon() {
        return currentBlindIndex >= blinds.size();
    }

    /**
     * Indique si la partie est perdue : plus de mains disponibles et blind
     * non atteint.
     * <p>
     * <strong>Correction bug :</strong> ce prédicat commence par vérifier
     * {@link #isGameWon()} pour éviter que l'appel à {@link #isBlindBeaten()}
     * — qui appelle {@link #getCurrentBlind()} — ne lève une
     * {@link IndexOutOfBoundsException} après que tous les blinds ont été battus.
     * </p>
     *
     * @return {@code true} si la partie est terminée en défaite
     */
    public boolean isGameOver() {
        if (isGameWon()) return false;           // guard : currentBlindIndex hors bornes
        return !isBlindBeaten() && !hasHandsRemaining();
    }

    // ── Mutations d'état ──────────────────────────────────────────────────

    /**
     * Ajoute des points au score cumulé du blind courant.
     *
     * @param points les points à ajouter, positif ou nul
     * @throws IllegalArgumentException si {@code points} est négatif
     */
    public void addScore(int points) {
        if (points < 0)
            throw new IllegalArgumentException("points must not be negative");
        currentScore += points;
    }

    /**
     * Décrémente le nombre de mains restantes d'une unité.
     *
     * @throws IllegalStateException si plus aucune main n'est disponible
     */
    public void decrementHands() {
        if (handsRemaining <= 0)
            throw new IllegalStateException("No hands remaining");
        handsRemaining--;
    }

    /**
     * Décrémente le compteur de défausses restantes d'une unité.
     *
     * @throws IllegalStateException si {@code discardsRemaining} est déjà à zéro
     */
    public void decrementDiscards() {
        if (discardsRemaining <= 0)
            throw new IllegalStateException("No discards remaining");
        discardsRemaining--;
    }

    /**
     * Applique l'effet d'une planète sur la combinaison ciblée.
     * <p>
     * Remplace l'entrée dans la map par un nouveau {@link HandLevel} —
     * pas de mutation en place. L'ancienne instance est éligible à la
     * collecte GC dès le retour de cette méthode.
     * </p>
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
     *
     * @param handsPerBlind le nombre de mains pour le nouveau blind, strictement positif
     * @throws IllegalArgumentException si {@code handsPerBlind} est négatif ou nul
     */
    public void nextBlind(int handsPerBlind) {
        if (handsPerBlind <= 0)
            throw new IllegalArgumentException("handsPerBlind must be positive");
        currentBlindIndex++;
        currentScore      = 0;
        handsRemaining    = handsPerBlind;
        discardsRemaining = discardsPerBlind;
    }

 
    /**
     * Retourne une représentation textuelle de l'état courant.
     *
     * @return ex : {@code "Blind 1/3 (cible : 300) | Score: 150 | Mains: 3 | Défausses: 2"}
     *         ou {@code "Partie gagnée | Score final : 1450"} si la partie est terminée
     */
    @Override
    public String toString() {
        if (isGameWon())
            return "Partie gagnée | Score final : " + currentScore;
        return getCurrentBlind()
                + " | Score: "    + currentScore
                + " | Mains: "    + handsRemaining
                + " | Défausses: " + discardsRemaining;
    }
}