package colin29.memoriesofsword.game.matchscreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import colin29.memoriesofsword.game.match.Player;

public class MiscUI {

	private final MatchScreen parent;

	public MiscUI(MatchScreen matchScreen) {
		parent = matchScreen;
	}

	void updateHpText(int playerNumber) {
		Label hpText = parent.getUIElements(playerNumber).hpText;
		Player player = parent.match.getPlayer(playerNumber);
		hpText.setText("" + player.getHp());
		if (player.getHp() != player.getMaxHp()) {
			hpText.setColor(parent.hpTextWoundedColor);
		} else {
			hpText.setColor(Color.WHITE);
		}
	}

	void updatePlayPointsText(int playerNumber) {
		Label playPointsText = parent.getUIElements(playerNumber).playPointsText;
		Player player = parent.match.getPlayer(playerNumber);
		playPointsText.setText("PP: " + player.getPlayPoints() + " / " + player.getMaxPlayPoints());
	}

	public void updateZoneCountTexts(int playerNumber) {

		PlayerPartitionUIElements elements = parent.getUIElements(playerNumber);
		Player player = parent.match.getPlayer(playerNumber);

		elements.graveyardCountText.setText("" + player.getGraveyard().size());
		elements.deckCountText.setText("" + player.getDeck().size());
		elements.handCountText.setText("" + player.getHand().size());

	}

}
