package colin29.memoriesofsword.game.matchscreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import colin29.memoriesofsword.MyFonts;
import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.Follower;
import colin29.memoriesofsword.game.match.Match.FollowerCallback;
import colin29.memoriesofsword.game.match.Match.FollowerOrPlayerCallback;
import colin29.memoriesofsword.game.match.Match.PlayerCallback;
import colin29.memoriesofsword.game.match.Player;
import colin29.memoriesofsword.game.match.cardeffect.Effect;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer;
import colin29.memoriesofsword.game.match.cardeffect.FollowerOrPlayer;
import colin29.memoriesofsword.game.matchscreen.MatchScreen.PromptContext;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.UIUtil;

public class UserPrompter implements PromptableForUserSelection {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private final MatchScreen parent;

	private FollowerCallback followerSelectedCallback;
	private PlayerCallback playerSelectedCallback;
	private FollowerOrPlayerCallback followerOrPlayerSelectedCallback;
	private Predicate<PermanentOrPlayer> isTargetValid;
	private Runnable selectionCancelledCallback;
	private PromptedTargetType promptedTargetType;
	private Table targetingSourceCardPanel;

	PromptContext promptContext = PromptContext.IDLE;

	private Table targetingInfoPanel; // shows info about the effect of the current targeting

	private final Stage stage;
	private final MyFonts fonts;

	UserPrompter(MatchScreen parent) {
		this.parent = parent;
		stage = parent.getStage();

		fonts = parent.app.getFonts();
	}

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

	void cancelUserTargetPrompt() {

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

	void onTargetableActorClicked(TargetableActor actor) {

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

	PromptContext getPromptContext() {
		return promptContext;
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

	private void beginTargetingContext(Effect effect, Predicate<PermanentOrPlayer> predicate) {
		promptContext = PromptContext.USER_PROMPT;
		isTargetValid = predicate;

		createAndDisplayTargetingInfoPanel(effect, effect.getSource().getOwner().getPlayerNumber());
		createAndDisplayTargetingSourceCardPanel(effect.getSource().getSourceCard());

		parent.handUI.disableValidHandCardsDraggable();
		parent.fieldUI.disableValidUnitsAttackDraggable();
		parent.miscUI.disableActivePlayerEndTurnButton();

		startDrawingOutlinesAroundValidTargetableActors(predicate); // do this after because disabling UI includes clearing actor outlines.
	}

	private void endFollowerTargettingContext() {

		promptContext = PromptContext.IDLE;
		promptedTargetType = null;
		isTargetValid = null;

		removeTargetingInfoPanel();
		removeTargetingSourceCardPanel();

		stopDrawingOutlinesAroundTargetableActors();

		parent.handUI.makeValidHandCardsDraggable();
		parent.fieldUI.makeValidUnitsAttackDraggable();
		parent.miscUI.enableActivePlayerEndTurnButton();

	}

	private void startDrawingOutlinesAroundValidTargetableActors(Predicate<PermanentOrPlayer> predicate) {
		List<TargetableActor> validActors = getValidTargetableActors(predicate);
		for (TargetableActor t : validActors) {
			parent.outlineRenderer.startDrawingMyOutline((Actor) t, Color.ORANGE);
		}
	}

	private void stopDrawingOutlinesAroundTargetableActors() {
		for (TargetableActor t : getAllTargetableActors()) {
			parent.outlineRenderer.stopDrawingMyOutline((Actor) t);
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

		for (PlayerPartitionUIElements element : parent.playerUIElements) {
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
		tempRoot.add(parent.handUI.createHandCardGraphic(card)).size(parent.cardGraphicWidth, parent.cardGraphicHeight);

		targetingSourceCardPanel = tempRoot;
	}

	private void removeTargetingSourceCardPanel() {
		if (targetingSourceCardPanel != null) {
			targetingSourceCardPanel.remove();
			targetingSourceCardPanel = null;
		}
	}

	/**
	 * This is a small panel that show the title of the source card and the effect text
	 */
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

	private enum PromptedTargetType {
		FOLLOWER, PLAYER, FOLLOWER_OR_PLAYER;
	}

}
