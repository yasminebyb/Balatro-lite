package domain;

/**
 * Niveau courant d'une combinaison de poker : chips et multiplicateur après
 * application des planètes.
 *
 * Toute modification via {@link #withBonus} produit une nouvelle instance sans
 * altérer l'existante, garantissant la cohérence d'une
 * {@code Map<HandRank, HandLevel>} même si une référence est conservée côté
 * appelant.
 *
 * @param chips le nombre de chips courant, strictement positif
 * @param mult  le multiplicateur courant, strictement positif
 */

public record HandLevel(int chips, int mult) {

	/** Validation compacte du constructeur canonique. */
	public HandLevel {
		if (chips <= 0) {
			throw new IllegalArgumentException("chips must be positive, got: " + chips);
		}
		if (mult <= 0) {
			throw new IllegalArgumentException("mult must be positive, got: " + mult);
		}
	}

	/**
	 * Retourne un nouveau {@code HandLevel} avec les bonus de planète appliqués.
	 * Cette instance reste inchangée.
	 *
	 * @param bonusChips chips supplémentaires, positif ou nul
	 * @param bonusMult  multiplicateur supplémentaire, positif ou nul
	 * @return un nouveau {@code HandLevel} avec les valeurs augmentées
	 * @throws IllegalArgumentException si {@code bonusChips} ou {@code bonusMult}
	 *                                  est strictement négatif
	 */

	public HandLevel withBonus(int bonusChips, int bonusMult) {
		if (bonusChips < 0) {
			throw new IllegalArgumentException("bonusChips must not be negative");
		}
		if (bonusMult < 0) {
			throw new IllegalArgumentException("bonusMult must not be negative");
		}
		return new HandLevel(chips + bonusChips, mult + bonusMult);
	}
}