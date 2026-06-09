package view;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import domain.Card;
import domain.Hand;
import domain.HandRank;
import domain.Planet;
import domain.Suit;
import model.GameState;

/**
 * Moteur de rendu graphique de Balatri.
 *
 * Responsabilité unique : calculer les positions et dessiner chaque frame. Ne
 * gère ni les événements ni l'état de la vue — délégués à {@link Zen6View}.
 */
public class Zen6Renderer {

	// Dimensions des cartes

	public static final int CARD_W = 130;
	public static final int CARD_H = 183;
	public static final int SPACING = 148;
	public static final int BTN_H = 50;

	// Couleurs
	private static final Color BG = new Color(0x0d, 0x0d, 0x08);
	private static final Color PANEL_BG = new Color(0x12, 0x10, 0x08);
	private static final Color BORDER = new Color(0x3a, 0x2a, 0x1a);
	private static final Color BORDER_SCR = new Color(0x5a, 0x3a, 0x2a);
	private static final Color GOLD = new Color(0xc8, 0xa9, 0x6e);
	private static final Color MUTED = new Color(0x5a, 0x4a, 0x3a);
	private static final Color RED_DARK = new Color(0x9a, 0x3a, 0x3a);
	private static final Color BLUE_COLD = new Color(0x6a, 0xb0, 0xd4);
	private static final Color ORANGE_FIRE = new Color(0xc8, 0x5a, 0x3a);
	private static final Color GREEN_BOOST = new Color(0x6e, 0xf0, 0x96);
	private static final Color SEL_GOLD = new Color(0xc8, 0xa9, 0x6e, 220);
	private static final Color ACTIVE_ORG = new Color(0xff, 0xa0, 0x32);
	private static final Color ACTIVE_FILL = new Color(0xff, 0xa0, 0x32, 80);
	private static final Color CARD_RED = new Color(0x9a, 0x3a, 0x3a);
	private static final Color CARD_BLACK = new Color(0x8a, 0x7a, 0x6a);

	// Mise en page

	static final int MARGIN = 10;
	static final int TOP_H = 150;
	static final int LEFT_W = 300;
	static final int RIGHT_W = 300;
	static final int BOTTOM_H = 300;
	static final int JOKER_H = 115;
	static final int BTN_H_INNER = 44;
	static final int CORNER = 8;

	private static final int PILE_W = 52;
	private static final int PILE_H = 72;
	private static final int PILE_ZONE_H = PILE_H + 32;

	// Polices

	private static final String FONT = "Courier New";

	private static final Font F_SCORE_BIG = new Font(FONT, Font.BOLD, 48);
	private static final Font F_CARD_RANK = new Font(FONT, Font.BOLD, 26);
	private static final Font F_CARD_SUIT = new Font(FONT, Font.BOLD, 28);
	private static final Font F_HEARTS = new Font(FONT, Font.PLAIN, 30);
	private static final Font F_SEL_LABEL = new Font(FONT, Font.BOLD, 17);
	private static final Font F_CARD_BACK = new Font(FONT, Font.BOLD, 26);
	private static final Font F_MSG = new Font(FONT, Font.BOLD, 26);

	// Cache de polices — clé = style<<8 | size
	private static final Map<Integer, Font> COURIER_CACHE = new HashMap<>();

	private static Font courier(int style, int size) {
		return COURIER_CACHE.computeIfAbsent((style << 8) | size,
				_ -> new Font(FONT, style, size));
	}

	private final Zen6Assets assets;

	/**
	 * @param assets les ressources graphiques à utiliser, non null
	 */
	public Zen6Renderer(Zen6Assets assets) {
		this.assets = assets;
	}

