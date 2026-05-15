package view;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.PointerEvent;

import domain.Card;
import model.GameState;

public class Zen6View implements View {

	private GameState currentState;

	private List<Card> currentCards;

	private final List<Integer> selectedCards;

	private ApplicationContext context;

	private String currentMessage = "";
	
	private Color messageColor = Color.WHITE;

	public Zen6View() {

		this.selectedCards = new ArrayList<>();

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

		for (int i = 0; i < currentCards.size(); i++) {

			int x = 50 + i * 100;
			int y = 300;

			boolean inside = mouseX >= x && mouseX <= x + 80 && mouseY >= y && mouseY <= y + 120;

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

			graphics.setColor(Color.BLACK);

			graphics.fillRect(
			    0,
			    0,
			    context.getScreenInfo().width(),
			    context.getScreenInfo().height()
			);
			
			graphics.setColor(Color.WHITE);

			graphics.drawString("BALATRI", 50, 50);

			if (currentState != null) {

				graphics.drawString("Score : " + currentState.getCurrentScore() + " / "
						+ currentState.getCurrentBlind().targetScore(), 50, 100);

				graphics.drawString("Mains restantes : " + currentState.getHandsRemaining(), 50, 130);
			}

			if (!currentMessage.isEmpty()) {

			    graphics.setColor(messageColor);

			    graphics.drawString(
			        currentMessage,
			        50,
			        180
			    );

			    graphics.setColor(Color.WHITE);
			}
			
			if (currentCards != null) {

				for (int i = 0; i < currentCards.size(); i++) {

					Card card = currentCards.get(i);

					int x = 50 + i * 100;
					int y = 300;

					if (selectedCards.contains(i)) {

						graphics.setColor(Color.GREEN);

					} else {

						graphics.setColor(Color.WHITE);
					}

					graphics.drawRect(x, y, 80, 120);

					graphics.drawString(card.toString(), x + 20, y + 60);

					graphics.setColor(Color.WHITE);
				}
			}
		});
	}
}