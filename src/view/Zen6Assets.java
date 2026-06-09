package view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import domain.Rank;
import domain.Suit;

/**
 * Responsabilité unique : charger et exposer toutes les ressources graphiques
 * (images de cartes et fond d'écran).
 */
public class Zen6Assets {

	private final Map<String, BufferedImage> cardImages = new HashMap<>();
	private BufferedImage background;

	/**
	 * Charge toutes les ressources depuis le classpath.
	 *
	 * @throws IOException si une ressource obligatoire est illisible
	 */
	public Zen6Assets() throws IOException {
		loadBackground();
		loadCardImages();
	}

	private void loadBackground() throws IOException {
		try (var in = Zen6Assets.class.getResourceAsStream("/background.png")) {
			if (in != null) {
				background = ImageIO.read(in);
			}
		}
	}

	private void loadCardImages() throws IOException {
		for (var rank : Rank.values()) {
			for (var suit : Suit.values()) {
				var key = rank.name() + "_" + suit.name();
				var path = "/cards/" + key + ".png";
				try (var in = Zen6Assets.class.getResourceAsStream(path)) {
					if (in != null) {
						cardImages.put(key, ImageIO.read(in));
					}
				}
			}
		}
	}

	/** @return le fond principal, ou {@code null} si le fichier est absent */
	public BufferedImage getBackground() {
		return background;
	}

	/**
	 * @param key clé au format {@code "RANK_SUIT"} (ex : {@code "ACE_HEARTS"})
	 * @return l'image de la carte, ou {@code null} si introuvable
	 */
	public BufferedImage getCardImage(String key) {
		return cardImages.get(key);
	}
}