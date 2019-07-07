package colin29.memoriesofsword.game.matchscreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import colin29.memoriesofsword.MyFonts;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.Attackable;
import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.Follower;
import colin29.memoriesofsword.game.match.ListOfCardsEmptyException;
import colin29.memoriesofsword.game.match.Match;
import colin29.memoriesofsword.game.match.Match.FollowerCallback;
import colin29.memoriesofsword.game.match.Match.FollowerOrPlayerCallback;
import colin29.memoriesofsword.game.match.Match.PlayerCallback;
import colin29.memoriesofsword.game.match.Permanent;
import colin29.memoriesofsword.game.match.Player;
import colin29.memoriesofsword.game.match.SimpleMatchStateListener;
import colin29.memoriesofsword.game.match.cardeffect.Effect;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer;
import colin29.memoriesofsword.game.match.cardeffect.FollowerOrPlayer;
import colin29.memoriesofsword.game.matchscreen.graphics.PermanentGraphic;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.UIUtil;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;
import colin29.memoriesofsword.util.template.AppWithResources;
import colin29.memoriesofsword.util.template.BaseScreen;

/**
 * A Match screen will create a match when it is created. Then when the match finishes, it will call whatever screen it likes.
 *
 */
public class MatchScreen extends BaseScreen implements InputProcessor, SimpleMatchStateListener, PromptableForUserSelection {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public final Match match;

	final Skin skin;
	final MyFonts fonts;

	InputMultiplexer multiplexer = new InputMultiplexer();

	private final int NUM_PLAYERS = 2;
	/**
	 * Use the helper {@link #getUIElements(int), getComponentAt} to access
	 */
	PlayerPartitionUIElements[] playerUIElements;

	AppWithResources app;

	DragAndDrop dadAttacking = new DragAndDrop();

	public OutlineRenderer outlineRenderer;

	Table targetingInfoPanel; // shows info about the effect of the current targeting (user prompt)

	/**
	 * Represents the line to draw between follower and cursor, when the user is dragging to attack
	 */
	final Segment attackingLine = new Segment();
	boolean attackingLineVisible = true;

	/**
	 * When user_prompt is on, the UI will respond to a Follower (other other graphic) being clicked. A valid targeting will fufill the prompt request
	 * and UIContext will be set back to idle
	 * 
	 * Note: disabling the normal UI must be done separately.
	 */
	public enum PromptContext {
		IDLE, USER_PROMPT;
	}

	PromptContext promptContext = PromptContext.IDLE;

	// SubModules
	MiscUI miscUI;
	HandUI handUI;
	FieldUI fieldUI;
	InfoPanelUI infoUI;

	public MatchScreen(AppWithResources app, CardRepository cardRepo) {
		super(app);
		this.app = app;
		fonts = app.getFonts();
		skin = app.getSkin();

		outlineRenderer = new OutlineRenderer(app.getShapeRenderer());

		/// Set up Match

		match = new Match(this, cardRepo);

		match.useTestDecks();
		match.beginMatch();

		// Set up UI modules
		UIConstructor constructor = new UIConstructor(this, app);

		miscUI = new MiscUI(this);
		handUI = new HandUI(this);
		fieldUI = new FieldUI(this);
		infoUI = new InfoPanelUI(this);

		playerUIElements = constructor.initializeUIElementsRef();
		constructor.constructUI(playerUIElements);

		match.addSimpleStateListener(this);

		multiplexer.addProcessor(this);
		multiplexer.addProcessor(stage);
	}

	int cardGraphicWidth = 80;
	int cardGraphicHeight = 100;

	private int permanentGraphicWidth = 80;
	private int permanentGraphicHeight = 100;

	final Color hpTextWoundedColor = RenderUtil.rgb(255, 128, 128); // pale red

