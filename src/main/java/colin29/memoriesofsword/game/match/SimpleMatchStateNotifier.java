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
		for (SimpleMatchStateListener simpleListener : simpleListeners) {
			simpleListener.cardOrPermanentStatsModified();
		}
	}

	public void notifyFieldModified(int playerNumber) {
		for (SimpleMatchStateListener simpleListener : simpleListeners) {
			simpleListener.fieldModified(playerNumber);
		}
	}

	public void notifyHandModified(int playerNumber) {
		for (SimpleMatchStateListener simpleListener : simpleListeners) {
			simpleListener.handModified(playerNumber);
		}
	}

	public void notifyDeckModified(int playerNumber) {
		for (SimpleMatchStateListener simpleListener : simpleListeners) {
			simpleListener.deckModified(playerNumber);
		}
	}

	public void notifyGraveyardModified(int playerNumber) {
		for (SimpleMatchStateListener simpleListener : simpleListeners) {
			simpleListener.graveyardModified(playerNumber);
		}
	}

	public void notifyPlayerHPModified(int playerNumber) {
		for (SimpleMatchStateListener simpleListener : simpleListeners) {
			simpleListener.playerHPModified(playerNumber);
		}
	}

	public void notifyPlayPointsModified(int playerNumber) {
		for (SimpleMatchStateListener simpleListener : simpleListeners) {
			simpleListener.playPointsModified(playerNumber);
		}
	}

	public void notifyTurnedChanged() {
		for (SimpleMatchStateListener simpleListener : simpleListeners) {
			simpleListener.turnChanged();
		}
	}

}