	/**
	 * Dessine une frame complète du jeu.
	 *
	 * @param g             le contexte graphique, non null
	 * @param w             largeur de la fenêtre en pixels
	 * @param h             hauteur de la fenêtre en pixels
	 * @param state         l'état courant de la partie, peut être null
	 * @param currentCards  les cartes en main, peut être null
	 * @param selectedCards indices des cartes sélectionnées, non null
	 * @param activeCards   cartes actives après évaluation, non null
	 * @param message       message à afficher en surimpression, non null
	 * @param messageColor  couleur du message, non null
	 * @param canPlay       {@code true} si le bouton Jouer est actif
	 * @param canDiscard    {@code true} si le bouton Défausser est actif
	 * @param discardsLeft  nombre de défausses restantes
	 * @throws NullPointerException si {@code g}, {@code selectedCards},
	 *                              {@code activeCards}, {@code message} ou
	 *                              {@code messageColor} est null
	 */
	public void renderFrame(Graphics2D g, int w, int h,
			GameState state,
			List<Card> currentCards,
			List<Integer> selectedCards,
			List<Card> activeCards,
			String message,
			Color messageColor,
			boolean canPlay,
			boolean canDiscard,
			int discardsLeft) {
		Objects.requireNonNull(g, "g must not be null");
		Objects.requireNonNull(selectedCards, "selectedCards must not be null");
		Objects.requireNonNull(activeCards, "activeCards must not be null");
		Objects.requireNonNull(message, "message must not be null");
		Objects.requireNonNull(messageColor, "messageColor must not be null");

		setupHints(g);
		g.setColor(BG);
		g.fillRect(0, 0, w, h);

		renderBackground(g, w, h);

		int midY = MARGIN + TOP_H + MARGIN;
		int midH = h - midY - BOTTOM_H - MARGIN;
		int centerX = MARGIN + LEFT_W + MARGIN;
		int centerW = w - centerX - RIGHT_W - 2 * MARGIN;
		int rightX = w - RIGHT_W - MARGIN;
		int bottomY = h - BOTTOM_H;

		renderBlindPanel(g, MARGIN, MARGIN, LEFT_W, TOP_H, state);
		renderScorePanel(g, centerX, MARGIN, centerW, TOP_H, state);
		renderCurrentBlindPanel(g, rightX, MARGIN, RIGHT_W, TOP_H, state);

		renderScoringHands(g, MARGIN, midY, LEFT_W, midH, state);
		renderPlayArea(g, centerX, midY, centerW, midH, activeCards, state);

		if (state != null) {
			renderPlanetsPanel(g, rightX, midY, RIGHT_W, midH, state);
		} else {
			drawPanel(g, rightX, midY, RIGHT_W, midH, BORDER);
		}

		renderBottomBar(g, MARGIN, bottomY, w - 2 * MARGIN, BOTTOM_H,
				currentCards, selectedCards, activeCards,
				canPlay, canDiscard, discardsLeft);

		if (message != null && !message.isEmpty()) {
			renderMessage(g, w, h, message, messageColor);
		}
	}

	private void renderBackground(Graphics2D g, int w, int h) {
		var bg = assets.getBackground();
		if (bg != null) {
			g.drawImage(bg, 0, 0, w, h, null);
			g.setColor(new Color(0, 0, 0, 130));
			g.fillRect(0, 0, w, h);
		}
	}

