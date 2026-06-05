package view;

import domain.Card;
import domain.Hand;
import domain.Planet;
import model.GameState;

import java.util.List;

/**
 * Contrat d'affichage du jeu Balatri.
 * <p>
 * Toute vue (console, graphique) doit implémenter cette interface.
 * {@code GameController} ne dépend que de cette abstraction — il ne sait pas si
 * l'affichage se fait en console ou avec Zen6.
 * </p>
 *
 * @see ConsoleView
 */
public interface View {

	/**
	 * Affiche les cartes piochées au joueur.
	 *
	 * @param cards les cartes piochées, non null
	 * @throws NullPointerException si {@code cards} est null
	 */
	void showHand(List<Card> cards);

	/**
	 * Demande au joueur de sélectionner 5 cartes parmi les 8 affichées. Retourne
	 * les indices des cartes choisies (base 0).
	 *
	 * @param cards les cartes disponibles, non null et non vide
	 * @return liste de 5 indices choisis par le joueur
	 * @throws NullPointerException     si {@code cards} est null
	 * @throws IllegalArgumentException si {@code cards} est vide
	 */
	List<Integer> askCardSelection(List<Card> cards);

	// ===================== EXTENSION B — DÉFAUSSE ACTIVE =====================

	/**
	 * Demande au joueur quelles cartes il souhaite défausser avant de jouer.
	 * <p>
	 * Le joueur peut choisir un ou plusieurs indices parmi les cartes disponibles,
	 * ou ne rien saisir pour passer. La liste retournée est vide si le joueur
	 * décide de ne pas défausser.
	 * </p>
	 * <p>
	 * <strong>Mode console uniquement</strong> — cette méthode est bloquante.
	 * {@code Zen6View} lève {@link UnsupportedOperationException} : la défausse
	 * graphique passe par {@code GameController#onDiscardComplete(List)}.
	 * </p>
	 *
	 * @param cards             les cartes actuellement en main, non null
	 * @param discardsRemaining le nombre de défausses encore disponibles, positif
	 * @return liste des indices à défausser (peut être vide si le joueur passe),
	 *         jamais null
	 * @throws NullPointerException si {@code cards} est null
	 */
	List<Integer> askDiscardSelection(List<Card> cards, int discardsRemaining);

	/**
	 * Affiche la main mise à jour après une défausse active.
	 * <p>
	 * Appelée par {@code GameController} après avoir tiré les cartes de
	 * remplacement. La vue doit raffraîchir son affichage de la main courante.
	 * </p>
	 *
	 * @param updatedHand       la main après remplacement, non null
	 * @param discardsRemaining le nombre de défausses restantes après cette
	 *                          défausse, positif ou nul
	 * @throws NullPointerException     si {@code updatedHand} est null
	 * @throws IllegalArgumentException si {@code discardsRemaining} est négatif
	 */
	void showDiscardResult(List<Card> updatedHand, int discardsRemaining);


	/**
	 * Affiche les cartes actives de la main et le bonus de chips associé.
	 * <p>
	 * Cette méthode est appelée par {@code GameController} après l'évaluation
	 * de la main, avant {@link #showHandResult}.
	 * </p>
	 *
	 * @param activeCards la liste des cartes actives, non null, peut être vide
	 *                    si aucune carte ne contribue individuellement
	 * @param cardBonus   le bonus total en chips apporté par les cartes actives,
	 *                    positif ou nul
	 * @throws NullPointerException     si {@code activeCards} est null
	 * @throws IllegalArgumentException si {@code cardBonus} est strictement négatif
	 * @see #showHandResult(Hand, int)
	 */
	void showActiveCards(List<Card> activeCards, int cardBonus); 
	/**
	 * Affiche la combinaison détectée et le score de la main jouée.
	 *
	 * @param hand  la main jouée, non null
	 * @param score le score calculé, positif ou nul
	 * @throws NullPointerException     si {@code hand} est null
	 * @throws IllegalArgumentException si {@code score} est négatif
	 */
	void showHandResult(Hand hand, int score);

	/**
	 * Affiche l'état courant de la partie.
	 *
	 * @param state l'état courant, non null
	 * @throws NullPointerException si {@code state} est null
	 */
	void showGameState(GameState state);

	/**
	 * Affiche la planète reçue après un blind battu.
	 *
	 * @param planet la planète obtenue, non null
	 * @param state  l'état courant, non null
	 * @throws NullPointerException si {@code planet} ou {@code state} est null
	 */
	void showPlanetReward(Planet planet, GameState state);

	/**
	 * Affiche un message générique.
	 *
	 * @param message le message à afficher, non null
	 * @throws NullPointerException si {@code message} est null
	 */
	void showMessage(String message);

	/**
	 * Affiche le message de victoire. Appelé quand tous les blinds ont été battus.
	 */
	void showVictory();

	/**
	 * Affiche le message de défaite. Appelé quand le joueur n'a plus de mains et
	 * n'a pas atteint la cible.
	 */
	void showDefeat();

}