	void regenerateFieldDisplay(int playerNumber) {
		List<Permanent<?>> entitiesOnField = match.getPlayer(playerNumber).getFieldInfo();

		PlayerPartitionUIElements elements = getUIElements(playerNumber);
		elements.listOfFieldGraphics.clear();

		Table fieldPanel = getUIElements(playerNumber).fieldPanel;
		fieldPanel.clearChildren();
		for (Permanent<?> entity : entitiesOnField) {
			PermanentGraphic p = fieldUI.createPermanentGraphic(entity);
			fieldPanel.add(p).size(permanentGraphicWidth, permanentGraphicHeight);
			elements.listOfFieldGraphics.add(p);
			p.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					logger.debug("Permanent Graphic {} clicked", entity.getPNumName());
					onTargetableActorClicked(p);
				}
			});
		}
		makeValidUnitsAttackDraggable();
	}

	final Color DARK_BLUE = RenderUtil.rgb(51, 51, 204);
	final Color DARK_RED = RenderUtil.rgb(128, 0, 0);
	final Color FOREST = Color.FOREST;

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

	private void disableValidUnitsAttackDraggable() {

		dadAttacking.clear();

		PlayerPartitionUIElements elements = getUIElements(match.getActivePlayerNumber());

		for (PermanentGraphic p : elements.listOfFieldGraphics) {
			outlineRenderer.stopDrawingMyOutline(p);
		}

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
		addAttackableAsDragTarget(match.getNonActivePlayer(), elements.getHandPanel());
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
			infoUI.removeInfoPanel();
		}

		// A right click generally signals a cancel of the current operation, if allowed
		if (button == Input.Buttons.RIGHT) {
			if (promptContext == PromptContext.USER_PROMPT) {
				cancelUserPromptForFollowerSelect();
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
			handUI.regenerateHandDisplay(elements.playerNumber);
			regenerateFieldDisplay(elements.playerNumber);
		}

		handUI.makeValidHandCardsDraggable();
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
		miscUI.updateAllEndTurnButtonDisabledStatus(this);
		handUI.makeValidHandCardsDraggable();
		makeValidUnitsAttackDraggable();
	}

	public Match getMatch() {
		return match;
	}

	static class Segment {
		final Vector2 start = new Vector2();
		final Vector2 end = new Vector2();
	}

	FollowerCallback followerSelectedCallback;
	PlayerCallback playerSelectedCallback;
	FollowerOrPlayerCallback followerOrPlayerSelectedCallback;

	Predicate<PermanentOrPlayer> isTargetValid;

	Runnable selectionCancelledCallback;

	private enum PromptedTargetType {
		FOLLOWER, PLAYER, FOLLOWER_OR_PLAYER;
	}

	PromptedTargetType promptedTargetType;

	Table targetingSourceCardPanel;

	@Override
	public void promptUserForFollowerSelect(EffectOnFollower effect, Predicate<PermanentOrPlayer> predicate, FollowerCallback callback,
			Runnable onCancelled) {

		followerSelectedCallback = callback;
		selectionCancelledCallback = onCancelled;
		promptedTargetType = PromptedTargetType.FOLLOWER;

		if ((getValidTargetableActors(predicate)).isEmpty()) {
			effect.fizzledBecauseNoValidTargets();
			fufillUserPromptForFollowerSelect((Follower) null);
		} else {
			beginTargetingContext(effect, predicate);
		}
	}

	@Override
	public void promptUserForPlayerSelect(EffectOnPlayer effect, Predicate<PermanentOrPlayer> predicate, PlayerCallback callback,
			Runnable onCancelled) {
		playerSelectedCallback = callback;
		selectionCancelledCallback = onCancelled;
		promptedTargetType = PromptedTargetType.PLAYER;
		if ((getValidTargetableActors(predicate)).isEmpty()) {
			effect.fizzledBecauseNoValidTargets();
			fufillUserPromptForPlayerSelect((Player) null);
		} else {
			beginTargetingContext(effect, predicate);
		}
	}

	@Override
	public void promptUserForFollowerOrPlayerSelect(EffectOnFollowerOrPlayer effect, Predicate<PermanentOrPlayer> predicate,
			FollowerOrPlayerCallback callback, Runnable onCancelled) {
		followerOrPlayerSelectedCallback = callback;
		selectionCancelledCallback = onCancelled;
		promptedTargetType = PromptedTargetType.FOLLOWER_OR_PLAYER;
		if ((getValidTargetableActors(predicate)).isEmpty()) {
			effect.fizzledBecauseNoValidTargets();
			fufillUserPromptForFollowerOrPlayerSelect((FollowerOrPlayer) null);
		} else {
			beginTargetingContext(effect, predicate);
		}
	}

	protected void onTargetableActorClicked(TargetableActor actor) {

		if (promptContext != PromptContext.USER_PROMPT) {
			return;
		}

		PermanentOrPlayer target = actor.getTarget();

		if (isTargetValid == null) {
			logger.warn("istargetValid is null, it shouldn't be. Overwriting it with true predicate.");
			isTargetValid = (PermanentOrPlayer) -> true;
		}
		if (!isTargetValid.test(target)) {
			return;
		}
		switch (promptedTargetType) {

		case PLAYER:
			if (target instanceof Player) {
				fufillUserPromptForPlayerSelect((Player) target);
			}
			break;
		case FOLLOWER:
			if (target instanceof Follower) {
				fufillUserPromptForFollowerSelect((Follower) target);
			}
			break;
		case FOLLOWER_OR_PLAYER:
			if (target instanceof FollowerOrPlayer) {
				fufillUserPromptForFollowerOrPlayerSelect((FollowerOrPlayer) target);
			}
			break;
		default:
			throw new AssertionError("Unsupported prompt target type");
		}

	}

	private void fufillUserPromptForFollowerSelect(Follower follower) {

		// Need to copy a tempRef because we need to clear the main field BEFORE making the callback. Because the callback could make another async
		// call, we don't want to modify related state after making the callback

		FollowerCallback followerSelectedCallbackTempRef = followerSelectedCallback;
		followerSelectedCallback = null;

		endFollowerTargettingContext();

		if (followerSelectedCallbackTempRef == null) {
			logger.warn("Follower selected callback is null. It shouldn't be.");
			return;
		}
		followerSelectedCallbackTempRef.provideSelection(follower);

	}

	private void fufillUserPromptForPlayerSelect(Player player) {
		PlayerCallback callbackTempRef = playerSelectedCallback;
		playerSelectedCallback = null;

		endFollowerTargettingContext();

		if (callbackTempRef == null) {
			logger.warn("Player selected callback is null. It shouldn't be.");
			return;
		}
		callbackTempRef.provideSelection(player);

	}

	private void fufillUserPromptForFollowerOrPlayerSelect(FollowerOrPlayer followerOrPlayer) {
		FollowerOrPlayerCallback callbackTempRef = followerOrPlayerSelectedCallback;
		followerOrPlayerSelectedCallback = null;

		endFollowerTargettingContext();

		if (callbackTempRef == null) {
			logger.warn("FollowerOrPlayer selected callback is null. It shouldn't be.");
			return;
		}
		callbackTempRef.provideSelection(followerOrPlayer);

	}

	private void cancelUserPromptForFollowerSelect() {

		logger.debug("Targeting cancelled!");

		Runnable selectionCancelledCallbackTempRef = selectionCancelledCallback;
		selectionCancelledCallback = null;
		endFollowerTargettingContext();

		if (selectionCancelledCallbackTempRef == null) {
			logger.warn("Follower selected callback is null. It shouldn't be.");
		} else {
			selectionCancelledCallbackTempRef.run();
		}

	}

	private void beginTargetingContext(Effect effect, Predicate<PermanentOrPlayer> predicate) {
		promptContext = PromptContext.USER_PROMPT;
		isTargetValid = predicate;

		createAndDisplayTargetingInfoPanel(effect, effect.getSource().getOwner().getPlayerNumber());
		createAndDisplayTargetingSourceCardPanel(effect.getSource().getSourceCard());

		handUI.disableValidHandCardsDraggable();
		disableValidUnitsAttackDraggable();
		disableActivePlayerEndTurnButton();

		startDrawingOutlinesAroundValidTargetableActors(predicate); // do this after because disabling UI includes clearing actor outlines.
	}

	private void endFollowerTargettingContext() {

		promptContext = PromptContext.IDLE;
		promptedTargetType = null;
		isTargetValid = null;

		removeTargetingInfoPanel();
		removeTargetingSourceCardPanel();

		stopDrawingOutlinesAroundTargetableActors();

		handUI.makeValidHandCardsDraggable();
		makeValidUnitsAttackDraggable();
		enableActivePlayerEndTurnButton();

	}

	private void startDrawingOutlinesAroundValidTargetableActors(Predicate<PermanentOrPlayer> predicate) {
		List<TargetableActor> validActors = getValidTargetableActors(predicate);
		for (TargetableActor t : validActors) {
			outlineRenderer.startDrawingMyOutline((Actor) t, Color.ORANGE);
		}
	}

	private void stopDrawingOutlinesAroundTargetableActors() {
		for (TargetableActor t : getAllTargetableActors()) {
			outlineRenderer.stopDrawingMyOutline((Actor) t);
		}
	}

	private List<TargetableActor> getValidTargetableActors(Predicate<PermanentOrPlayer> predicate) {

		List<TargetableActor> actors = getAllTargetableActors();
		List<TargetableActor> validActors = new ArrayList<TargetableActor>();

		for (TargetableActor actor : actors) {
			if (predicate.test(actor.getTarget())) {
				validActors.add(actor);
			}
		}
		return validActors;
	}

	/**
	 * @return list of actors, in no particular order
	 */
	private List<TargetableActor> getAllTargetableActors() {
		List<TargetableActor> actors = new ArrayList<TargetableActor>();

		for (PlayerPartitionUIElements element : playerUIElements) {
			actors.addAll(element.listOfFieldGraphics);
			actors.add(element.handPanel);
		}
		return actors;
	}

	private void createAndDisplayTargetingSourceCardPanel(Card card) {
		removeTargetingSourceCardPanel();

		Table tempRoot = new Table();
		tempRoot.setFillParent(true);
		tempRoot.left().pad(40);
		stage.addActor(tempRoot);
		tempRoot.add(handUI.createHandCardGraphic(card)).size(cardGraphicWidth, cardGraphicHeight);

		targetingSourceCardPanel = tempRoot;
	}

	private void removeTargetingSourceCardPanel() {
		if (targetingSourceCardPanel != null) {
			targetingSourceCardPanel.remove();
			targetingSourceCardPanel = null;
		}
	}

	private void disableActivePlayerEndTurnButton() {
		getUIElements(match.getActivePlayerNumber()).endTurnButton.setDisabled(true);
	}

	private void enableActivePlayerEndTurnButton() {
		getUIElements(match.getActivePlayerNumber()).endTurnButton.setDisabled(false);
	}

	private void createAndDisplayTargetingInfoPanel(Effect effect, int playerNumber) {

		removeTargetingInfoPanel();

		Table tempRoot = new Table();
		Table main = new Table();

		Label titleText = new Label(effect.getSource().getName(), UIUtil.createLabelStyle(fonts.largishFont()));
		Label effectText = new Label(effect.toString(), UIUtil.createLabelStyle(fonts.mediumFont()));

		tempRoot.setFillParent(true);

		tempRoot.left();

		if (playerNumber == 1) {
			tempRoot.bottom();
		} else {
			tempRoot.top();
		}

		tempRoot.add(main);

		main.setBackground(RenderUtil.getSolidBG(Color.DARK_GRAY));

		main.add(titleText).row();
		main.add(effectText);

		stage.addActor(tempRoot);
		targetingInfoPanel = tempRoot;
	}

	/**
	 * It's OK if info panel is null already
	 */
	private void removeTargetingInfoPanel() {
		if (targetingInfoPanel != null) {
			targetingInfoPanel.remove();
			targetingInfoPanel = null;
		}
	}

	Stage getStage() {
		return stage;
	}

	OutlineRenderer getOutlineRenderer() {
		return outlineRenderer;
	}

	Label createColoredLabel(String text, LabelStyle style, Color bgColor, int align) {
		Label l = new Label(text, style);
		RenderUtil.setLabelBackgroundColor(l, bgColor);
		l.setAlignment(align);
		return l;
	}

}