	private void renderBlindPanel(Graphics2D g, int x, int y, int pw, int ph, GameState state) {
		drawPanel(g, x, y, pw, ph, BORDER);
		int handsLeft = state != null ? state.getHandsRemaining() : 0;
		int discardsLeft = state != null ? state.getDiscardsRemaining() : 0;
		int maxHands = state != null ? state.getHandsPerBlind() : handsLeft;

		int cx = x + pw / 2;
		int ty = y + 22;

		txtC(g, "MAINS", cx, ty, 16, Font.BOLD, MUTED);
		ty += 22;
		g.setFont(F_HEARTS);
		int heartsW = maxHands * 32 - 4;
		int hx = cx - heartsW / 2;
		for (int i = 0; i < maxHands; i++) {
			g.setColor(i < handsLeft ? RED_DARK : new Color(0x3c, 0x3c, 0x3c));
			g.drawString(i < handsLeft ? "♥" : "♡", hx + i * 32, ty + 22);
		}
		ty += 44;

		g.setColor(BORDER);
		g.drawLine(x + 16, ty, x + pw - 16, ty);
		ty += 14;

		txtC(g, "DÉFAUSSES", cx, ty, 16, Font.BOLD, MUTED);
		ty += 20;
		txtC(g, String.valueOf(discardsLeft), cx, ty + 18, 32, Font.BOLD,
				discardsLeft > 0 ? BLUE_COLD : new Color(0x3c, 0x3c, 0x3c));
	}

	private void renderScorePanel(Graphics2D g, int x, int y, int pw, int ph, GameState state) {
		drawPanel(g, x, y, pw, ph, BORDER_SCR);
		int cx = x + pw / 2;
		txtC(g, "SCORE EN COURS", cx, y + 20, 21, Font.BOLD, MUTED);

		var blind = (state != null && !state.isGameWon()) ? state.getCurrentBlind() : null;
		int current = state != null ? state.getCurrentScore() : 0;
		int target = blind != null ? blind.targetScore() : 0;

		g.setFont(F_SCORE_BIG);
		g.setColor(GOLD);
		FontMetrics fm = g.getFontMetrics();
		String s = String.valueOf(current);
		g.drawString(s, cx - fm.stringWidth(s) / 2, y + 62);

		if (target > 0) {
			txtC(g, "sur " + target + " pts", cx, y + 80, 18, Font.PLAIN, MUTED);
		}

		float pct = target > 0 ? Math.min(1f, (float) current / target) : 0f;
		drawProgressBar(g, x + 20, y + 88, pw - 40, 12, pct);
		txtC(g, Math.round(pct * 100) + " %", cx, y + 130, 18, Font.BOLD,
				pct >= 1f ? new Color(0x3a, 0x8a, 0x3a) : MUTED);
	}

	private void renderCurrentBlindPanel(Graphics2D g, int x, int y, int pw, int ph, GameState state) {
		drawPanel(g, x, y, pw, ph, BORDER);

		if (state == null || state.isGameWon()) {
			txtC(g, "VICTOIRE", x + pw / 2, y + ph / 2 + 6, 18, Font.BOLD, GOLD);
			return;
		}

		var blind = state.getCurrentBlind();
		String blindName = blind.name().toUpperCase();
		int cx = x + pw / 2;

		Color nameColor = blindName.contains("BOSS") ? new Color(0xf0, 0x46, 0x46)
				: blindName.contains("GRAND") ? new Color(0xff, 0x96, 0x3c)
						: GOLD;

		txtC(g, blindName, cx, y + 45, 20, Font.BOLD, nameColor);
		g.setColor(BORDER);
		g.drawLine(x + 16, y + 58, x + pw - 16, y + 58);
		txtC(g, "BLIND " + state.getCurrentBlindNumber() + "  /  " + state.getTotalBlinds(),
				cx, y + 80, 17, Font.PLAIN, MUTED);
		txtC(g, "Cible : " + blind.targetScore() + " pts",
				cx, y + 105, 16, Font.BOLD, MUTED);
	}

