package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

public class ActionOnFollowerOrPlayer {
	public enum ActionType {
		HEAL_DEFENSE, DO_DAMAGE
	}

	public final ActionType actionType;
	public int amount;

	public ActionOnFollowerOrPlayer(ActionType actionType) {
		if (actionType == null) {
			throw new InvalidArgumentException("action type can't be null");
		}
		this.actionType = actionType;
	}

	public ActionOnFollowerOrPlayer(ActionOnFollowerOrPlayer src) {
		actionType = src.actionType;
		amount = src.amount;
	}
}
