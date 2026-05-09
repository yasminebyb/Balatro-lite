package domain;

public enum HandRank {
    HIGH_CARD("Carte haute", 5, 1),
    PAIR("Paire", 10, 2),
    TWO_PAIR("Double paire", 20, 2),
    THREE_OF_A_KIND("Brelan", 30, 3),
    STRAIGHT("Suite", 30, 4),
    FLUSH("Couleur", 35, 4),
    FULL_HOUSE("Full", 40, 4),
    FOUR_OF_A_KIND("Carré", 60, 7),
    STRAIGHT_FLUSH("Quinte flush", 100, 8);

    private final String label;
    private final int baseChips;
    private final int baseMult;

    HandRank(String label, int baseChips, int baseMult) {
        this.label = label;
        this.baseChips = baseChips;
        this.baseMult = baseMult;
    }

    public String getLabel() { return label; }
    public int getBaseChips() { return baseChips; }
    public int getBaseMult() { return baseMult; }

    @Override
    public String toString() { return label; }
}