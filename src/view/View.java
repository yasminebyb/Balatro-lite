package view;

import domain.Card;
import domain.Hand;
import domain.Planet;
import model.GameState;

import java.util.List;

/**
 * Contrat d'affichage du jeu Balatri.
 *
 * Toute vue (console, graphique) doit implémenter cette interface.
 * {@code GameController} ne dépend que de cette abstraction — il ne sait pas si
 * l'affichage se fait en console ou avec Zen6.
 *
 * @see ConsoleView
 * @see Zen6View
 */
public sealed interface View permits ConsoleView, Zen6View {

	/**
	 * Affiche les cartes piochées au joueur.
	 *
	 * @param cards les cartes piochées, non null
	 * @throws NullPointerException si {@code cards} est null
	 */
	void showHand(List<Card> cards);

	/**
	 * Demande au joueur de sélectionner {@value domain.Hand#SIZE} cartes. <- 5
	 *
	 * @param cards les cartes disponibles, non null et non vide
	 * @return liste de {@value domain.Hand#SIZE} indices (base 0) choisis
	 * @throws NullPointerException     si {@code cards} est null
	 * @throws IllegalArgumentException si {@code cards} est vide
	 */
	List<Integer> askCardSelection(List<Card> cards);

	// ===================== Extension B — Défausse active =====================

	/**
	 * Demande au joueur quelles cartes il souhaite défausser avant de jouer.
	 *
	 * Le joueur peut choisir un ou plusieurs indices, ou ne rien saisir pour
	 * passer. La liste retournée est vide si le joueur décide de ne pas défausser.
	 *
	 * @param cards             les cartes actuellement en main, non null
	 * @param discardsRemaining le nombre de défausses encore disponibles, positif
	 * @return liste des indices à défausser, jamais null (peut être vide)
	 * @throws NullPointerException si {@code cards} est null
	 *
	 * @implNote Mode console uniquement — méthode bloquante. {@code Zen6View} lève
	 *           {@link UnsupportedOperationException} : la défausse graphique passe
	 *           par {@code GameController#onDiscardComplete(List)}.
	 */
	List<Integer> askDiscardSelection(List<Card> cards, int discardsRemaining);

	/**
	 * Affiche la main mise à jour après une défausse active.
	 *
	 * @param updatedHand       la main après remplacement, non null
	 * @param discardsRemaining le nombre de défausses restantes, positif ou nul
	 * @throws NullPointerException     si {@code updatedHand} est null
	 * @throws IllegalArgumentException si {@code discardsRemaining} est négatif
	 *
	 * @implNote La vue doit rafraîchir son affichage de la main courante.
	 */
	void showDiscardResult(List<Card> updatedHand, int discardsRemaining);

	// ===================== Résultat d'un tour ================================

	/**
	 * Affiche les cartes actives de la main et le bonus de chips associé. Appelée
	 * avant {@link #showHandResult}.
	 *
	 * @param activeCards la liste des cartes actives, non null
	 * @param cardBonus   le bonus total en chips, positif ou nul
	 * @throws NullPointerException     si {@code activeCards} est null
	 * @throws IllegalArgumentException si {@code cardBonus} est négatif
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

	// ===================== État de la partie =================================

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

	/** Affiche le message de victoire -> tous les blinds ont été battus. */
	void showVictory();

	/** Affiche le message de défaite -> plus de mains, cible non atteinte. */
	void showDefeat();
}