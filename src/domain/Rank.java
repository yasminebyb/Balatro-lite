package domain;

public enum Rank {
	TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9),
	TEN("10", 10), JACK("V", 10), QUEEN("D", 10), KING("R", 10), ACE("A", 11);

	private final String label;
	private final int value;

	Rank(String label, int value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return label;
	}
}