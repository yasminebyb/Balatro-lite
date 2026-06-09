package view;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.Event;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import controller.GameController;
import domain.Card;
import domain.Hand;
import domain.Planet;
import model.GameState;

/**
 * Vue graphique de Balatri.
 *
 * Responsabilités :
 * <ul>
 * <li>Implémenter {@link View} (contrat avec le contrôleur).</li>
 * <li>Gérer la boucle d'événements Zen6 et dispatcher les clics.</li>
 * <li>Maintenir l'état mutable de la vue (cartes, sélection, messages…).</li>
 * <li>Déléguer le chargement des ressources à {@link Zen6Assets}.</li>
 * <li>Déléguer chaque frame à {@link Zen6Renderer}.</li>
 * </ul>
 */
public final class Zen6View implements View {

	private static final Color GOLD = new Color(220, 180, 80);
	private static final Color ACTIVE_ORG = new Color(255, 160, 50);
	private static final Color CYAN_INFO = new Color(100, 200, 255);

	private final Zen6Assets assets;
	private final Zen6Renderer renderer;

	// Injecté via start() et non via le constructeur (boucle Zen6 asynchrone)
	private GameController controller;
	private GameState currentState;
	private List<Card> currentCards;
	private final List<Integer> selectedCards = new ArrayList<>();
	private List<Card> activeCards = List.of();
	private ApplicationContext context;
	private String currentMessage = "";
	private Color messageColor = Color.WHITE;
	private volatile boolean gameOver = false;

	/**
	 * Construit la vue et charge toutes les ressources (images).
	 *
	 * @throws IOException si une ressource obligatoire est introuvable
	 */
	public Zen6View() throws IOException {
		this.assets = new Zen6Assets();
		this.renderer = new Zen6Renderer(assets);
	}