	private void renderScoringHands(Graphics2D g, int x, int y, int pw, int ph, GameState state) {
		drawPanel(g, x, y, pw, ph, BORDER);
		txtC(g, "MAINS SCORANTES", x + pw / 2, y + 19, 18, Font.BOLD, GOLD);
		g.setColor(BORDER);
		g.drawLine(x + 8, y + 26, x + pw - 8, y + 26);

		HandRank[] ranks = HandRank.values();
		int rowH = (ph - 32) / ranks.length;

		for (int i = 0; i < ranks.length; i++) {
			HandRank hr = ranks[i];
			int ry = y + 30 + i * rowH;
			int chips = state != null ? state.getChips(hr) : hr.getBaseChips();
			int mult = state != null ? state.getMult(hr) : hr.getBaseMult();
			boolean boosted = chips > hr.getBaseChips() || mult > hr.getBaseMult();

			if (boosted) {
				g.setColor(new Color(0x50, 0xdc, 0x78, 25));
				g.fillRoundRect(x + 4, ry + 1, pw - 8, rowH - 1, 4, 4);
			} else if (i % 2 == 0) {
				g.setColor(new Color(0x18, 0x14, 0x0a, 60));
				g.fillRoundRect(x + 4, ry + 1, pw - 8, rowH - 1, 4, 4);
			}
			Color nc = boosted ? GREEN_BOOST : new Color(0x8a, 0x7a, 0x6a);
			txt(g, hr.getLabel(), x + 8, ry + rowH - 5, 15, boosted ? Font.BOLD : Font.PLAIN, nc);
			txt(g, String.valueOf(chips), x + pw - 66, ry + rowH - 5, 15, Font.BOLD, BLUE_COLD);
			txt(g, "×" + mult, x + pw - 36, ry + rowH - 5, 15, Font.BOLD, ORANGE_FIRE);

			if (i < ranks.length - 1) {
				g.setColor(new Color(0x2a, 0x20, 0x12));
				g.drawLine(x + 8, ry + rowH, x + pw - 8, ry + rowH);
			}
		}
	}

