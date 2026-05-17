package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.Event;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import controller.GameController;
import domain.Card;
import domain.Hand;
import domain.HandRank;
import domain.Planet;
import domain.Rank;
import domain.Suit;
import model.GameState;

/**
 * Vue graphique de Balatri utilisant Zen6.
 * <p>
 * Orchestre la boucle graphique et délègue la logique à {@link GameController}.
 * N'appelle jamais {@code askCardSelection} — gère la sélection via les clics.
 * </p>
 */
public class Zen6View implements View {

    private GameState currentState;
    private List<Card> currentCards;
    private final List<Integer> selectedCards = new ArrayList<>();
    private final Map<String, BufferedImage> cardImages = new HashMap<>();
    private ApplicationContext context;
    private String currentMessage = "";
    private Color messageColor = Color.WHITE;
    private BufferedImage background;
    private Clip musicClip;
    private boolean gameOver = false;

    /**
     * @throws IOException si le chargement d'une ressource obligatoire échoue
     */
    public Zen6View() throws IOException {
        loadBackground();
        loadMusic();
        loadCardImages();
    }

    private void loadBackground() throws IOException {
        try (var input = Zen6View.class.getResourceAsStream("/background.png")) {
            if (input != null) background = ImageIO.read(input);
        }
    }

    private void loadMusic() {
        try {
            var input = Zen6View.class.getResourceAsStream("/music/ambience.wav");
            if (input == null) return;
            var audio = AudioSystem.getAudioInputStream(input);
            musicClip = AudioSystem.getClip();
            musicClip.open(audio);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            var gain = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(-20.0f);
            musicClip.start();
        } catch (IOException e) {
            musicClip = null;
        } catch (Exception e) {
            musicClip = null;
        }
    }

    private void loadCardImages() throws IOException {
        for (var rank : Rank.values()) {
            for (var suit : Suit.values()) {
                var key  = rank.name() + "_" + suit.name();
                var path = "/cards/" + key + ".png";
                try (var input = Zen6View.class.getResourceAsStream(path)) {
                    if (input != null) cardImages.put(key, ImageIO.read(input));
                }
            }
        }
    }

    /**
     * Lance la boucle graphique Zen6.
     * C'est le point d'entrée du mode graphique — appelé depuis {@code Main}.
     *
     * @param controller le contrôleur de jeu, non null
     */
    public void start(GameController controller) {
    	Objects.requireNonNull(controller, "controller must not be null"); 
        Application.run(Color.BLACK, ctx -> {
            this.context = ctx;

            // initialise le premier tour
            controller.initTour();
            render();

            // boucle graphique principale — conforme à la démo Zen6
            for (;;) {
                Event event = ctx.pollOrWaitEvent(100);

                if (event != null) {
                    switch (event) {
                        case PointerEvent pe -> {
                            if (pe.action() == PointerEvent.Action.POINTER_DOWN) {
                                handleClick(
                                    (int) pe.location().x(),
                                    (int) pe.location().y(),
                                    controller
                                );
                            }
                        }
                        case KeyboardEvent ke -> {
                            if (ke.key() == KeyboardEvent.Key.ESCAPE) {
                                ctx.dispose();
                                return;
                            }
                        }
                        default -> {}
                    }
                }

                if (gameOver) {
                    ctx.dispose();
                    return;
                }

                render();
            }
        });
    }

    /**
     * Gère un clic souris — détecte si une carte a été cliquée.
     */
    private void handleClick(int mouseX, int mouseY, GameController controller) {
        Objects.requireNonNull(controller, "controller must not be null"); 
    	if (currentCards == null || gameOver) return;

        var info       = context.getScreenInfo();
        int cardWidth  = 120;
        int cardHeight = 160;
        int spacing    = 140;
        int startX     = (int) (info.width() / 2 - currentCards.size() * spacing / 2);
        int cardY      = (int) (info.height() - cardHeight - 40);

        for (int i = 0; i < currentCards.size(); i++) {
            int x  = startX + i * spacing;
            var ok = mouseX >= x && mouseX <= x + cardWidth
                  && mouseY >= cardY && mouseY <= cardY + cardHeight;
            if (ok) {
                if (selectedCards.contains(i)) {
                    selectedCards.remove(Integer.valueOf(i));
                } else if (selectedCards.size() < 5) {
                    selectedCards.add(i);
                }

                // si 5 cartes sélectionnées → notifier le contrôleur
                if (selectedCards.size() == 5) {
                    var selection = List.copyOf(selectedCards);
                    selectedCards.clear();
                    currentCards = null;
                    controller.onSelectionComplete(selection);
                }
                break;
            }
        }
    }

    // ===================== INTERFACE VIEW =====================

    @Override
    public void showHand(List<Card> cards) {
    	Objects.requireNonNull(cards, "cards must not be null"); 
        this.currentCards = cards;
        this.selectedCards.clear();
    }

