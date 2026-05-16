package view;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.imageio.ImageIO;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import domain.Card;
import domain.Rank;
import domain.Suit;
import model.GameState;

public class Zen6View implements View {

	private GameState currentState;

	private List<Card> currentCards;

	private final List<Integer> selectedCards;

	private final Map<String, BufferedImage> cardImages;

	private ApplicationContext context;

	private String currentMessage = "";

	private Color messageColor = Color.WHITE;

	private BufferedImage background;

	private Clip musicClip;

	public Zen6View() throws IOException, UnsupportedAudioFileException, LineUnavailableException {

		this.selectedCards = new ArrayList<>();

		this.cardImages = new HashMap<>();

		try (InputStream input = Zen6View.class.getResourceAsStream("/background.png")) {

			background = ImageIO.read(input);

		}

		try (InputStream input = Zen6View.class.getResourceAsStream("/music/ambience.wav");
				AudioInputStream audio = AudioSystem.getAudioInputStream(input)) {

			musicClip = AudioSystem.getClip();

			musicClip.open(audio);

			musicClip.loop(Clip.LOOP_CONTINUOUSLY);

			FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);

			gainControl.setValue(-20.0f);

			musicClip.start();
		}

		for (Rank rank : Rank.values()) {

			for (Suit suit : Suit.values()) {

				String key = rank.name() + "_" + suit.name();

				String path = "/cards/" + key + ".png";

				try (InputStream input = Zen6View.class.getResourceAsStream(path)) {

					if (input != null) {

						BufferedImage image = ImageIO.read(input);

						cardImages.put(key, image);
					}
				}
			}
		}

		Application.run(Color.BLACK, context -> {

			this.context = context;

			render();
		});
	}

	@Override
	public void showMessage(String message) {

		this.currentMessage = message;

		this.messageColor = Color.WHITE;

		render();
	}

	public void showLoseMessage(String message) {

		this.currentMessage = message;

		this.messageColor = Color.RED;

		render();
	}

	@Override
	public void showState(GameState state) {

		this.currentState = state;

		render();
	}

	@Override
	public void showCards(List<Card> cards) {

		this.currentCards = cards;

		render();
	}

	@Override
	public List<Integer> askCardSelection(List<Card> cards) {

		this.currentCards = cards;

		this.selectedCards.clear();

		render();

		while (selectedCards.size() < 5) {

			var event = context.pollOrWaitEvent(10);

			if (event == null) {
				continue;
			}

			if (event instanceof KeyboardEvent keyboardEvent) {

				if (keyboardEvent.action() == KeyboardEvent.Action.KEY_PRESSED
						&& keyboardEvent.key() == KeyboardEvent.Key.ESCAPE) {

					System.exit(0);
				}
			}
			
			if (event instanceof PointerEvent pointerEvent) {

				if (pointerEvent.action() == PointerEvent.Action.POINTER_DOWN) {

					int mouseX = (int) pointerEvent.location().x();
					int mouseY = (int) pointerEvent.location().y();

					handleCardClick(mouseX, mouseY);
				}
			}
		}

		return List.copyOf(selectedCards);
	}

	private void handleCardClick(int mouseX, int mouseY) {

		if (currentCards == null) {
			return;
		}

		var screenInfo = context.getScreenInfo();

		float screenWidth = screenInfo.width();
		float screenHeight = screenInfo.height();

		int cardWidth = 120;
		int cardHeight = 160;
		int spacing = 140;

		int totalWidth = currentCards.size() * spacing;

		int startX = (int) (screenWidth / 2 - totalWidth / 2);

		int y = (int) (screenHeight - cardHeight - 40);

		for (int i = 0; i < currentCards.size(); i++) {

			int x = startX + i * spacing;

			boolean inside = mouseX >= x && mouseX <= x + cardWidth && mouseY >= y && mouseY <= y + cardHeight;

			if (inside) {

				if (selectedCards.contains(i)) {

					selectedCards.remove(Integer.valueOf(i));

				} else if (selectedCards.size() < 5) {

					selectedCards.add(i);
				}

				render();

				break;
			}
		}
	}

	private void render() {

		if (context == null) {
			return;
		}

		context.renderFrame(graphics -> {

			var screenInfo = context.getScreenInfo();

			float screenWidth = screenInfo.width();
			float screenHeight = screenInfo.height();

			graphics.drawImage(background, 0, 0, (int) screenWidth, (int) screenHeight, null);

			graphics.setColor(new Color(0, 0, 0, 120));

			graphics.fillRect(0, 0, (int) screenWidth, (int) screenHeight);

			graphics.setColor(Color.WHITE);

			graphics.drawString("BALATRI", screenWidth / 2 - 60, 60);

			if (currentState != null) {

				graphics.drawString("Score : " + currentState.getCurrentScore() + " / "
						+ currentState.getCurrentBlind().targetScore(), screenWidth / 2 - 120, 120);

				graphics.drawString("Mains restantes : " + currentState.getHandsRemaining(), screenWidth / 2 - 120,
						150);
			}

			if (!currentMessage.isEmpty()) {

				graphics.setColor(messageColor);

				graphics.drawString(currentMessage, screenWidth / 2 - 120, 220);

				graphics.setColor(Color.WHITE);
			}

			if (currentCards != null) {

				int cardWidth = 120;
				int cardHeight = 160;
				int spacing = 140;

				int totalWidth = currentCards.size() * spacing;

				int startX = (int) (screenWidth / 2 - totalWidth / 2);

				int y = (int) (screenHeight - cardHeight - 40);

				for (int i = 0; i < currentCards.size(); i++) {

					Card card = currentCards.get(i);

					int x = startX + i * spacing;

					String key = card.rank().name() + "_" + card.suit().name();

					BufferedImage image = cardImages.get(key);

					if (image != null) {

						graphics.drawImage(image, x, y, cardWidth, cardHeight, null);

						if (selectedCards.contains(i)) {

							graphics.setColor(Color.GREEN);

							graphics.drawRect(x, y, cardWidth, cardHeight);
						}

					} else {

						graphics.setColor(Color.WHITE);

						graphics.fillRect(x, y, cardWidth, cardHeight);

						if (selectedCards.contains(i)) {

							graphics.setColor(Color.GREEN);

						} else {

							graphics.setColor(Color.BLACK);
						}

						graphics.drawRect(x, y, cardWidth, cardHeight);

						if (card.suit().toString().equals("♥") || card.suit().toString().equals("♦")) {

							graphics.setColor(Color.RED);

						} else {

							graphics.setColor(Color.BLACK);
						}

						graphics.drawString(card.toString(), x + 10, y + 20);
					}
				}
			}
		});
	}
}