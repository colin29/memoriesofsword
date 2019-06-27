package colin29.memoriesofsword.game.matchscreen.graphics;

import colin29.memoriesofsword.game.match.Amulet;

public class AmuletGraphic extends PermanentGraphic {

	public AmuletGraphic(Amulet parent) {
		super(parent);
	}

	Amulet getAmulet() {
		return (Amulet) super.getPermanent();
	}
}
