package domain;

/**
 * Représente un objectif de score à atteindre dans une partie de Balatri.
 * <p>
 * Permet d'avoir différents types de blinds sans modifier le reste du code.
 * Implémentations disponibles : {@link StandardBlind}. Extension E : ajouter
 * des blinds avec contraintes en implémentant cette interface.
 * </p>
 *
 * @see StandardBlind
 * @see model.GameState
 */
public interface Blind {

	/**
	 * Retourne le nom du blind.
	 *
	 * @return le nom
	 */
	String name();

	/**
	 * Retourne le score cible à atteindre.
	 *
	 * @return le score cible, strictement positif
	 */
	int targetScore();
}