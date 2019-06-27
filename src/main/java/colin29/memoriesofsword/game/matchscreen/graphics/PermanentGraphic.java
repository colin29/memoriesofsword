package colin29.memoriesofsword.game.matchscreen.graphics;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import colin29.memoriesofsword.game.match.Permanent;

public class PermanentGraphic extends Table {

	private final Permanent permanent;

	public PermanentGraphic(Permanent parent) {
		this.permanent = parent;
	}

	public Permanent getPermanent() {
		return permanent;
	}

}
