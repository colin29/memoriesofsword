package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * An action just specifies a thing to do. It doesn't include what to target (which is an Effect).
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
	public FollowerCardEffect appliedEffect; // used by give_applied_effect, which just uses this field

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
