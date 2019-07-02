package colin29.memoriesofsword.game.matchscreen;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import colin29.memoriesofsword.App;
import colin29.memoriesofsword.MyFonts;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.Amulet;
import colin29.memoriesofsword.game.match.Attackable;
import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.Card.Type;
import colin29.memoriesofsword.game.match.CardInfo;
import colin29.memoriesofsword.game.match.Follower;
import colin29.memoriesofsword.game.match.ListOfCardsEmptyException;
import colin29.memoriesofsword.game.match.Match;
import colin29.memoriesofsword.game.match.Permanent;
import colin29.memoriesofsword.game.match.Player;
import colin29.memoriesofsword.game.match.SimpleMatchStateListener;
import colin29.memoriesofsword.game.matchscreen.graphics.AmuletGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.FollowerGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.HandCardGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.PermanentGraphic;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;
import colin29.memoriesofsword.util.template.AppWithResources;
import colin29.memoriesofsword.util.template.BaseScreen;

/**
 * A Match screen will create a match when it is created. Then when the match finishes, it will call whatever screen it likes.
 *
 */
public class MatchScreen extends BaseScreen implements InputProcessor, SimpleMatchStateListener {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private Match match;

	final Skin skin = App.getSkin();
	final MyFonts fonts;

	InputMultiplexer multiplexer = new InputMultiplexer();

	private final int NUM_PLAYERS = 2;
	/**
	 * Use the helper {@link #getUIElements(int), getComponentAt} to access
	 */
	PlayerPartitionUIElements[] playerUIElements = new PlayerPartitionUIElements[NUM_PLAYERS];

	AppWithResources app;

	DragAndDrop dragAndDrop = new DragAndDrop();
	DragAndDrop dadAttacking = new DragAndDrop();

	OutlineRenderer outlineRenderer;

	Table infoPanel; // shows detailed information about a clicked permanent

	/**
	 * Represents the line to draw between follower and cursor, when the user is dragging to attack
	 */
	final Segment attackingLine = new Segment();
	boolean attackingLineVisible = true;

	public MatchScreen(AppWithResources app, CardRepository cardRepo) {
		super(app);
		this.app = app;
		fonts = app.getFonts();

		outlineRenderer = new OutlineRenderer(app.getShapeRenderer());
		for (int i = 0; i < NUM_PLAYERS; i++) {
			playerUIElements[i] = new PlayerPartitionUIElements(outlineRenderer, i + 1);
		}

		/// Set up Match

		match = new Match(cardRepo);

		match.useTestDecks();
		match.beginMatch();
		constructUI();
		match.addSimpleStateListener(this);

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(stage);
	}

	private int sidePanelWidth = 150;

	int cardGraphicWidth = 80;
	int cardGraphicHeight = 100;

	private int permanentGraphicWidth = 80;
	private int permanentGraphicHeight = 100;

	/**
	 * Also Initializes fields which refer to UI elements, thus regenerate/refresh calls (including those from a listener) should not be invoked
	 * before this method is called.
	 */
	private void constructUI() {

		constructPlayerPartition(2);
		constructPlayerPartition(1); // Player 1's area should be on the bottom

		makeValidHandCardsDraggable();

		root.pack();
	}

