package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * Indicates an action that can be done to a follower
 * 
 * Includes fields from all types, only the applicable ones are used
 * 
 * @author Colin Ta
 *
 */
public class ActionOnFollower {
	public enum ActionType {
		HEAL_DEFENSE, DO_DAMAGE, BUFF, GIVE_APPLIED_EFFECT
	}

	public int amount; // used by heal_defense, do_damage
	public int atkBuff; // used by buff
	public int defBuff;
	public FollowerEffect appliedEffect; // used by give_applied_effect, which just uses this field

	public ActionType actionType;

	public ActionOnFollower(ActionType actionType) {
		if (actionType == null) {
			throw new InvalidArgumentException("action type can't be null");
		}
		this.actionType = actionType;

	}

	public ActionOnFollower(ActionOnFollower src) {
		amount = src.amount;
		atkBuff = src.atkBuff;
		defBuff = src.defBuff;
		appliedEffect = src.appliedEffect;

		actionType = src.actionType;
	}

}