	private void renderPlayArea(Graphics2D g, int x, int y, int pw, int ph,
			List<Card> active, GameState state) {
		g.setColor(PANEL_BG);
		g.fillRoundRect(x, y, pw, ph, CORNER, CORNER);
		float[] dash = { 8f, 6f };
		g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, dash, 0f));
		g.setColor(BORDER);
		g.drawRoundRect(x, y, pw, ph, CORNER, CORNER);
		g.setStroke(new BasicStroke(1f));

		int activeAreaH = ph - PILE_ZONE_H - MARGIN;

		if (active == null || active.isEmpty()) {
			txtC(g, "ZONE DE JEU", x + pw / 2, y + activeAreaH / 2 + 6,
					18, Font.BOLD, new Color(0x2a, 0x2a, 0x1a));
		} else {
			int cw = CARD_W, ch = CARD_H, gap = 10;
			int totalW = active.size() * (cw + gap) - gap;
			int cx = x + pw / 2 - totalW / 2;
			int cy = y + activeAreaH / 2 - ch / 2;
			for (int i = 0; i < active.size(); i++) {
				renderCard(g, active.get(i), cx + i * (cw + gap), cy, cw, ch, false, true);
			}
		}

		int deckCount = state != null ? state.getDeck().drawPileSize() : 52;
		int discardCount = state != null ? state.getDeck().discardPileSize() : 0;

		int pileY = y + ph - PILE_ZONE_H - 40;
		int leftCx = x + pw / 4;
		int rightCx = x + 3 * pw / 4;
		int pileX1 = leftCx - PILE_W / 2;
		int pileX2 = rightCx - PILE_W / 2;

		g.setColor(new Color(0x2a, 0x20, 0x12));
		g.drawLine(x + 12, pileY - 6, x + pw - 12, pileY - 6);

		int stack = Math.min(deckCount, 5);
		for (int i = stack - 1; i >= 0; i--) {
			renderCardBack(g, pileX1 + i, pileY - i, PILE_W, PILE_H);
		}
		txtC(g, "Pioche", leftCx, pileY + PILE_H + 16, 18, Font.BOLD, MUTED);
		txtC(g, String.valueOf(deckCount), leftCx, pileY + PILE_H + 36, 18, Font.PLAIN, GOLD);

		if (discardCount > 0) {
			int ds = Math.min(discardCount, 4);
			for (int i = ds - 1; i >= 0; i--) {
				renderCardBack(g, pileX2 + i, pileY - i, PILE_W, PILE_H);
			}
		} else {
			g.setColor(new Color(0x08, 0x05, 0x10));
			g.fillRoundRect(pileX2, pileY, PILE_W, PILE_H, 6, 6);
			float[] d = { 5f, 4f };
			g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, d, 0f));
			g.setColor(BORDER);
			g.drawRoundRect(pileX2, pileY, PILE_W, PILE_H, 6, 6);
			g.setStroke(new BasicStroke(1f));
		}
		txtC(g, "Défausse", rightCx, pileY + PILE_H + 16, 18, Font.BOLD, MUTED);
		txtC(g, String.valueOf(discardCount), rightCx, pileY + PILE_H + 36, 18, Font.PLAIN, GOLD);
	}

	private void renderPlanetsPanel(Graphics2D g, int x, int y, int pw, int ph, GameState state) {
		drawPanel(g, x, y, pw, ph, BORDER);
		txtC(g, "PLANÈTES", x + pw / 2, y + 19, 18, Font.BOLD, GOLD);
		g.setColor(BORDER);
		g.drawLine(x + 8, y + 26, x + pw - 8, y + 26);

		Planet[] planets = Planet.values();
		int rowH = (ph - 32) / planets.length;

		for (int i = 0; i < planets.length; i++) {
			Planet planet = planets[i];
			int ry = y + 30 + i * rowH;
			if (ry + rowH > y + ph - 4) {
				break;
			}

			HandRank hr = planet.getTarget();
			boolean boosted = state.getChips(hr) > hr.getBaseChips()
					|| state.getMult(hr) > hr.getBaseMult();

			if (boosted) {
				g.setColor(new Color(0x50, 0xdc, 0x78, 25));
				g.fillRoundRect(x + 4, ry + 1, pw - 8, rowH - 1, 4, 4);
			} else if (i % 2 == 0) {
				g.setColor(new Color(0x18, 0x14, 0x0a, 60));
				g.fillRoundRect(x + 4, ry + 1, pw - 8, rowH - 1, 4, 4);
			}

			Color nc = boosted ? GREEN_BOOST : new Color(0x8a, 0x7a, 0x6a);
			txt(g, planet.getLabel(), x + 8, ry + rowH - 5, 15, boosted ? Font.BOLD : Font.PLAIN, nc);
			txt(g, "+" + planet.getBonusChips(), x + pw - 66, ry + rowH - 5, 15, Font.BOLD, BLUE_COLD);
			txt(g, "+" + planet.getBonusMult(), x + pw - 36, ry + rowH - 5, 15, Font.BOLD, ORANGE_FIRE);

			if (i < planets.length - 1) {
				g.setColor(new Color(0x2a, 0x20, 0x12));
				g.drawLine(x + 8, ry + rowH, x + pw - 8, ry + rowH);
			}
		}
	}

	private void renderBottomBar(Graphics2D g, int x, int y, int pw, int ph,
			List<Card> cards, List<Integer> selected, List<Card> active,
			boolean canPlay, boolean canDiscard, int discardsLeft) {
		drawPanel(g, x, y, pw, ph, BORDER);

		int selCount = selected != null ? selected.size() : 0;
		String instr = "Sélectionnez vos cartes  (" + selCount + " / " + Hand.SIZE + ")";
		g.setFont(F_SEL_LABEL);
		FontMetrics fm = g.getFontMetrics();
		int iw = fm.stringWidth(instr);
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRoundRect(x + pw / 2 - iw / 2 - 10, y + 5, iw + 20, 20, 6, 6);
		g.setColor(GOLD);
		g.drawString(instr, x + pw / 2 - iw / 2, y + 19);

		int btnW = Math.min(220, (pw - 60) / 2);
		int gap = 16;
		int btnsW = 2 * btnW + gap;
		int btnX = x + pw / 2 - btnsW / 2;
		int btnY = y + 26;

		String discardLabel = "DÉFAUSSER (" + discardsLeft + ")";
		Color dBg = canDiscard ? new Color(0x1a, 0x1a, 0x4a) : new Color(0x10, 0x10, 0x18);
		Color dBorder = canDiscard ? new Color(0x3a, 0x3a, 0x8a) : new Color(0x1e, 0x1e, 0x2a);
		Color dTc = canDiscard ? new Color(0x6a, 0x6a, 0xc8) : new Color(0x32, 0x32, 0x42);
		drawButton(g, btnX, btnY, btnW, BTN_H_INNER, discardLabel, dBg, dBorder, dTc);

		Color jBg = canPlay ? new Color(0x1a, 0x40, 0x1a) : new Color(0x10, 0x18, 0x10);
		Color jBorder = canPlay ? new Color(0x3a, 0x8a, 0x3a) : new Color(0x1e, 0x2a, 0x1e);
		Color jTc = canPlay ? new Color(0x5a, 0xc8, 0x5a) : new Color(0x30, 0x42, 0x30);
		drawButton(g, btnX + btnW + gap, btnY, btnW, BTN_H_INNER, "JOUER LA MAIN", jBg, jBorder, jTc);

		if (cards != null && !cards.isEmpty()) {
			int totalW = cards.size() * SPACING - (SPACING - CARD_W);
			int startX = x + pw / 2 - totalW / 2;
			int baseY = y + ph - CARD_H - 10;

			for (int i = 0; i < cards.size(); i++) {
				boolean isSel = selected != null && selected.contains(i);
				boolean isActive = active != null && active.contains(cards.get(i));
				int cx = startX + i * SPACING;
				int cy = isSel ? baseY - 18 : baseY;

				g.setColor(new Color(0, 0, 0, 110));
				g.fillRoundRect(cx + 4, cy + 6, CARD_W, CARD_H, 12, 12);
				renderCard(g, cards.get(i), cx, cy, CARD_W, CARD_H, isSel, isActive);
			}
		}
	}

	private void renderCard(Graphics2D g, Card card, int x, int y, int w, int h,
			boolean selected, boolean active) {
		String key = card.rank().name() + "_" + card.suit().name();
		var img = assets.getCardImage(key);

		if (img != null) {
			if (selected) {
				g.setColor(new Color(0xc8, 0xa9, 0x6e, 90));
				g.fillRoundRect(x - 4, y - 4, w + 8, h + 8, CORNER + 2, CORNER + 2);
			}
			if (active) {
				g.setColor(ACTIVE_FILL);
				g.fillRoundRect(x - 4, y - 4, w + 8, h + 8, CORNER + 2, CORNER + 2);
			}
			g.drawImage(img, x, y, w, h, null);
			if (selected) {
				g.setColor(SEL_GOLD);
				g.setStroke(new BasicStroke(2.5f));
				g.drawRoundRect(x, y, w, h, CORNER, CORNER);
				g.setStroke(new BasicStroke(1f));
			}
			if (active) {
				g.setColor(ACTIVE_ORG);
				g.setStroke(new BasicStroke(2.5f));
				g.drawRoundRect(x, y, w, h, CORNER, CORNER);
				g.setStroke(new BasicStroke(1f));
			}
		} else {
			g.setColor(selected ? new Color(0x20, 0x18, 0x08) : new Color(0x1a, 0x12, 0x0a));
			g.fillRoundRect(x, y, w, h, CORNER, CORNER);
			g.setColor(selected ? GOLD : active ? ACTIVE_ORG : BORDER);
			g.setStroke(new BasicStroke(selected || active ? 2f : 1f));
			g.drawRoundRect(x, y, w, h, CORNER, CORNER);
			g.setStroke(new BasicStroke(1f));

			boolean isRed = card.suit() == Suit.HEARTS || card.suit() == Suit.DIAMONDS;
			Color cc = isRed ? CARD_RED : CARD_BLACK;
			String sym = card.suit().getSymbol();
			g.setFont(F_CARD_RANK);
			g.setColor(cc);
			g.drawString(card.rank().getLabel(), x + 6, y + 22);
			g.drawString(sym, x + 6, y + 38);
			g.setFont(F_CARD_SUIT);
			FontMetrics fm = g.getFontMetrics();
			g.drawString(sym, x + w / 2 - fm.stringWidth(sym) / 2, y + h / 2 + 10);
		}
	}

	private void renderCardBack(Graphics2D g, int x, int y, int w, int h) {
		g.setColor(new Color(0x1a, 0x0d, 0x2a));
		g.fillRoundRect(x, y, w, h, 8, 8);
		g.setColor(new Color(0x4a, 0x2a, 0x5a));
		g.drawRoundRect(x, y, w, h, 8, 8);
		g.setFont(F_CARD_BACK);
		g.setColor(new Color(0x5a, 0x3a, 0x6a));
		FontMetrics fm = g.getFontMetrics();
		String sym = "◆";
		g.drawString(sym, x + w / 2 - fm.stringWidth(sym) / 2, y + h / 2 + 6);
	}

	private void renderMessage(Graphics2D g, int w, int h, String msg, Color color) {
		g.setFont(F_MSG);
		FontMetrics fm = g.getFontMetrics();
		int mw = fm.stringWidth(msg), mh = 64;
		int mx = w / 2 - mw / 2, my = h / 2 - mh / 2;
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRoundRect(mx - 20, my - 14, mw + 40, mh + 28, 12, 12);
		g.setColor(color.darker());
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(mx - 20, my - 14, mw + 40, mh + 28, 12, 12);
		g.setStroke(new BasicStroke(1f));
		g.setColor(color);
		g.drawString(msg, mx, my + mh / 2 + 8);
	}

	private void drawPanel(Graphics2D g, int x, int y, int w, int h, Color border) {
		g.setColor(PANEL_BG);
		g.fillRoundRect(x, y, w, h, CORNER, CORNER);
		g.setColor(border);
		g.drawRoundRect(x, y, w, h, CORNER, CORNER);
	}

	private void drawButton(Graphics2D g, int x, int y, int w, int h,
			String label, Color bg, Color border, Color tc) {
		g.setColor(bg);
		g.fillRoundRect(x, y, w, h, CORNER, CORNER);
		g.setColor(border);
		g.drawRoundRect(x, y, w, h, CORNER, CORNER);
		txtC(g, label, x + w / 2, y + h / 2 + 5, 15, Font.BOLD, tc);
	}

	private void drawProgressBar(Graphics2D g, int x, int y, int w, int h, float progress) {
		g.setColor(new Color(0x08, 0x05, 0x02));
		g.fillRoundRect(x, y, w, h, h, h);
		float c = Math.max(0f, Math.min(1f, progress));
		if (c > 0f) {
			g.setColor(c >= 1f ? new Color(0x3a, 0x8a, 0x3a) : new Color(0x7a, 0x2a, 0x1a));
			g.fillRoundRect(x, y, Math.max(h, (int) (w * c)), h, h, h);
		}
		g.setColor(new Color(0x3a, 0x1a, 0x0a));
		g.drawRoundRect(x, y, w, h, h, h);
	}

	private void txt(Graphics2D g, String t, int x, int y, int size, int style, Color c) {
		g.setFont(courier(style, size));
		g.setColor(c);
		g.drawString(t, x, y);
	}

	private void txtC(Graphics2D g, String t, int cx, int cy, int size, int style, Color c) {
		g.setFont(courier(style, size));
		g.setColor(c);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(t, cx - fm.stringWidth(t) / 2, cy);
	}

	private void setupHints(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
}