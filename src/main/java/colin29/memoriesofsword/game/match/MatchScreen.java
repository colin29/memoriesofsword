package colin29.memoriesofsword.game.match;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import colin29.memoriesofsword.App;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.util.RenderUtil;
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

	InputMultiplexer multiplexer = new InputMultiplexer();

	Player player1; // should be changed to playerinfo later, or some thing like that

	private final int NUM_PLAYERS = 2;
	/**
	 * Use the helper {@link #getUIElements(int), getComponentAt} to access
	 */
	PlayerPartitionUIElements[] playerUIElements = new PlayerPartitionUIElements[NUM_PLAYERS];
	{
		for (int i = 0; i < NUM_PLAYERS; i++) {
			playerUIElements[i] = new PlayerPartitionUIElements();
		}
	}

	public MatchScreen(AppWithResources game, CardRepository cardRepo) {
		super(game);
		match = new Match(cardRepo);

		match.useTestDecks();
		match.beginMatch();
		constructUI();
		match.addSimpleStateListener(this);

		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(this);

		player1 = match.getPlayerReadOnly(1);

		// Sandbox area
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();
		Player player1 = match.getPlayer1Sandboxing();
		player1.playCard((Card) player1.getHand().get(0));
		player1.playCardWithoutPayingCost((Card) player1.getHand().get(0));
		// player1.playCardWithoutPayingCost((Card) player1.getHand().get(0));
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

		createPlayerPartition(2);
		createPlayerPartition(1); // Player 1's area should be on the bottom

		root.pack();
	}

	private void createPlayerPartition(int playerNumber) {

		final boolean normalOrientation = playerNumber == 1; // normal orientation has your hand on the bottom of your area

		PlayerPartitionUIElements elements = getUIElements(playerNumber);

		Table playerPartition = new Table();

		root.add(playerPartition).height(Value.percentHeight(0.5f, root)).expandX().fill();
		root.row();

		playerPartition.add(createSidePanel(playerNumber)).width(sidePanelWidth).expandY().fill();
		Table mainArea = new Table();
		playerPartition.add(mainArea).expand().fill();

		TextButton endTurnButton = new TextButton("End Turn", skin);
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
	}

	private Table createSidePanel(int playerNumber) {
		Table sidePanel = new Table();
		sidePanel.defaults().expandX().left();

		Color DARK_GRAY = RenderUtil.rgb(40, 40, 40);
		Color DARK_RED = RenderUtil.rgb(128, 0, 0);

		Label nameText = new Label(match.getPlayer1Name(), skin);
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

		refreshHpText(playerNumber);
		refreshPlayPointsText(playerNumber);
		refreshZoneCountTexts(playerNumber);

		return sidePanel;
	}

	private final Color hpTextWoundedColor = RenderUtil.rgb(255, 128, 128); // pale red

	private void refreshHpText(int playerNumber) {
		Label hpText = getUIElements(playerNumber).hpText;
		Player player = match.getPlayerReadOnly(playerNumber);
		hpText.setText("" + player.getHp());
		if (player.getHp() != player.getMaxHp()) {
			hpText.setColor(hpTextWoundedColor);
		} else {
			hpText.setColor(Color.WHITE);
		}
	}

	private void refreshPlayPointsText(int playerNumber) {
		Label playPointsText = getUIElements(playerNumber).playPointsText;
		Player player = match.getPlayerReadOnly(playerNumber);
		playPointsText.setText("PP: " + player.getPlayPoints() + " / " + player.getMaxPlayPoints());
	}

	private void refreshZoneCountTexts(int playerNumber) {

		PlayerPartitionUIElements elements = getUIElements(playerNumber);
		Player player = match.getPlayerReadOnly(playerNumber);

		elements.graveyardCountText.setText("" + player.getGraveyard().size());
		elements.deckCountText.setText("" + player.getDeck().size());
		elements.handCountText.setText("" + player.getHand().size());

	}

	private PlayerPartitionUIElements getUIElements(int playerNumber) {
		if (playerNumber > 0 && playerNumber <= NUM_PLAYERS) {
			return playerUIElements[playerNumber - 1];
		} else {
			throw new InvalidArgumentException("Player number (" + playerNumber + ") is not valid");
		}
	}

	private void regenerateFieldDisplay(int playerNumber) {
		List<Permanent> entitiesOnField = match.getPlayerReadOnly(playerNumber).getFieldInfo();

		Table fieldPanel = getUIElements(playerNumber).fieldPanel;
		fieldPanel.clear();
		for (Permanent entity : entitiesOnField) {
			fieldPanel.add(constructPermanentCardGraphic(entity)).size(permanentGraphicWidth, permanentGraphicHeight);
		}
	}

	private Table constructPermanentCardGraphic(Permanent entity) {

		// Should have a label that shows the card name

		Table permGraphic = new Table();

		// Set the background card art
		Texture img = assets.get("img/image01.jpg", Texture.class);
		TextureRegionDrawable imgDrawable = new TextureRegionDrawable(new TextureRegion(img));
		permGraphic.setBackground(imgDrawable);

		// make the cost symbol, etc.

		permGraphic.bottom().defaults().space(10);

		if (entity instanceof Follower) {

			Follower follower = (Follower) entity;

			Label atkText = new Label(String.valueOf(follower.getAtk()), skin);
			Label defText = new Label(String.valueOf(follower.getDef()), skin);

			Color DARK_BLUE = RenderUtil.rgb(51, 51, 204);
			Color DARK_RED = RenderUtil.rgb(128, 0, 0);

			RenderUtil.setLabelBackgroundColor(atkText, DARK_BLUE);
			RenderUtil.setLabelBackgroundColor(defText, DARK_RED);

			permGraphic.add(atkText);
			permGraphic.add(defText);
		}

		return permGraphic;
	}

	private void regenerateHandDisplay(int playerNumber) {
		Table handPanel = getUIElements(playerNumber).handPanel;
		handPanel.clear();
		List<CardInfo> p1CardsInHand = match.getPlayerReadOnly(playerNumber).getHand();
		for (CardInfo card : p1CardsInHand) {
			handPanel.add(constructHandCardGraphic(card)).size(cardGraphicWidth, cardGraphicHeight);
		}
	}

	private void updateAllEndTurnButtonDisabledStatus() {
		for (int playerNumber = 1; playerNumber <= 2; playerNumber++) {
			updateEndTurnButtonDisabledStatus(playerNumber);
		}
	}

	private void updateEndTurnButtonDisabledStatus(int playerNumber) {
		PlayerPartitionUIElements elements = getUIElements(playerNumber);
		elements.endTurnButton.setDisabled(playerNumber != match.getActivePlayerNumber());
	}

	private Table constructHandCardGraphic(CardInfo card) {

		// Should have a label that shows the card name

		Table cardGraphic = new Table();

		// Set the background card art
		Texture img = assets.get("img/image01.jpg", Texture.class);
		TextureRegionDrawable imgDrawable = new TextureRegionDrawable(new TextureRegion(img));
		cardGraphic.setBackground(imgDrawable);

		// make the cost symbol, etc.

		Label costText = new Label(String.valueOf(card.getCost()), skin);
		costText.setAlignment(Align.center);
		Label aOneDigitLabel = new Label("0", skin); // example label for sizing purposes

		Container<Label> costPanel = new Container<Label>(costText);

		int costPanelPadding = 4; // make the cost icon slightly bigger, but it might have to expand anyways if it's a two digit cost
		costPanel.size(Math.max(aOneDigitLabel.getWidth() + costPanelPadding, costPanel.getActor().getWidth()),
				aOneDigitLabel.getHeight() + costPanelPadding);
		costPanel.setBackground(RenderUtil.getSolidBG(Color.FOREST));

		Color DARK_BLUE = RenderUtil.rgb(51, 51, 204);
		Color DARK_RED = RenderUtil.rgb(128, 0, 0);

		Label atkText = new Label(String.valueOf(card.getAtk()), skin);
		RenderUtil.setLabelBackgroundColor(atkText, DARK_BLUE);
		Label defText = new Label(String.valueOf(card.getDef()), skin);
		RenderUtil.setLabelBackgroundColor(defText, DARK_RED);

		cardGraphic.top().left();

		Table cardStatsColumn = new Table();
		cardStatsColumn.defaults().left();
		cardStatsColumn.add(costPanel).row();
		cardStatsColumn.add(atkText).row();
		cardStatsColumn.add(defText).row();

		cardGraphic.add(cardStatsColumn);

		return cardGraphic;
	}

	@Override
	public void render(float delta) {
		RenderUtil.clearGLScreen(Color.BLACK);
		stage.draw();

		root.setDebug(Gdx.input.isKeyPressed(Keys.SPACE), true);

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Keys.D) {
			Player player = match.getActivePlayerInfo();
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cardOrPermanentStatsModified() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fieldModified(int playerNumber) {
		regenerateFieldDisplay(playerNumber);
	}

	@Override
	public void handModified(int playerNumber) {
		regenerateHandDisplay(playerNumber);
		refreshZoneCountTexts(playerNumber);
	}

	@Override
	public void deckModified(int playerNumber) {
		refreshZoneCountTexts(playerNumber);
	}

	@Override
	public void graveyardModified(int playerNumber) {
		refreshZoneCountTexts(playerNumber);

	}

	@Override
	public void playerHPModified(int playerNumber) {
		refreshHpText(playerNumber);

	}

	@Override
	public void playPointsModified(int playerNumber) {
		refreshPlayPointsText(playerNumber);
	}

	@Override
	public void turnChanged() {
		updateAllEndTurnButtonDisabledStatus();
	}

	/**
	 * Is not the Partition itself, but the UI elements we need a reference to.
	 * 
	 */
	private static class PlayerPartitionUIElements {
		public Table handPanel;
		public Table fieldPanel;

		public Label playPointsText;
		public Label hpText;

		public Label graveyardCountText;
		public Label deckCountText;
		public Label handCountText;

		public TextButton endTurnButton;
	}

}