	private void constructPlayerPartition(int playerNumber) {

		final boolean normalOrientation = playerNumber == 1; // normal orientation has your hand on the bottom of your area

		PlayerPartitionUIElements elements = getUIElements(playerNumber);

		Table playerPartition = new Table();

		root.add(playerPartition).height(Value.percentHeight(0.5f, root)).expandX().fill();
		root.row();

		playerPartition.add(constructSidePanel(playerNumber)).width(sidePanelWidth).expandY().fill();
		Table mainArea = new Table();
		playerPartition.add(mainArea).expand().fill();

		TextButtonStyle bigTextButtonStyle = skin.get(TextButtonStyle.class);
		bigTextButtonStyle.font = fonts.largishFont();

		TextButton endTurnButton = new TextButton("End Turn", bigTextButtonStyle);
		endTurnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (playerNumber == match.getActivePlayerNumber()) {
					match.nextTurn();
				}

			}
		});
		elements.endTurnButton = endTurnButton;
		updateEndTurnButtonDisabledStatus(playerNumber);
		endTurnButton.pad(20, 10, 20, 10);

		Table fieldPanel = new Table();
		fieldPanel.defaults().space(20);
		Table handPanel = new Table();
		if (normalOrientation) {
			mainArea.add(fieldPanel).expand().fill();
			mainArea.add(endTurnButton);
			mainArea.row();
			mainArea.add(handPanel).expandX().colspan(2).fill();
		} else {
			mainArea.add(handPanel).expandX().colspan(2).fill();
			mainArea.row();
			mainArea.add(fieldPanel).expand().fill();
			mainArea.add(endTurnButton);
		}

		elements.handPanel = handPanel;
		elements.fieldPanel = fieldPanel;

		regenerateHandDisplay(playerNumber);
		regenerateFieldDisplay(playerNumber);

		fieldPanel.setTouchable(Touchable.enabled);

	}

	private Table constructSidePanel(int playerNumber) {
		Table sidePanel = new Table();
		sidePanel.defaults().expandX().left();

		Color DARK_GRAY = RenderUtil.rgb(40, 40, 40);
		Color DARK_RED = RenderUtil.rgb(128, 0, 0);

		Label nameText = new Label(match.getPlayer(playerNumber).getName(), skin);
		RenderUtil.setLabelBackgroundColor(nameText, DARK_GRAY);

		Label hpText = new Label("initial hp text", skin);
		RenderUtil.setLabelBackgroundColor(hpText, DARK_RED);

		Label aTwoDigitLabel = new Label("00", skin); // example label for sizing purposes
		int hpTextPadding = 8;
		Container<Label> hpTextWrapper = new Container<Label>(hpText);
		hpTextWrapper.size(aTwoDigitLabel.getWidth() + hpTextPadding,
				hpText.getHeight() + hpTextPadding);
		hpText.setAlignment(Align.center);

		Label playPointsText = new Label("intial pp text", skin);
		RenderUtil.setLabelBackgroundColor(playPointsText, DARK_GRAY);

		Table zoneCountPanel = new Table(); // contains counts for current graveyard, deck, and hand size
		zoneCountPanel.defaults().left().padLeft(5).padRight(5).spaceLeft(10);
		zoneCountPanel.pad(5);
		zoneCountPanel.setBackground(RenderUtil.getSolidBG(DARK_GRAY));

		Label graveyardCountText = new Label("", skin);
		Label deckCountText = new Label("", skin);
		Label handCountText = new Label("", skin);

		zoneCountPanel.add(new Label("Graveyard:", skin));
		zoneCountPanel.add(graveyardCountText).row();
		zoneCountPanel.add(new Label("Deck:", skin));
		zoneCountPanel.add(deckCountText).row();
		zoneCountPanel.add(new Label("Hand:", skin));
		zoneCountPanel.add(handCountText);

		// add ui elements to panel

		sidePanel.defaults().padLeft(5).padRight(5).spaceTop(5);

		sidePanel.add(nameText);
		sidePanel.row();
		sidePanel.add(hpTextWrapper).center();
		sidePanel.row();
		sidePanel.add(playPointsText).center();
		sidePanel.row();
		sidePanel.add(zoneCountPanel).expandX();

		PlayerPartitionUIElements elements = getUIElements(playerNumber);

		// store reference to elements that need to be updated

		elements.hpText = hpText;
		elements.playPointsText = playPointsText;

		elements.graveyardCountText = graveyardCountText;
		elements.deckCountText = deckCountText;
		elements.handCountText = handCountText;

		updateHpText(playerNumber);
		updatePlayPointsText(playerNumber);
		updateZoneCountTexts(playerNumber);

		return sidePanel;
	}

	private final Color hpTextWoundedColor = RenderUtil.rgb(255, 128, 128); // pale red

	private void updateHpText(int playerNumber) {
		Label hpText = getUIElements(playerNumber).hpText;
		Player player = match.getPlayer(playerNumber);
		hpText.setText("" + player.getHp());
		if (player.getHp() != player.getMaxHp()) {
			hpText.setColor(hpTextWoundedColor);
		} else {
			hpText.setColor(Color.WHITE);
		}
	}

	private void updatePlayPointsText(int playerNumber) {
		Label playPointsText = getUIElements(playerNumber).playPointsText;
		Player player = match.getPlayer(playerNumber);
		playPointsText.setText("PP: " + player.getPlayPoints() + " / " + player.getMaxPlayPoints());
	}

	private void updateZoneCountTexts(int playerNumber) {

		PlayerPartitionUIElements elements = getUIElements(playerNumber);
		Player player = match.getPlayer(playerNumber);

		elements.graveyardCountText.setText("" + player.getGraveyard().size());
		elements.deckCountText.setText("" + player.getDeck().size());
		elements.handCountText.setText("" + player.getHand().size());

	}

	private void updateAllEndTurnButtonDisabledStatus() {
		for (int playerNumber = 1; playerNumber <= 2; playerNumber++) {
			updateEndTurnButtonDisabledStatus(playerNumber);
		}
	}

	private void updateEndTurnButtonDisabledStatus(int playerNumber) {
		PlayerPartitionUIElements elements = getUIElements(playerNumber);
		if (playerNumber == match.getActivePlayerNumber()) {
			elements.endTurnButton.setDisabled(false);
		} else {
			elements.endTurnButton.setDisabled(true);
		}

	}

	private void regenerateFieldDisplay(int playerNumber) {
		List<Permanent<?>> entitiesOnField = match.getPlayer(playerNumber).getFieldInfo();

		PlayerPartitionUIElements elements = getUIElements(playerNumber);
		elements.listOfFieldGraphics.clear();

		Table fieldPanel = getUIElements(playerNumber).fieldPanel;
		fieldPanel.clear();
		for (Permanent<?> entity : entitiesOnField) {
			PermanentGraphic p = createPermanentGraphic(entity);
			fieldPanel.add(p).size(permanentGraphicWidth, permanentGraphicHeight);
			elements.listOfFieldGraphics.add(p);
		}
		makeValidUnitsAttackDraggable();
	}

	final Color DARK_BLUE = RenderUtil.rgb(51, 51, 204);
	final Color DARK_RED = RenderUtil.rgb(128, 0, 0);
	final Color FOREST = Color.FOREST;

	private PermanentGraphic createPermanentGraphic(final Permanent permanent) {

		// Should have a label that shows the card name

		PermanentGraphic permanentGraphic;

		if (permanent instanceof Follower) {
			permanentGraphic = new FollowerGraphic((Follower) permanent);
		} else if (permanent instanceof Amulet) {
			permanentGraphic = new AmuletGraphic((Amulet) permanent);
		} else {
			permanentGraphic = new PermanentGraphic(permanent);
		}

		// Set the background card art

		setPermanentGraphicBackGround(permanentGraphic);
		permanentGraphic.bottom().defaults().space(10);

		if (permanent instanceof Follower) {
			final Follower follower = (Follower) permanent;

			LabelStyle style = createLabelStyle(fonts.permanentStatsBorderedText());
			LabelStyle woundedTextStyle = createLabelStyle(fonts.damagedFollowerDefText());
			LabelStyle buffedTextStyle = createLabelStyle(fonts.buffedFollowerDefText());

			Label atkText = new Label(String.valueOf(follower.getAtk()), style);
			Label defText = new Label(String.valueOf(follower.getDef()), style);

			if (follower.isAtkGreaterThanOrig()) {
				atkText.setStyle(buffedTextStyle);
			}

			if (follower.isWounded()) {
				defText.setStyle(woundedTextStyle);
			} else if (follower.isDefGreaterThanOrig()) {
				defText.setStyle(buffedTextStyle);
			}

			RenderUtil.setLabelBackgroundColor(atkText, DARK_BLUE);
			RenderUtil.setLabelBackgroundColor(defText, DARK_RED);

			atkText.setAlignment(Align.center);
			defText.setAlignment(Align.center);
			permanentGraphic.add(atkText).size(atkText.getWidth() + 7, atkText.getHeight() + 1);
			permanentGraphic.add(defText).size(defText.getWidth() + 7, defText.getHeight() + 1);
		}
		permanentGraphic.setTouchable(Touchable.enabled);
		makeClickShowInfoPanel(permanentGraphic);
		return permanentGraphic;
	}

	private void setPermanentGraphicBackGround(PermanentGraphic permanentGraphic) {
		Texture img = assets.get("img/image01.jpg", Texture.class);
		TextureRegionDrawable imgDrawable = new TextureRegionDrawable(new TextureRegion(img));
		permanentGraphic.setBackground(imgDrawable);
	}

	private void makeClickShowInfoPanel(PermanentGraphic graphic) {
		graphic.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				logger.debug("{} was clicked", graphic.getPermanent().getName());
				createAndDisplayInfoPanel(graphic);
			}
		});
	}

	private void makeClickShowInfoPanel(HandCardGraphic graphic) {
		graphic.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				logger.debug("{} was clicked", graphic.getParentCard().getName());
				createAndDisplayInfoPanel(graphic);
			}
		});
	}

	private void createAndDisplayInfoPanel(PermanentGraphic graphic) {
		if (infoPanel != null) {
			removeInfoPanel();
		}
		this.infoPanel = createInfoPanel(graphic.getPermanent());
	}

	private void createAndDisplayInfoPanel(HandCardGraphic graphic) {
		if (infoPanel != null) {
			removeInfoPanel();
		}
		this.infoPanel = createInfoPanel(graphic.getParentCard());
	}

	private Table createInfoPanel(Permanent<?> permanent) {
		InfoTable infoTable = createInfoTableSkeleton(permanent.getParentCard());
		infoTable.elements.origEffectsText.setText(permanent.generateOrigEffectsText());
		return infoTable;
	}

	private Table createInfoPanel(CardInfo card) {
		InfoTable infoTable = createInfoTableSkeleton(card);
		infoTable.elements.origEffectsText.setText(card.generateOrigEffectsText());
		return infoTable;
	}

	/**
	 * Creates the shape of the info table and fills in all the areas in common (Selected target can be Card or Permanent)
	 * 
	 * @param card:
	 *            If the selected target is a card, that card. If the selected targeted is a permanent, its parent card.
	 * 
	 * @return References to table elements so that callers can fill in the rest
	 */
	public InfoTable createInfoTableSkeleton(final CardInfo card) {

		int infoPanelWidth = 300;

		InfoTable rootTemp = new InfoTable();

		rootTemp.setFillParent(true);
		stage.addActor(rootTemp);

		Table info = new Table();
		info.setBackground(RenderUtil.getSolidBG(Color.DARK_GRAY));

		LabelStyle largishStyle = createLabelStyle(fonts.largishFont());
		Label nameText = new Label(card.getName(), largishStyle);

		Table statsRow = new Table();
		statsRow.defaults().uniform().fill().spaceRight(10);
		statsRow.left();

		int statLabelWidth = 15;

		Label costText = createColoredLabel(String.valueOf(card.getCost()), largishStyle, FOREST, Align.center);

		// generateOrigEffectsText()

		statsRow.defaults().width(statLabelWidth);

		statsRow.add(costText);

		if (card.getType() == Type.FOLLOWER) {
			Label atkText = createColoredLabel(String.valueOf(card.getAtk()), largishStyle, DARK_BLUE, Align.center);
			Label defText = createColoredLabel(String.valueOf(card.getDef()), largishStyle, DARK_RED, Align.center);

			statsRow.add(atkText);
			statsRow.add(defText);
		}

		statsRow.row();

		info.defaults().space(5);
		info.pad(10).left();
		info.defaults().left();

		info.add(nameText).row();
		info.add(statsRow).row();

		Label cardText = createColoredLabel("", largishStyle, Color.BLACK, Align.left);

		cardText.setWrap(true);
		info.add(cardText).expandX().fillX();

		Table effectsPanel = new Table(); // technically only for applied effects
		effectsPanel.setBackground(RenderUtil.getSolidBG(Color.DARK_GRAY));

		Label sampleEffectText = createColoredLabel("{Applied effects show up here}", largishStyle, Color.BLACK, Align.left);

		effectsPanel.pad(10).left();
		effectsPanel.defaults().space(5).expandX().fillX();
		effectsPanel.add(sampleEffectText).row();

		rootTemp.top().left().pad(10);
		rootTemp.defaults().space(10).left().uniformX().fillX();
		rootTemp.add(info).top().width(infoPanelWidth).left().row();
		rootTemp.add(effectsPanel);

		rootTemp.elements.origEffectsText = cardText;
		rootTemp.elements.appliedEffectsPanel = effectsPanel;

		return rootTemp;
	}

	private class InfoTable extends Table {
		public final InfoTableElements elements = new InfoTableElements();
	}

	private class InfoTableElements {
		Label origEffectsText;
		Table appliedEffectsPanel;
	}

	private void removeInfoPanel() {
		infoPanel.remove();
		infoPanel = null;
	}

	/**
	 * Creates a label style, retaining the original font color
	 */
	private LabelStyle createLabelStyle(BitmapFont font) {
		return new LabelStyle(font, font.getColor());
	}

	private Label createColoredLabel(String text, LabelStyle style, Color bgColor, int align) {
		Label l = new Label(text, style);
		RenderUtil.setLabelBackgroundColor(l, bgColor);
		l.setAlignment(align);
		return l;
	}

	private void regenerateHandDisplay(int playerNumber) {
		PlayerPartitionUIElements elements = getUIElements(playerNumber);

		elements.listOfHandGraphics.clear();

		Table handPanel = elements.handPanel;
		handPanel.clear();

		List<CardInfo> p1CardsInHand = match.getPlayer(playerNumber).getHand();
		for (CardInfo card : p1CardsInHand) {
			HandCardGraphic cardGraphic = createHandCardGraphic(card);
			cardGraphic.setTouchable(Touchable.enabled);
			handPanel.add(cardGraphic).width(cardGraphicWidth);

			elements.listOfHandGraphics.add(cardGraphic);
		}
	}

	private HandCardGraphic createHandCardGraphic(CardInfo card) {

		HandCardGraphic cardGraphic = new HandCardGraphic(card);
		Table cardHeader = new Table();
		Table cardBody = new Table();

		LabelStyle smallStyle = createLabelStyle(fonts.smallFont());
		LabelStyle mediumStyle = createLabelStyle(fonts.mediumFont());
		LabelStyle largishStyle = createLabelStyle(fonts.smallFont());

		// Set the background card art
		Texture img = assets.get("img/image01.jpg", Texture.class);
		TextureRegionDrawable imgDrawable = new TextureRegionDrawable(new TextureRegion(img));
		cardBody.setBackground(imgDrawable);

		// make the cost symbol, etc.

		Label costText = new Label(String.valueOf(card.getCost()), largishStyle);
		costText.setAlignment(Align.top);

		Table costPanel = new Table();
		costPanel.add(costText);
		costPanel.setBackground(RenderUtil.getSolidBG(Color.FOREST));

		Label nameText = new Label(card.getName(), smallStyle);
		nameText.setEllipsis(true);
		nameText.setEllipsis("..");

		RenderUtil.setLabelBackgroundColor(nameText, Color.GRAY);

		cardBody.top().left();

		cardBody.add(cardHeader).expandX().fillX().row();

		if (card.getType() == Type.FOLLOWER) {

			Color DARK_BLUE = RenderUtil.rgb(51, 51, 204);
			Color DARK_RED = RenderUtil.rgb(128, 0, 0);

			Label atkText = new Label(String.valueOf(card.getAtk()), mediumStyle);
			RenderUtil.setLabelBackgroundColor(atkText, DARK_BLUE);
			Label defText = new Label(String.valueOf(card.getDef()), mediumStyle);
			RenderUtil.setLabelBackgroundColor(defText, DARK_RED);

			Table cardAtkDefColumn = new Table();
			cardAtkDefColumn.defaults().left();
			cardAtkDefColumn.add(atkText).row();
			cardAtkDefColumn.add(defText).row();

			cardBody.add(cardAtkDefColumn).left();
		}

		cardHeader.defaults().top();

		final int costPanelWidth = 15;
		cardHeader.add(costPanel).width(costPanelWidth).fill();
		cardHeader.add(nameText).width(cardGraphicWidth - costPanelWidth);

		cardGraphic.add(cardBody).height(cardGraphicHeight);

		makeClickShowInfoPanel(cardGraphic);
		return cardGraphic;
	}

	private void makeValidHandCardsDraggable() {
		// Currently, cards in hand can be dragged on their owner's turn
		// and the player has enough pp to play them
		dragAndDrop.clear();

		for (int playerNumber = 1; playerNumber <= 2; playerNumber++) {
			List<HandCardGraphic> graphics = getUIElements(playerNumber).listOfHandGraphics;

			Player activePlayer = match.getActivePlayer();

			for (HandCardGraphic graphic : graphics) {
				CardInfo card = graphic.getParentCard();
				if (activePlayer == card.getOwner() &&
						activePlayer.getPlayPoints() >= card.getCost()) {
					makeDraggable(graphic);
					outlineRenderer.startDrawingMyOutline(graphic);
				} else {
					outlineRenderer.stopDrawingMyOutline(graphic);
				}
			}
		}

		addFieldsAsDragTargets();
	}

	private void makeDraggable(HandCardGraphic cardGraphic) { // cards are cast by dragging to the field
		dragAndDrop.addSource(new Source(cardGraphic) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();

				payload.setObject(cardGraphic.getParentCard());

				// Make a seperate temporary graphic based on the same card
				Container<HandCardGraphic> dragGraphic = new Container<HandCardGraphic>(createHandCardGraphic(cardGraphic.getParentCard()))
						.width(cardGraphicWidth);

				payload.setDragActor(dragGraphic);
				payload.setInvalidDragActor(dragGraphic);
				payload.setValidDragActor(dragGraphic);
				return payload;
			}
		});
	}

	private void addFieldsAsDragTargets() {
		for (int i = 1; i <= 2; i++) {
			final int playerNumber = i;
			Table fieldPanel = getUIElements(playerNumber).fieldPanel;
			dragAndDrop.addTarget(new Target(fieldPanel) {

				Drawable coloredBG = RenderUtil.getSolidBG(Color.DARK_GRAY);

				private boolean isValidTarget(Payload payload) {
					Card card = (Card) payload.getObject();
					return match.getPlayer(playerNumber) == card.getOwner(); // can only drop cards onto their owner's field
				}

				@Override
				public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
					if (isValidTarget(payload)) {

						fieldPanel.background(coloredBG);
						return true;
					}

					return false;
				}

				/** Called when the payload is no longer over the target, whether because the touch was moved or a drop occurred. */
				@Override
				public void reset(Source source, Payload payload) {
					fieldPanel.background((Drawable) null);
				}

				@Override
				public void drop(Source source, Payload payload, float x, float y, int pointer) {
					if (isValidTarget(payload)) {
						Card card = (Card) payload.getObject();
						card.getOwner().playCard(card);
					}

				}
			});
		}
	}

	private void makeValidUnitsAttackDraggable() {

		dadAttacking.clear();

		PlayerPartitionUIElements elements = getUIElements(match.getActivePlayerNumber());
		PlayerPartitionUIElements elementsNonActive = getUIElements(match.getNonActivePlayerNumber());

		for (PermanentGraphic p : elementsNonActive.listOfFieldGraphics) {
			outlineRenderer.stopDrawingMyOutline(p);
		}

		for (PermanentGraphic p : elements.listOfFieldGraphics) {
			if (p.getPermanent() instanceof Follower) {
				Follower follower = (Follower) p.getPermanent();

				if (follower.canAttackPlayers() || follower.canAttackFollowers()) {
					makeAttackDraggable(p);
				}

				// Set outline around followers that can attack
				if (follower.canAttackPlayers()) {
					outlineRenderer.startDrawingMyOutline(p, Color.GREEN);
				} else if (follower.canAttackFollowers()) {
					outlineRenderer.startDrawingMyOutline(p, Color.YELLOW);
				} else {
					outlineRenderer.stopDrawingMyOutline(p);
				}
			}
		}

		addEnemyFollowersAndLeaderAsDragTargets();

	}

	private void makeAttackDraggable(PermanentGraphic permGraphic) { // cards are cast by dragging to the field
		dadAttacking.addSource(new Source(permGraphic) {
			@Override
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				attackingLine.start.set(permGraphic.localToStageCoordinates(new Vector2(permGraphic.getWidth() / 2, permGraphic.getHeight() / 2)));
				attackingLineVisible = true;
				return new Payload();
			}

			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
				attackingLineVisible = false;
			}

		});
	}

	private void addEnemyFollowersAndLeaderAsDragTargets() {
		PlayerPartitionUIElements elements = getUIElements(match.getNonActivePlayerNumber());
		for (PermanentGraphic p : elements.listOfFieldGraphics) {
			if (p.getPermanent() instanceof Follower) {
				Follower defender = (Follower) p.getPermanent();
				addAttackableAsDragTarget(defender, p);
			}
		}
		addAttackableAsDragTarget(match.getNonActivePlayer(), elements.handPanel);
	}

	private void addAttackableAsDragTarget(Attackable defender, Actor dropTarget) {
		dadAttacking.addTarget(new Target(dropTarget) {
			@Override
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				return true;
			}

			@Override
			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				Follower attacker = (Follower) ((PermanentGraphic) source.getActor()).getPermanent();
				attacker.attack(defender);
			}
		});
	}

	private PlayerPartitionUIElements getUIElements(int playerNumber) {
		if (playerNumber > 0 && playerNumber <= NUM_PLAYERS) {
			return playerUIElements[playerNumber - 1];
		} else {
			throw new InvalidArgumentException("Player number (" + playerNumber + ") is not valid");
		}
	}

	@Override
	public void render(float delta) {
		RenderUtil.clearGLScreen(Color.BLACK);
		stage.draw();

		outlineRenderer.renderOutlines();

		renderAttackingLineIfVisible();

		stage.setDebugAll(Gdx.input.isKeyPressed(Keys.SPACE));

	}

	public void renderAttackingLineIfVisible() {
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
		if (attackingLineVisible) {
			shapeRenderer.line(attackingLine.start, attackingLine.end);
		}
		shapeRenderer.end();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Keys.D) {
			Player player = match.getActivePlayer();
			try {
				player.drawFromDeck();
			} catch (ListOfCardsEmptyException e) {
				logger.info("Can't draw, deck is empty");
			}
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			if (infoPanel != null)
				removeInfoPanel();
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (pointer == Input.Buttons.LEFT) {
			Vector3 cursor = camera.unproject(new Vector3(screenX, screenY, 0));
			attackingLine.end.set(cursor.x, cursor.y);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		attackingLineVisible = false;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void cardOrPermanentStatsModified() {

		// need to update values in hand and field

		for (PlayerPartitionUIElements elements : playerUIElements) {
			regenerateHandDisplay(elements.playerNumber);
			regenerateFieldDisplay(elements.playerNumber);
		}

		makeValidHandCardsDraggable();
	}

	@Override
	public void cardOrPermanentEffectsModified() {
		makeValidUnitsAttackDraggable();
	}

	@Override
	public void unitAttacked() {
		makeValidUnitsAttackDraggable();
	}

	@Override
	public void fieldModified(int playerNumber) {
		regenerateFieldDisplay(playerNumber);
		makeValidUnitsAttackDraggable();
	}

	@Override
	public void handModified(int playerNumber) {
		regenerateHandDisplay(playerNumber);
		makeValidHandCardsDraggable();
		updateZoneCountTexts(playerNumber);
	}

	@Override
	public void deckModified(int playerNumber) {
		updateZoneCountTexts(playerNumber);
	}

	@Override
	public void graveyardModified(int playerNumber) {
		updateZoneCountTexts(playerNumber);

	}

	@Override
	public void playerHPModified(int playerNumber) {
		updateHpText(playerNumber);

	}

	@Override
	public void playPointsModified(int playerNumber) {
		updatePlayPointsText(playerNumber);
		makeValidHandCardsDraggable();
	}

	@Override
	public void turnChanged() {
		updateAllEndTurnButtonDisabledStatus();
		makeValidHandCardsDraggable();
		makeValidUnitsAttackDraggable();
	}

	public Match getMatch() {
		return match;
	}

	/**
	 * Is not the Partition itself, but the UI elements we need a reference to.
	 * 
	 */
	private static class PlayerPartitionUIElements {

		public Table handPanel;
		List<HandCardGraphic> listOfHandGraphics;

		List<PermanentGraphic> listOfFieldGraphics;

		public Table fieldPanel;

		public Label playPointsText;
		public Label hpText;

		public Label graveyardCountText;
		public Label deckCountText;
		public Label handCountText;

		public TextButton endTurnButton;

		public final int playerNumber;

		public PlayerPartitionUIElements(OutlineRenderer outlineRenderer, int playerNumber) {
			listOfHandGraphics = new OutlineSmartArrayList<HandCardGraphic>(outlineRenderer);
			listOfFieldGraphics = new OutlineSmartArrayList<PermanentGraphic>(outlineRenderer);
			this.playerNumber = playerNumber;
		}

	}

	static class Segment {
		final Vector2 start = new Vector2();
		final Vector2 end = new Vector2();
	}

}
