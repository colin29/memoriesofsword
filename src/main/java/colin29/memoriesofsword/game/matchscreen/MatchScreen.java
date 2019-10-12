package colin29.memoriesofsword.game.matchscreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import colin29.memoriesofsword.MyFonts;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.ListOfCardsEmptyException;
import colin29.memoriesofsword.game.match.Match;
import colin29.memoriesofsword.game.match.Player;
import colin29.memoriesofsword.game.match.SimpleMatchStateListener;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;
import colin29.memoriesofsword.util.template.AppWithResources;
import colin29.memoriesofsword.util.template.BaseScreen;

/**
 * A Match screen will create a match when it is created. Then when the match finishes, it will call whatever screen it likes.
 *
 */
public class MatchScreen extends BaseScreen implements InputProcessor, SimpleMatchStateListener {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	final Skin skin;
	final MyFonts fonts;

	InputMultiplexer multiplexer = new InputMultiplexer();

	private final int NUM_PLAYERS = 2;
	/**
	 * Should access using the helper, unless iterating {@link #getUIElements(int), getComponentAt}
	 */
	PlayerPartitionUIElements[] playerUIElements;

	AppWithResources app;

	public OutlineRenderer outlineRenderer;

	final Match match;

	// Sub-modules
	MiscUI miscUI;
	HandUI handUI;
	FieldUI fieldUI;
	InfoPanelUI infoUI;
	final UserPrompter userPrompter;

	public MatchScreen(AppWithResources app, CardRepository cardRepo) {
		super(app);
		this.app = app;
		fonts = app.getFonts();
		skin = app.getSkin();

		outlineRenderer = new OutlineRenderer(app.getShapeRenderer());

		/// Set up Match
		userPrompter = new UserPrompter(this);
		match = new Match(userPrompter, cardRepo);

		match.useTestDecks();
		match.beginMatch();

		// Set up UI modules
		UIConstructor constructor = new UIConstructor(this, app);
		playerUIElements = constructor.getUIElements();

		miscUI = new MiscUI(this);
		handUI = new HandUI(this);
		fieldUI = new FieldUI(this);
		infoUI = new InfoPanelUI(this);

		constructor.constructUI(playerUIElements);

		match.addSimpleStateListener(this);

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(stage);
	}

	int cardGraphicWidth = 80;
	int cardGraphicHeight = 100;

	int permanentGraphicWidth = 80;
	int permanentGraphicHeight = 100;

	final Color DARK_BLUE = RenderUtil.rgb(51, 51, 204);
	final Color DARK_RED = RenderUtil.rgb(128, 0, 0);
	final Color FOREST = Color.FOREST;

	public PlayerPartitionUIElements getUIElements(int playerNumber) {
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

		fieldUI.renderAttackingLineIfVisible();

		stage.setDebugAll(Gdx.input.isKeyPressed(Keys.SPACE));

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
			infoUI.removeInfoPanel();
		}

		// A right click generally cancels the current operation, if allowed
		if (button == Input.Buttons.RIGHT) {
			if (userPrompter.getPromptContext() == UserPrompter.PromptContext.USER_PROMPT) {
				userPrompter.cancelUserTargetPrompt();
			}
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
			fieldUI.setAttackingLineEndPoint(cursor.x, cursor.y);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		fieldUI.attackingLineVisible = false;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void cardOrPermanentStatsModified() {
		for (PlayerPartitionUIElements elements : playerUIElements) {
			handUI.regenerateHandDisplay(elements.playerNumber);
			fieldUI.regenerateFieldDisplay(elements.playerNumber);
		}

		handUI.makeValidHandCardsDraggable();
	}

	@Override
	public void cardOrPermanentEffectsModified() {
		fieldUI.makeValidUnitsAttackDraggable();
	}

	@Override
	public void unitAttacked() {
		fieldUI.makeValidUnitsAttackDraggable();
	}

	@Override
	public void fieldModified(int playerNumber) {
		fieldUI.regenerateFieldDisplay(playerNumber);
		fieldUI.makeValidUnitsAttackDraggable();
	}

	@Override
	public void handModified(int playerNumber) {
		handUI.regenerateHandDisplay(playerNumber);
		handUI.makeValidHandCardsDraggable();
		miscUI.updateZoneCountTexts(playerNumber);
	}

	@Override
	public void deckModified(int playerNumber) {
		miscUI.updateZoneCountTexts(playerNumber);
	}

	@Override
	public void graveyardModified(int playerNumber) {
		miscUI.updateZoneCountTexts(playerNumber);

	}

	@Override
	public void playerHPModified(int playerNumber) {
		miscUI.updateHpText(playerNumber);

	}

	@Override
	public void playPointsModified(int playerNumber) {
		miscUI.updatePlayPointsText(playerNumber);
		handUI.makeValidHandCardsDraggable();
	}

	@Override
	public void turnChanged() {
		miscUI.updateAllEndTurnButtonDisabledStatus();
		handUI.makeValidHandCardsDraggable();
		fieldUI.makeValidUnitsAttackDraggable();
	}

	public Match getMatch() {
		return match;
	}

	Stage getStage() {
		return stage;
	}

	OutlineRenderer getOutlineRenderer() {
		return outlineRenderer;
	}

}