    @Override
    public void showHandResult(Hand hand, int score) {
    	Objects.requireNonNull(hand, "hand must not be null"); 
        this.currentMessage = hand.getHandRank().getLabel()
                + " → +" + score + " pts";
        this.messageColor = Color.YELLOW;
        render();
        pause(2000);
        this.currentMessage = "";
    }

    @Override
    public void showGameState(GameState state) {
        this.currentState = state;
    }

    @Override
    public void showPlanetReward(Planet planet, GameState state) {
    	Objects.requireNonNull(planet, "planet must not be null"); 
    	Objects.requireNonNull(state, "state must not be null"); 
        this.currentState   = state;
        this.currentMessage = "Planete : " + planet.getLabel()
                + "  +" + planet.getBonusChips() + " chips"
                + " / +" + planet.getBonusMult() + " mult";
        this.messageColor = Color.CYAN;
        render();
        pause(3000);
        this.currentMessage = "";
    }

    @Override
    public void showMessage(String message) {
    	Objects.requireNonNull(message, "message must not be null"); 
        this.currentMessage = message;
        this.messageColor   = Color.WHITE;
    }

    @Override
    public void showVictory() {
        this.currentMessage = "VICTOIRE ! Tous les blinds battus !";
        this.messageColor   = Color.GREEN;
        this.currentCards   = null; // cache les cartes
        render();                   // affiche le message
        pause(2000);                // attend 3 secondes
        this.gameOver       = true; // ferme la fenêtre
    }
    @Override
    public void showDefeat() {
        this.currentMessage = "DEFAITE - Score insuffisant";
        this.messageColor   = Color.RED;
        this.currentCards   = null; // cache les cartes
        render();                   // affiche le message
        pause(2000);                // attend 3 secondes
        this.gameOver       = true; // ferme la fenêtre
    }

    /**
     * Non utilisé en mode Zen6 — la sélection passe par les clics souris.
     * Lève une exception si appelé par erreur.
     */
    @Override
    public List<Integer> askCardSelection(List<Card> cards) {
        throw new UnsupportedOperationException(
            "Zen6View ne supporte pas askCardSelection — utiliser handleClick"
        );
    }

    // ===================== RENDU =====================