	/**
	 * Lance la boucle graphique Zen6.
	 *
	 * @param controller le contrôleur de jeu, non null
	 */
	public void start(GameController controller) {
		Objects.requireNonNull(controller, "controller must not be null");
		this.controller = controller;

		Application.run(Color.BLACK, ctx -> {
			this.context = ctx;
			controller.initTurn();
			render();

			for (;;) {
				Event event = ctx.pollOrWaitEvent(100);
				if (event != null) {
					switch (event) {
					case PointerEvent pe -> {
						if (pe.action() == PointerEvent.Action.POINTER_DOWN) {
							handleClick((int) pe.location().x(),
									(int) pe.location().y());
						}
					}
					case KeyboardEvent ke -> {
						if (ke.key() == KeyboardEvent.Key.ESCAPE) {
							ctx.dispose();
							return;
						}
					}
					default -> {
					}
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
	 * Traite les clics pendant le jeu.
	 *
	 * Trois zones détectées :
	 * <ol>
	 * <li>Bouton <em>DÉFAUSSER</em> — si défausses disponibles et sélection non
	 * vide.</li>
	 * <li>Bouton <em>JOUER</em> — si {@value Hand#SIZE} cartes sélectionnées.</li>
	 * <li>Zone d'une carte — bascule son état de sélection.</li>
	 * </ol>
	 */
	private void handleClick(int mx, int my) {
		if (currentCards == null || gameOver) {
			return;
		}

		var info = context.getScreenInfo();
		int w = (int) info.width();
		int h = (int) info.height();

		int barPW = w - 2 * Zen6Renderer.MARGIN;
		int btnW = Math.min(220, (barPW - 60) / 2);
		int gap = 16;
		int bx = w / 2 - (2 * btnW + gap) / 2;
		int by = h - Zen6Renderer.BOTTOM_H + 26;
		int bh = Zen6Renderer.BTN_H;

		boolean canDiscard = currentState != null
				&& currentState.hasDiscardsRemaining()
				&& !selectedCards.isEmpty();
		if (canDiscard && mx >= bx && mx <= bx + btnW && my >= by && my <= by + bh) {
			var indices = List.copyOf(selectedCards);
			selectedCards.clear();
			controller.onDiscardComplete(indices);
			return;
		}

		int playBtnX = bx + btnW + gap;
		if (selectedCards.size() == Hand.SIZE
				&& mx >= playBtnX && mx <= playBtnX + btnW
				&& my >= by && my <= by + bh) {
			var indices = List.copyOf(selectedCards);
			selectedCards.clear();
			currentCards = null;
			controller.onSelectionComplete(indices);
			return;
		}

		int totalW = currentCards.size() * Zen6Renderer.SPACING - (Zen6Renderer.SPACING - Zen6Renderer.CARD_W);
		int startX = w / 2 - totalW / 2;
		int baseY = h - Zen6Renderer.CARD_H - 10;

		for (int i = 0; i < currentCards.size(); i++) {
			boolean isSel = selectedCards.contains(i);
			int cx = startX + i * Zen6Renderer.SPACING;
			int cy = isSel ? baseY - 18 : baseY;
			if (mx >= cx && mx <= cx + Zen6Renderer.CARD_W && my >= cy && my <= cy + Zen6Renderer.CARD_H) {
				if (isSel) {
					selectedCards.remove(Integer.valueOf(i));
				} else if (selectedCards.size() < Hand.SIZE) {
					selectedCards.add(i);
				}
				break;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void showHand(List<Card> cards) {
		Objects.requireNonNull(cards, "cards must not be null");
		this.currentCards = cards;
		this.selectedCards.clear();
		this.activeCards = List.of();
		render();
	}

	/** {@inheritDoc} */
	@Override
	public void showActiveCards(List<Card> active, int cardBonus) {
		Objects.requireNonNull(active, "activeCards must not be null");
		if (cardBonus < 0) {
			throw new IllegalArgumentException("cardBonus must not be negative");
		}
		this.activeCards = List.copyOf(active);
		this.currentMessage = "Cartes actives : +" + cardBonus + " chips";
		this.messageColor = ACTIVE_ORG;
		render();
		pause(1500);
		this.activeCards = List.of();
		this.currentMessage = "";
	}

	/** {@inheritDoc} */
	@Override
	public void showHandResult(Hand hand, int score) {
		Objects.requireNonNull(hand, "hand must not be null");
		if (score < 0) {
			throw new IllegalArgumentException("score must not be negative");
		}
		this.currentMessage = hand.getHandRank().getLabel() + "  →  +" + score + " pts";
		this.messageColor = GOLD;
		render();
		pause(2000);
		this.currentMessage = "";
	}

	/** {@inheritDoc} */
	@Override
	public void showGameState(GameState state) {
		Objects.requireNonNull(state, "state must not be null");
		this.currentState = state;
	}

	/** {@inheritDoc} */
	@Override
	public void showPlanetReward(Planet planet, GameState state) {
		Objects.requireNonNull(planet, "planet must not be null");
		Objects.requireNonNull(state, "state must not be null");
		this.currentState = state;
		this.currentMessage = planet.getLabel()
				+ "  +" + planet.getBonusChips() + " chips"
				+ "  /  +" + planet.getBonusMult() + " mult"
				+ "  →  " + planet.getTarget().getLabel();
		this.messageColor = CYAN_INFO;
		render();
		pause(3000);
		this.currentMessage = "";
	}

	/** {@inheritDoc} */
	@Override
	public void showMessage(String message) {
		Objects.requireNonNull(message, "message must not be null");
		this.currentMessage = message;
		this.messageColor = Color.WHITE;
	}

	/** {@inheritDoc} */
	@Override
	public void showVictory() {
		this.currentMessage = "VICTOIRE !  Tous les blinds battus !";
		this.messageColor = new Color(80, 220, 80);
		this.currentCards = null;
		render();
		pause(3000);
		this.gameOver = true;
	}

	/** {@inheritDoc} */
	@Override
	public void showDefeat() {
		this.currentMessage = "DEFAITE  -  Score insuffisant";
		this.messageColor = new Color(220, 60, 60);
		this.currentCards = null;
		render();
		pause(2000);
		this.gameOver = true;
	}

	/** {@inheritDoc} */
	@Override
	public void showDiscardResult(List<Card> updatedHand, int discardsRemaining) {
		Objects.requireNonNull(updatedHand, "updatedHand must not be null");
		if (discardsRemaining < 0) {
			throw new IllegalArgumentException("discardsRemaining must not be negative");
		}
		this.currentCards = updatedHand;
		this.selectedCards.clear();
		this.currentMessage = discardsRemaining > 0
				? "Défausse effectuée — " + discardsRemaining + " restante(s)"
				: "Défausse effectuée — plus de défausse disponible";
		this.messageColor = CYAN_INFO;
		render();
		pause(1500);
		this.currentMessage = "";
	}

	/**
	 * Non utilisé en mode Zen6 — la défausse passe par
	 * {@link GameController#onDiscardComplete(List)} via {@link #handleClick}.
	 *
	 * @throws UnsupportedOperationException toujours
	 */
	@Override
	public List<Integer> askDiscardSelection(List<Card> cards, int discardsRemaining) {
		throw new UnsupportedOperationException(
				"Zen6View : défausse via clic uniquement (onDiscardComplete)");
	}

	/**
	 * Non utilisé en mode Zen6 — la sélection passe par {@link #handleClick}.
	 *
	 * @throws UnsupportedOperationException toujours
	 */
	@Override
	public List<Integer> askCardSelection(List<Card> cards) {
		throw new UnsupportedOperationException(
				"Zen6View : sélection via clic uniquement (onSelectionComplete)");
	}

	private void render() {
		if (context == null) {
			return;
		}
		context.renderFrame(g -> {
			var info = context.getScreenInfo();
			int w = (int) info.width();
			int h = (int) info.height();

			boolean canPlay = selectedCards.size() == Hand.SIZE;
			boolean canDiscard = currentState != null
					&& currentState.hasDiscardsRemaining()
					&& !selectedCards.isEmpty();
			int discardsLeft = currentState != null
					? currentState.getDiscardsRemaining()
					: 0;

			renderer.renderFrame(g, w, h,
					currentState,
					currentCards, selectedCards,
					activeCards, currentMessage, messageColor,
					canPlay, canDiscard, discardsLeft);
		});
	}

	// Suspend le thread courant pour laisser le temps d'afficher un message
	private static void pause(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}