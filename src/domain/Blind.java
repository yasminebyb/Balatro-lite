package domain;

/**
 * Représente un objectif de score à atteindre dans une partie de Balatri.
 *
 * @see StandardBlind
 * @see model.GameState
 *
 * @implNote Pour ajouter des blinds avec contraintes (Extension E), créer une
 *           nouvelle implémentation de cette interface sans modifier le reste
 *           du code.
 */

public sealed interface Blind permits StandardBlind {

	/**
	 * @return le nom du blind
	 */

	String name();

	/**
	 * @return le score cible à atteindre, strictement positif
	 */

	int targetScore();
}