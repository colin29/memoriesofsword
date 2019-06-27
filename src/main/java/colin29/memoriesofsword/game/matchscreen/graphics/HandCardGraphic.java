package colin29.memoriesofsword.game.matchscreen.graphics;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import colin29.memoriesofsword.game.match.CardInfo;

public class HandCardGraphic extends Table {

	private CardInfo parentCard;

	public HandCardGraphic(CardInfo parentCard) {
		this.parentCard = parentCard;
	}

	public CardInfo getParentCard() {
		return parentCard;
	}
}
