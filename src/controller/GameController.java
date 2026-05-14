package controller;

import domain.Card;
import domain.Hand;
import domain.Planet;
import model.GameState;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contrôleur principal du jeu Balatri.
 * <p>
 * Orchestre la boucle de jeu : gestion des blinds, des tours,
 * du score et des planètes. Interagit avec le joueur via la console
 * provisoirement — sera branché sur {@code View} ultérieurement.
 * </p>
 */
public class GameController {

    private final GameState state;
    private final Scanner scanner;

    /**
     * @param state l'état initial de la partie, non null
     */
    public GameController(GameState state) {
        this.state   = state;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Lance et orchestre la partie complète.
     */
    public void run() {
        IO.println("=== BALATRI ===");
        IO.println("Bonne chance !\n");

        while (!state.isGameWon() && !state.isGameOver()) {
            jouerUnBlind();
        }

        if (state.isGameWon()) {
            IO.println("\n=== VICTOIRE ! Vous avez battu tous les blinds ! ===");
        }
    }

    /**
     * Gère le déroulement complet d'un blind.
     */
    private void jouerUnBlind() {
        IO.println("=== " + state.getCurrentBlind().name().toUpperCase()
                + " — Cible : " + state.getCurrentBlind().targetScore() + " pts ===");

        while (state.hasHandsRemaining() && !state.isBlindBeaten()) {
            afficherEtat();
            jouerUnTour();
        }

        if (state.isBlindBeaten()) {
            IO.println("\nBlind battu ! Score : " + state.getCurrentScore()
                    + " / " + state.getCurrentBlind().targetScore());
            gererBlindBattu();
        } else {
            IO.println("\n=== DÉFAITE — Score insuffisant : "
                    + state.getCurrentScore()
                    + " / " + state.getCurrentBlind().targetScore() + " ===");
        }
    }

    /**
     * Affiche l'état courant de la partie.
     */
    private void afficherEtat() {
        IO.println("\nScore : " + state.getCurrentScore()
                + " / " + state.getCurrentBlind().targetScore()
                + " | Mains restantes : " + state.getHandsRemaining()
                + " | Pioche : " + state.getDeck().drawPileSize());
    }

    /**
     * Joue un tour complet :
     * pioche 8 cartes, sélectionne 5, calcule le score.
     */
    private void jouerUnTour() {
        // 1. piocher 8 cartes
        List<Card> pioche = state.getDeck().draw(8);

        // 2. afficher les cartes
        IO.println("\nTes cartes :");
        for (int i = 0; i < pioche.size(); i++) {
            IO.println(i + " : " + pioche.get(i));
        }

        // 3. demander la sélection de 5 cartes
        List<Integer> indices = demanderSelection(pioche);

        // 4. construire la main avec les 5 cartes choisies
        List<Card> cartesChoisies = new ArrayList<>();
        for (int i : indices) {
            cartesChoisies.add(pioche.get(i));
        }
        Hand hand = new Hand(cartesChoisies);

        // 5. défausser les 3 cartes restantes
        List<Card> cartesDefaussees = new ArrayList<>(pioche);
        cartesDefaussees.removeAll(cartesChoisies);
        state.getDeck().discard(cartesDefaussees);

        // 6. calculer le score
        int chips = state.getChips(hand.getHandRank());
        int mult  = state.getMult(hand.getHandRank());
        int score = chips * mult;

        // 7. afficher le résultat
        IO.println("\n" + hand);
        IO.println(hand.getHandRank().getLabel()
                + " → " + chips + " chips × " + mult + " mult = " + score + " pts");

        // 8. mettre à jour l'état
        state.addScore(score);
        state.decrementHands();
    }

    /**
     * Demande au joueur de sélectionner 5 indices parmi 8.
     * Vérifie la validité de la saisie.
     *
     * @param pioche les 8 cartes disponibles
     * @return liste de 5 indices valides
     */
    private List<Integer> demanderSelection(List<Card> pioche) {
        while (true) {
            IO.println("Choisis 5 cartes (ex: 0 1 2 3 4) :");
            String ligne = scanner.nextLine().trim();
            String[] parts = ligne.split("\\s+");

            // vérifier qu'on a bien 5 indices
            if (parts.length != 5) {
                IO.println("Erreur : tu dois choisir exactement 5 cartes.");
                continue;
            }

            // vérifier que les indices sont valides
            List<Integer> indices = new ArrayList<>();
            boolean valide = true;
            for (String part : parts) {
                try {
                    int idx = Integer.parseInt(part);
                    if (idx < 0 || idx >= pioche.size()) {
                        IO.println("Erreur : indice " + idx + " invalide (0 à 7).");
                        valide = false;
                        break;
                    }
                    if (indices.contains(idx)) {
                        IO.println("Erreur : indice " + idx + " choisi deux fois.");
                        valide = false;
                        break;
                    }
                    indices.add(idx);
                } catch (NumberFormatException e) {
                    IO.println("Erreur : " + part + " n'est pas un nombre.");
                    valide = false;
                    break;
                }
            }

            if (valide) return indices;
        }
    }

    /**
     * Gère la récompense après un blind battu :
     * donne une planète aléatoire et passe au blind suivant.
     */
    private void gererBlindBattu() {
        Planet planet = Planet.random();
        state.applyPlanet(planet);
        IO.println("Planète obtenue : " + planet.getLabel()
                + " → " + planet.getTarget().getLabel()
                + " +" + planet.getBonusChips() + " chips"
                + " / +" + planet.getBonusMult() + " mult");

        IO.println("Nouveaux niveaux — "
                + planet.getTarget().getLabel()
                + " : " + state.getChips(planet.getTarget())
                + " chips × " + state.getMult(planet.getTarget()) + " mult\n");

        state.nextBlind(4);
    }
}