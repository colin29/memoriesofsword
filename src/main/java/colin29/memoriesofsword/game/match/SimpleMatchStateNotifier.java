package colin29.memoriesofsword.game.match;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SimpleMatchStateNotifier {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private ArrayList<SimpleMatchStateListener> simpleListeners = new ArrayList<SimpleMatchStateListener>();

	public void addSimpleStateListener(SimpleMatchStateListener listener) {
		logger.debug("listener registered");
		simpleListeners.add(listener);
	}

	public void notifyCardStatsModified() {
		simpleListeners.forEach((listener) -> listener.cardOrPermanentStatsModified());
	}

	/**
	 * See {@link SimpleMatchStateListener#cardOrPermanentEffectsModified}
	 */
	public void notifyCardEffectsModified() {
		simpleListeners.forEach((listener) -> listener.cardOrPermanentEffectsModified());
	}

	public void notifyUnitAttacked() {
		simpleListeners.forEach((listener) -> listener.unitAttacked());
	}

	public void notifyFieldModified(int playerNumber) {
		simpleListeners.forEach((listener) -> listener.fieldModified(playerNumber));
	}

	public void notifyHandModified(int playerNumber) {
		simpleListeners.forEach((listener) -> listener.handModified(playerNumber));
	}

	public void notifyDeckModified(int playerNumber) {
		simpleListeners.forEach((listener) -> listener.deckModified(playerNumber));
	}

	public void notifyGraveyardModified(int playerNumber) {
		simpleListeners.forEach((listener) -> listener.graveyardModified(playerNumber));
	}

	public void notifyPlayerHPModified(int playerNumber) {
		simpleListeners.forEach((listener) -> listener.playerHPModified(playerNumber));
	}

	public void notifyPlayPointsModified(int playerNumber) {
		simpleListeners.forEach((listener) -> listener.playPointsModified(playerNumber));
	}

	public void notifyTurnedChanged() {
		simpleListeners.forEach((listener) -> listener.turnChanged());
	}

}
