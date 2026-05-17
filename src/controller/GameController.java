package controller;

import domain.Card;
import domain.Hand;
import domain.Planet;
import model.GameState;
import view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contrôleur principal du jeu Balatri.
 * <p>
 * Deux modes de fonctionnement :
 * - Console : {@link #run()} orchestre tout avec une boucle bloquante.
 * - Zen6 : {@link #initTour()} + {@link #onSelectionComplete(List)} réagissent
 *   aux événements sans bloquer la boucle graphique.
 * </p>
 */
public class GameController {

    private final GameState state;
    private final View view;
    private List<Card> piocheCourante;

    /**
     * @param state l'état initial de la partie, non null
     * @param view  la vue à utiliser, non null
     */
    public GameController(GameState state, View view) {
        this.state = Objects.requireNonNull(state, "state must not be null");
        this.view  = Objects.requireNonNull(view,  "view must not be null");
    }

    // ===================== MODE CONSOLE =====================

    /**
     * Lance la partie complète en mode console.
     * Boucle bloquante — ne pas appeler en mode Zen6.
     */
    public void run() {
        view.showMessage("=== BALATRI === Bonne chance !\n");
        while (!state.isGameWon() && !state.isGameOver()) {
            jouerUnBlind();
        }
        if (state.isGameWon()) {
            view.showVictory();
        }
    }

    private void jouerUnBlind() {
        view.showMessage("\n=== "
                + state.getCurrentBlind().name().toUpperCase()
                + " — Cible : "
                + state.getCurrentBlind().targetScore() + " pts ===");

        while (state.hasHandsRemaining() && !state.isBlindBeaten()) {
            view.showGameState(state);
            jouerUnTour();
        }

        if (state.isBlindBeaten()) {
            gererBlindBattu();
            // victoire vérifiée dans run()
        } else {
            view.showDefeat();
        }
    }
    private void jouerUnTour() {
        var pioche  = state.getDeck().draw(8);
        view.showHand(pioche);
        var indices = view.askCardSelection(pioche);
        appliquerSelection(pioche, indices);
    }

    // ===================== MODE ZEN6 =====================

    /**
     * Initialise un nouveau tour en mode Zen6.
     * Pioche 8 cartes et les transmet à la vue — sans bloquer.
     * Appelé par {@code Zen6View} au démarrage et après chaque tour.
     */
    public void initTour() {
        if (state.isGameWon()) {
            view.showVictory();
            return;
        }
        if (state.isGameOver()) {
            view.showDefeat();
            return;
        }
        view.showGameState(state);
        piocheCourante = state.getDeck().draw(8);
        view.showHand(piocheCourante);
    }

    /**
     * Appelé par {@code Zen6View} quand le joueur a sélectionné 5 cartes.
     * Calcule le score, met à jour l'état, prépare le tour suivant.
     *
     * @param indices les 5 indices sélectionnés par le joueur
     * @throws NullPointerException si {@code indices} est null
     */
    public void onSelectionComplete(List<Integer> indices) {
        Objects.requireNonNull(indices, "indices must not be null");
        appliquerSelection(piocheCourante, indices);

        if (state.isBlindBeaten()) {
            gererBlindBattu();
        }
        
        if (state.isGameWon()) {
            view.showVictory();
            return;
        }

        if (state.isGameOver()) {
            view.showDefeat();
            return;
        }
        
        initTour();
    }
    // ===================== COMMUN =====================

    /**
     * Applique la sélection du joueur — commun aux deux modes.
     */
    /**
     * Applique la sélection du joueur — commun aux deux modes.
     */
    private void appliquerSelection(List<Card> pioche, List<Integer> indices) {
        var cartesChoisies = new ArrayList<Card>();
        for (int i : indices) {
            cartesChoisies.add(pioche.get(i));
        }
        var hand = new Hand(cartesChoisies);
        int chips = state.getChips(hand.getHandRank());
        int mult  = state.getMult(hand.getHandRank());
        int score = chips * mult;
        view.showHandResult(hand, score);
        
        state.addScore(score);
        state.decrementHands();
        state.getDeck().discard(pioche);
    }

    private void gererBlindBattu() {
        var planet = Planet.random();
        state.applyPlanet(planet);
        state.nextBlind(4);
        // affiche la planète que si la partie continue
        if (!state.isGameWon()) {
            view.showPlanetReward(planet, state);
        }
    }
}