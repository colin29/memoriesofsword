package colin29.memoriesofsword.game.matchscreen;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import colin29.memoriesofsword.GameException;
import colin29.memoriesofsword.game.matchscreen.graphics.HandCardGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.PermanentGraphic;

/**
 * Is not the Partition itself, but the UI elements we need a reference to.
 * 
 */
public class PlayerPartitionUIElements {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	TargetableTable handPanel;
	public List<HandCardGraphic> listOfHandGraphics;

	List<PermanentGraphic> listOfFieldGraphics;

	public Table fieldPanel;

	public Label playPointsText;
	public Label hpText;

	public Label graveyardCountText;
	public Label deckCountText;
	public Label handCountText;

	public TextButton endTurnButton;

	public final int playerNumber;

	public PlayerPartitionUIElements(OutlineRenderer outlineRenderer, int playerNumber) {
		listOfHandGraphics = new OutlineSmartArrayList<HandCardGraphic>(outlineRenderer);
		listOfFieldGraphics = new OutlineSmartArrayList<PermanentGraphic>(outlineRenderer);
		this.playerNumber = playerNumber;
	}

	public TargetableTable getHandPanel() {
		return handPanel;
	}

	/**
	 * Hand panel is effectively final, it can only be set once
	 */
	public void setHandPanel(TargetableTable handPanel) {
		if (handPanel == null) {
			logger.warn("Hand panel can't be set to null");
			return;
		}
		if (this.handPanel != null) {
			throw new GameException("HandPanel can't be re-assigned after it is set");
		}
		this.handPanel = handPanel;
	}

}