    private void render() {
        if (context == null) return;

        context.renderFrame(g -> {
            var info = context.getScreenInfo();
            int w    = (int) info.width();
            int h    = (int) info.height();

            // ===== FOND =====
            if (background != null) {
                g.drawImage(background, 0, 0, w, h, null);
                g.setColor(new Color(0, 0, 0, 140));
                g.fillRect(0, 0, w, h);
            } else {
                g.setColor(new Color(20, 20, 40));
                g.fillRect(0, 0, w, h);
            }

            // ===== TITRE =====
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.setColor(new Color(220, 180, 80)); // doré
            var title = "BALATRI";
            var titleW = g.getFontMetrics().stringWidth(title);
            g.drawString(title, w / 2 - titleW / 2, 50);

            // ===== PANNEAU ÉTAT (gauche) =====
            if (currentState != null && !currentState.isGameWon()) {
                // fond semi-transparent
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRoundRect(20, 70, 280, 180, 12, 12);
                g.setColor(new Color(220, 180, 80, 80));
                g.drawRoundRect(20, 70, 280, 180, 12, 12);

                g.setFont(new Font("Arial", Font.BOLD, 15));
                g.setColor(Color.WHITE);
                g.drawString("Blind  : " + currentState.getCurrentBlind().name(), 35, 100);
                g.drawString("Cible  : " + currentState.getCurrentBlind().targetScore() + " pts", 35, 125);

                // barre de progression du score
                int targetScore  = currentState.getCurrentBlind().targetScore();
                int currentScore = currentState.getCurrentScore();
                double progress  = Math.min(1.0, (double) currentScore / targetScore);
                g.setColor(new Color(60, 60, 60, 180));
                g.fillRoundRect(35, 135, 250, 16, 8, 8);
                g.setColor(new Color(80, 200, 100));
                g.fillRoundRect(35, 135, (int)(250 * progress), 16, 8, 8);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 12));
                g.drawString(currentScore + " / " + targetScore + " pts", 35, 148);

                g.setFont(new Font("Arial", Font.BOLD, 15));
                g.setColor(Color.WHITE);

                // mains restantes avec icônes
                var sb = new StringBuilder("Mains  : ");
                for (int i = 0; i < 4; i++) {
                    sb.append(i < currentState.getHandsRemaining() ? "♥ " : "♡ ");
                }
                g.drawString(sb.toString(), 35, 175);
                g.drawString("Pioche : " + currentState.getDeck().drawPileSize() + " cartes", 35, 200);
                g.drawString("Defausse : " + currentState.getDeck().discardPileSize() + " cartes", 35, 225);
            }

            // ===== PANNEAU NIVEAUX (droite ) =====
            if (currentState != null && !currentState.isGameWon()) {
                int panelX = w - 290;
                int panelH = 9 * 22 + 40;
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRoundRect(panelX - 10, 65, 280, panelH, 12, 12);
                g.setColor(new Color(220, 180, 80, 80));
                g.drawRoundRect(panelX - 10, 65, 280, panelH, 12, 12);

                g.setFont(new Font("Arial", Font.BOLD, 13));
                g.setColor(new Color(220, 180, 80));
                g.drawString("Niveaux des combinaisons", panelX, 88);

                int levelY = 108;
                for (var hr : HandRank.values()) {
                    int chips = currentState.getChips(hr);
                    int mult  = currentState.getMult(hr);
                    int pts   = chips * mult;
                    boolean boosted = chips > hr.getBaseChips() || mult > hr.getBaseMult();
                    g.setFont(new Font("Arial", Font.PLAIN, 12));
                    g.setColor(boosted ? new Color(80, 220, 120) : Color.WHITE);
                    g.drawString(hr.getLabel() + " : " + chips + "c x " + mult + "m = " + pts + " pts",
                            panelX, levelY);
                    levelY += 22;
                }
            }

            // ===== MESSAGE =====
            if (!currentMessage.isEmpty()) {
                var fm  = g.getFontMetrics(new Font("Arial", Font.BOLD, 22));
                int mw  = fm.stringWidth(currentMessage);
                int mx  = w / 2 - mw / 2;
                int my  = h / 2;
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRoundRect(mx - 20, my - 28, mw + 40, 44, 12, 12);
                g.setFont(new Font("Arial", Font.BOLD, 22));
                g.setColor(messageColor);
                g.drawString(currentMessage, mx, my);
            }

            // ===== CARTES =====
            if (currentCards != null) {
                int cardWidth  = 110;
                int cardHeight = 155;
                int spacing    = 130;
                int totalW     = currentCards.size() * spacing - (spacing - cardWidth);
                int startX     = w / 2 - totalW / 2;
                int cardY      = h - cardHeight - 30;

                // instruction
                g.setFont(new Font("Arial", Font.BOLD, 15));
                g.setColor(Color.WHITE);
                var instr  = "Clique sur 5 cartes  (" + selectedCards.size() + " / 5)";
                var instrW = g.getFontMetrics().stringWidth(instr);
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRoundRect(w / 2 - instrW / 2 - 12, cardY - 38, instrW + 24, 28, 8, 8);
                g.setColor(new Color(220, 180, 80));
                g.drawString(instr, w / 2 - instrW / 2, cardY - 18);

                for (int i = 0; i < currentCards.size(); i++) {
                    var card     = currentCards.get(i);
                    int x        = startX + i * spacing;
                    boolean sel  = selectedCards.contains(i);
                    int drawY    = sel ? cardY - 18 : cardY; // remonte si sélectionnée

                    var key   = card.rank().name() + "_" + card.suit().name();
                    var image = cardImages.get(key);

                    if (image != null) {
                        // ombre
                        g.setColor(new Color(0, 0, 0, 100));
                        g.fillRoundRect(x + 4, drawY + 4, cardWidth, cardHeight, 10, 10);
                        g.drawImage(image, x, drawY, cardWidth, cardHeight, null);
                    } else {
                        // carte sans image — style amélioré
                        g.setColor(new Color(240, 235, 210));
                        g.fillRoundRect(x, drawY, cardWidth, cardHeight, 10, 10);
                        g.setColor(new Color(180, 160, 100));
                        g.drawRoundRect(x, drawY, cardWidth, cardHeight, 10, 10);

                        boolean isRed = card.suit().toString().equals("♥")
                                     || card.suit().toString().equals("♦");
                        g.setColor(isRed ? new Color(180, 30, 30) : new Color(20, 20, 20));

                        // rang en haut à gauche
                        g.setFont(new Font("Arial", Font.BOLD, 16));
                        g.drawString(card.rank().getLabel(), x + 8, drawY + 22);
                        g.drawString(card.suit().getSymbol(), x + 8, drawY + 40);

                        // rang au centre
                        g.setFont(new Font("Arial", Font.BOLD, 36));
                        var sym  = card.suit().getSymbol();
                        var symW = g.getFontMetrics().stringWidth(sym);
                        g.drawString(sym, x + cardWidth / 2 - symW / 2, drawY + cardHeight / 2 + 12);

                        // rang en bas à droite (inversé)
                        g.setFont(new Font("Arial", Font.BOLD, 16));
                        g.drawString(card.rank().getLabel(), x + cardWidth - 24, drawY + cardHeight - 28);
                        g.drawString(card.suit().getSymbol(), x + cardWidth - 24, drawY + cardHeight - 10);
                    }

                    // bordure sélection
                    if (sel) {
                        g.setColor(new Color(80, 220, 80, 200));
                        g.setStroke(new java.awt.BasicStroke(3));
                        g.drawRoundRect(x, drawY, cardWidth, cardHeight, 10, 10);
                        g.setStroke(new java.awt.BasicStroke(1));
                    }
                }
            }
        });
    }
    private static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}