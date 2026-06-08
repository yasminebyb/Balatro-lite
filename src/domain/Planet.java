package domain;

import java.util.Objects;
import java.util.Random;

/**
 * Représente une planète — modificateur permanent de score.
 * <p>
 * Chaque planète est associée à une combinaison ({@link HandRank}). Lorsqu'une
 * planète est obtenue, elle augmente définitivement les chips et le
 * multiplicateur de la combinaison ciblée pour le reste de la partie. Ces
 * valeurs sont stockées et appliquées dans {@code GameState}.
 * </p>
 * <p>
 * À chaque blind battu, le joueur reçoit une planète choisie aléatoirement via
 * {@link #random()}. Il est possible d'obtenir plusieurs fois la même planète —
 * les bonus s'accumulent.
 * </p>
 *
 * @see HandRank
 * @see model.GameState
 */
public enum Planet {

    PLUTO  ("Pluton",  HandRank.HIGH_CARD,       10, 1),
    MERCURY("Mercure", HandRank.PAIR,             15, 1),
    URANUS ("Uranus",  HandRank.TWO_PAIR,         20, 1),
    VENUS  ("Vénus",   HandRank.THREE_OF_A_KIND,  20, 2),
    SATURN ("Saturne", HandRank.STRAIGHT,         30, 3),
    JUPITER("Jupiter", HandRank.FLUSH,            15, 2),
    EARTH  ("Terre",   HandRank.FULL_HOUSE,       25, 2),
    MARS   ("Mars",    HandRank.FOUR_OF_A_KIND,   30, 3),
    NEPTUNE("Neptune", HandRank.STRAIGHT_FLUSH,   40, 4);


    /**
     * Snapshot immuable des valeurs de l'enum, calculé une seule fois.
     * <p>
     * {@link #values()} retourne un nouveau tableau cloné à chaque appel.
     * Mettre en cache évite cette allocation inutile dans {@link #random()}.
     * </p>
     */
    private static final Planet[] VALUES = Planet.values();

    /**
     * Générateur aléatoire partagé sur toute la durée du programme.
     * <p>
     * Une seule instance suffit : {@link Random} maintient un état interne
     * qui garantit une bonne distribution. Créer un {@code new Random()} à
     * chaque tirage réinitialise cet état depuis l'horloge système, ce qui
     * dégrade la distribution et produit des allocations inutiles.
     * </p>
     */
    private static final Random RNG = new Random();

    // ── Champs d'instance ─────────────────────────────────────────────────

    /** Label d'affichage en français. */
    private final String   label;

    /** La combinaison de poker ciblée par cette planète. */
    private final HandRank target;

    /** Bonus de chips ajouté à la combinaison ciblée. */
    private final int bonusChips;

    /** Bonus de multiplicateur ajouté à la combinaison ciblée. */
    private final int bonusMult;

    // ── Constructeur ──────────────────────────────────────────────────────

    /**
     * @param label      le label d'affichage, non null
     * @param target     la combinaison ciblée, non null
     * @param bonusChips le bonus de chips, strictement positif
     * @param bonusMult  le bonus de multiplicateur, strictement positif
     * @throws NullPointerException     si {@code label} ou {@code target} est null
     * @throws IllegalArgumentException si {@code bonusChips} ou {@code bonusMult}
     *                                  est négatif ou nul
     */
    Planet(String label, HandRank target, int bonusChips, int bonusMult) {
        this.label      = Objects.requireNonNull(label,  "label must not be null");
        this.target     = Objects.requireNonNull(target, "target must not be null");
        if (bonusChips <= 0)
            throw new IllegalArgumentException("bonusChips must be positive, got: " + bonusChips);
        if (bonusMult  <= 0)
            throw new IllegalArgumentException("bonusMult must be positive, got: "  + bonusMult);
        this.bonusChips = bonusChips;
        this.bonusMult  = bonusMult;
    }

    // ── Accesseurs ────────────────────────────────────────────────────────

    /**
     * Retourne le label d'affichage de la planète.
     *
     * @return le label d'affichage, jamais null
     */
    public String getLabel() { return label; }

    /**
     * Retourne la combinaison de poker ciblée par cette planète.
     *
     * @return le {@link HandRank} ciblé, jamais null
     */
    public HandRank getTarget() { return target; }

    /**
     * Retourne le bonus de chips apporté par cette planète.
     *
     * @return le bonus de chips, strictement positif
     */
    public int getBonusChips() { return bonusChips; }

    /**
     * Retourne le bonus de multiplicateur apporté par cette planète.
     *
     * @return le bonus de multiplicateur, strictement positif
     */
    public int getBonusMult() { return bonusMult; }

    /**
     * Retourne une planète choisie aléatoirement parmi les 9 disponibles.
     * Appelée par {@code GameController} à chaque blind battu.
     *
     * <p>
     * Utilise {@link #VALUES} (tableau mis en cache) et {@link #RNG} (instance
     * partagée) — aucune allocation à chaque appel.
     * </p>
     *
     * @return une planète aléatoire, jamais null
     */
    public static Planet random() {
        return VALUES[RNG.nextInt(VALUES.length)];
    }

    /**
     * {@inheritDoc}
     *
     * @return le label d'affichage de la planète
     */
    @Override
    public String toString() { return label; }
}