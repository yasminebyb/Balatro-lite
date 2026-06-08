package view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import domain.Rank;
import domain.Suit;

/**
 * Responsabilité unique : charger et exposer toutes les ressources disque
 * (images de cartes, fonds d'écran, musique d'ambiance).
 */
public class Zen6Assets {

    private final Map<String, BufferedImage> cardImages = new HashMap<>();
    private BufferedImage background;
    private BufferedImage menuBackground;

    public Zen6Assets() throws IOException {
        loadBackground();
        loadMenuBackground();
        loadCardImages();
    }

    private void loadBackground() throws IOException {
        try (var in = Zen6Assets.class.getResourceAsStream("/background.png")) {
            if (in != null) background = ImageIO.read(in);
        }
    }

    private void loadMenuBackground() throws IOException {
        try (var in = Zen6Assets.class.getResourceAsStream("/background-2.png")) {
            if (in != null) menuBackground = ImageIO.read(in);
        }
    }

    private void loadCardImages() throws IOException {
        for (var rank : Rank.values()) {
            for (var suit : Suit.values()) {
                var key  = rank.name() + "_" + suit.name();
                var path = "/cards/" + key + ".png";
                try (var in = Zen6Assets.class.getResourceAsStream(path)) {
                    if (in != null) cardImages.put(key, ImageIO.read(in));
                }
            }
        }
    }

    /** Fond principal du jeu, peut être null si le fichier est absent. */
    public BufferedImage getBackground()    { return background; }

    /** Fond du menu principal, peut être null si le fichier est absent. */
    public BufferedImage getMenuBackground() { return menuBackground; }

    /**
     * Image d'une carte identifiée par sa clé "RANK_SUIT".
     * Retourne null si l'image n'a pas été trouvée sur le disque.
     */
    public BufferedImage getCardImage(String key) { return cardImages.get(key); }
}