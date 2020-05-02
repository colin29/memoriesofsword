package colin29.memoriesofsword.game.matchscreen.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import colin29.memoriesofsword.game.match.Permanent;
import colin29.memoriesofsword.game.matchscreen.PermanentOrPlayer;
import colin29.memoriesofsword.game.matchscreen.TargetableActor;

public class PermanentGraphic extends Table implements TargetableActor {

	private final Permanent<?> permanent;

	public PermanentGraphic(Permanent<?> parent) {
		this.permanent = parent;
	}

	public Permanent<?> getPermanent() {
		return permanent;
	}

	@Override
	public PermanentOrPlayer getTarget() {
		return permanent;
	}

	public void setPermanentGraphicBackGround(Texture img) {
		TextureRegionDrawable imgDrawable = new TextureRegionDrawable(new TextureRegion(img));
		setBackground(imgDrawable);
	}

}
