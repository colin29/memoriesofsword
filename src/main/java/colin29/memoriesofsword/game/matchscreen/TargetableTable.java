package colin29.memoriesofsword.game.matchscreen;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TargetableTable extends Table implements TargetableActor {

	final PermanentOrPlayer target;

	public TargetableTable(PermanentOrPlayer target) {
		this.target = target;
	}

	@Override
	public PermanentOrPlayer getTarget() {
		return target;
	}

}
