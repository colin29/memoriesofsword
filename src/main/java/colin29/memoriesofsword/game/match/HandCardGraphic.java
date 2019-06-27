package colin29.memoriesofsword.game.match;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class HandCardGraphic extends Table {

	private CardInfo parentCard;

	HandCardGraphic(CardInfo parentCard) {
		this.parentCard = parentCard;
	}

	public CardInfo getParentCard() {
		return parentCard;
	}
}
