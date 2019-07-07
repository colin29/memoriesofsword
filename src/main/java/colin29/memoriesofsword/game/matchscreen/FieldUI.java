package colin29.memoriesofsword.game.matchscreen;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import colin29.memoriesofsword.game.match.Amulet;
import colin29.memoriesofsword.game.match.Attackable;
import colin29.memoriesofsword.game.match.Follower;
import colin29.memoriesofsword.game.match.Permanent;
import colin29.memoriesofsword.game.matchscreen.MatchScreen.Segment;
import colin29.memoriesofsword.game.matchscreen.graphics.AmuletGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.FollowerGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.PermanentGraphic;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.UIUtil;

public class FieldUI {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private final MatchScreen parent;

	DragAndDrop dadAttacking = new DragAndDrop();

	/**
	 * Represents the line to draw between follower and cursor, when the user is dragging to attack
	 */
	final Segment attackingLine = new Segment();

	boolean attackingLineVisible = true;

	public FieldUI(MatchScreen matchScreen) {
		parent = matchScreen;
	}

	void regenerateFieldDisplay(int playerNumber) {
		List<Permanent<?>> entitiesOnField = parent.match.getPlayer(playerNumber).getFieldInfo();

		PlayerPartitionUIElements elements = parent.getUIElements(playerNumber);
		elements.listOfFieldGraphics.clear();

		Table fieldPanel = parent.getUIElements(playerNumber).fieldPanel;
		fieldPanel.clearChildren();
		for (Permanent<?> entity : entitiesOnField) {
			PermanentGraphic p = createPermanentGraphic(entity);
			fieldPanel.add(p).size(parent.permanentGraphicWidth, parent.permanentGraphicHeight);
			elements.listOfFieldGraphics.add(p);
			p.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					logger.debug("Permanent Graphic {} clicked", entity.getPNumName());
					parent.onTargetableActorClicked(p);
				}
			});
		}
		makeValidUnitsAttackDraggable(parent);
	}

	private PermanentGraphic createPermanentGraphic(final Permanent<?> permanent) {

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

			LabelStyle style = UIUtil.createLabelStyle(parent.fonts.permanentStatsBorderedText());
			LabelStyle woundedTextStyle = UIUtil.createLabelStyle(parent.fonts.damagedFollowerDefText());
			LabelStyle buffedTextStyle = UIUtil.createLabelStyle(parent.fonts.buffedFollowerDefText());

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

			RenderUtil.setLabelBackgroundColor(atkText, parent.DARK_BLUE);
			RenderUtil.setLabelBackgroundColor(defText, parent.DARK_RED);

			atkText.setAlignment(Align.center);
			defText.setAlignment(Align.center);
			permanentGraphic.add(atkText).size(atkText.getWidth() + 7, atkText.getHeight() + 1);
			permanentGraphic.add(defText).size(defText.getWidth() + 7, defText.getHeight() + 1);
		}
		permanentGraphic.setTouchable(Touchable.enabled);
		parent.infoUI.makeClickShowInfoPanel(parent, permanentGraphic);
		return permanentGraphic;
	}

	private void setPermanentGraphicBackGround(PermanentGraphic permanentGraphic) {
		Texture img = parent.assets.get("img/image01.jpg", Texture.class);
		TextureRegionDrawable imgDrawable = new TextureRegionDrawable(new TextureRegion(img));
		permanentGraphic.setBackground(imgDrawable);
	}

	void makeValidUnitsAttackDraggable(MatchScreen parent) {

		dadAttacking.clear();

		PlayerPartitionUIElements elements = parent.getUIElements(parent.match.getActivePlayerNumber());
		PlayerPartitionUIElements elementsNonActive = parent.getUIElements(parent.match.getNonActivePlayerNumber());

		for (PermanentGraphic p : elementsNonActive.listOfFieldGraphics) {
			parent.outlineRenderer.stopDrawingMyOutline(p);
		}

		for (PermanentGraphic p : elements.listOfFieldGraphics) {
			if (p.getPermanent() instanceof Follower) {
				Follower follower = (Follower) p.getPermanent();

				if (follower.canAttackPlayers() || follower.canAttackFollowers()) {
					makeAttackDraggable(p);
				}

				// Set outline around followers that can attack
				if (follower.canAttackPlayers()) {
					parent.outlineRenderer.startDrawingMyOutline(p, Color.GREEN);
				} else if (follower.canAttackFollowers()) {
					parent.outlineRenderer.startDrawingMyOutline(p, Color.YELLOW);
				} else {
					parent.outlineRenderer.stopDrawingMyOutline(p);
				}
			}
		}

		addEnemyFollowersAndLeaderAsDragTargets(parent);

	}

	private void makeAttackDraggable(PermanentGraphic permGraphic) { // cards are cast by dragging to the field
		dadAttacking.addSource(new Source(permGraphic) {
			@Override
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				attackingLine.start
						.set(permGraphic.localToStageCoordinates(new Vector2(permGraphic.getWidth() / 2, permGraphic.getHeight() / 2)));
				attackingLineVisible = true;
				return new Payload();
			}
	
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
				attackingLineVisible = false;
			}
	
		});
	}

	private void addEnemyFollowersAndLeaderAsDragTargets(MatchScreen parent) {
		PlayerPartitionUIElements elements = parent.getUIElements(parent.match.getNonActivePlayerNumber());
		for (PermanentGraphic p : elements.listOfFieldGraphics) {
			if (p.getPermanent() instanceof Follower) {
				Follower defender = (Follower) p.getPermanent();
				addAttackableAsDragTarget(defender, p);
			}
		}
		addAttackableAsDragTarget(parent.match.getNonActivePlayer(), elements.getHandPanel());
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

	void disableValidUnitsAttackDraggable() {

		dadAttacking.clear();

		PlayerPartitionUIElements elements = parent.getUIElements(parent.match.getActivePlayerNumber());

		for (PermanentGraphic p : elements.listOfFieldGraphics) {
			parent.outlineRenderer.stopDrawingMyOutline(p);
		}

	}

	void renderAttackingLineIfVisible() {
		parent.shapeRenderer.begin(ShapeType.Line);
		parent.shapeRenderer.setColor(Color.WHITE);
		if (attackingLineVisible) {
			parent.shapeRenderer.line(attackingLine.start, attackingLine.end);
		}
		parent.shapeRenderer.end();
	}

	void setAttackingLineEndPoint(float x, float y) {
		attackingLine.end.set(x, y);
	}

}
