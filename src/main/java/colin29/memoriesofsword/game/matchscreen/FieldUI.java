package colin29.memoriesofsword.game.matchscreen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import colin29.memoriesofsword.game.match.Amulet;
import colin29.memoriesofsword.game.match.Follower;
import colin29.memoriesofsword.game.match.Permanent;
import colin29.memoriesofsword.game.matchscreen.graphics.AmuletGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.FollowerGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.PermanentGraphic;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.UIUtil;

public class FieldUI {
	private final MatchScreen parent;

	public FieldUI(MatchScreen matchScreen) {
		parent = matchScreen;
	}

	PermanentGraphic createPermanentGraphic(final Permanent<?> permanent) {

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

		parent.fieldUI.setPermanentGraphicBackGround(permanentGraphic);
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

	void setPermanentGraphicBackGround(PermanentGraphic permanentGraphic) {
		Texture img = parent.assets.get("img/image01.jpg", Texture.class);
		TextureRegionDrawable imgDrawable = new TextureRegionDrawable(new TextureRegion(img));
		permanentGraphic.setBackground(imgDrawable);
	}

}
