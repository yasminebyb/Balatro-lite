package view;

import java.util.List;

import domain.Card;
import model.GameState;

/**
 * Représente une vue du jeu Balatri.
 * <p>
 * Une vue est responsable de l'interaction avec le joueur :
 * affichage des informations, des cartes et récupération
 * des choix utilisateur.
 * </p>
 * <p>
 * Plusieurs implémentations sont possibles :
 * console et interface graphique Zen6.
 * </p>
 */
public interface View {

	/**
	 * Permet d'afficher un message sur la vue pour l'utilisateur.
	 *
	 * @param message le message à afficher
	 */
    void showMessage(String message);

    /**
     * Affiche l'état courant de la partie.
     *
     * @param state l'état courant du jeu, non null
     */
    void showState(GameState state);
    
    /**
     * Affiche les cartes actuellement disponibles.
     *
     * @param cards les cartes à afficher, non null
     */
    void showCards(List<Card> cards);

    /**
     * Demande au joueur de sélectionner des cartes parmi celles disponibles.
     *
     * @param cards les cartes disponibles
     * @return la liste des indices sélectionnés
     */
    List<Integer> askCardSelection(List<Card> cards);

}