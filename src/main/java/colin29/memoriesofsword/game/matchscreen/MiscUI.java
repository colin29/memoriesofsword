package colin29.memoriesofsword.game.matchscreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import colin29.memoriesofsword.game.match.Match;
import colin29.memoriesofsword.game.match.Player;
import colin29.memoriesofsword.util.RenderUtil;

public class MiscUI {

	private final MatchScreen parent;
	final Color hpPlayerTextWoundedColor = RenderUtil.rgb(255, 128, 128); // pale red

	final Match match;

	public MiscUI(MatchScreen parent) {
		this.parent = parent;
		match = parent.match;
	}

	void updateHpText(int playerNumber) {
		Label hpText = parent.getUIElements(playerNumber).hpText;
		Player player = match.getPlayer(playerNumber);
		hpText.setText("" + player.getHp());
		if (player.getHp() != player.getMaxHp()) {
			hpText.setColor(hpPlayerTextWoundedColor);
		} else {
			hpText.setColor(Color.WHITE);
		}
	}

	void updatePlayPointsText(int playerNumber) {
		Label playPointsText = parent.getUIElements(playerNumber).playPointsText;
		Player player = match.getPlayer(playerNumber);
		playPointsText.setText("PP: " + player.getPlayPoints() + " / " + player.getMaxPlayPoints());
	}

	public void updateZoneCountTexts(int playerNumber) {

		PlayerPartitionUIElements elements = parent.getUIElements(playerNumber);
		Player player = match.getPlayer(playerNumber);

		elements.graveyardCountText.setText("" + player.getGraveyard().size());
		elements.deckCountText.setText("" + player.getDeck().size());
		elements.handCountText.setText("" + player.getHand().size());

	}

	void updateEndTurnButtonDisabledStatus(int playerNumber) {
		PlayerPartitionUIElements elements = parent.getUIElements(playerNumber);
		if (playerNumber == match.getActivePlayerNumber()) {
			elements.endTurnButton.setDisabled(false);
		} else {
			elements.endTurnButton.setDisabled(true);
		}

	}

	void updateAllEndTurnButtonDisabledStatus() {
		for (int playerNumber = 1; playerNumber <= 2; playerNumber++) {
			updateEndTurnButtonDisabledStatus(playerNumber);
		}
	}

	void disableActivePlayerEndTurnButton() {
		parent.getUIElements(match.getActivePlayerNumber()).endTurnButton.setDisabled(true);
	}

	void enableActivePlayerEndTurnButton() {
		parent.getUIElements(match.getActivePlayerNumber()).endTurnButton.setDisabled(false);
	}

}
