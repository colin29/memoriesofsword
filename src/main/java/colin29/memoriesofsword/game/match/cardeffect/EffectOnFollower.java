package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.game.match.Follower;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * Represents an action that targets a follower
 * 
 * The source can be anything (it isn't tracked atm): A Spell-type Card, A Follower, or an Amulet.
 * 
 * It is valid for a targeted action to have no action, though only initially (you can't set it to null)
 * 
 * @author Colin Ta
 *
 */
public class EffectOnFollower extends Effect {

	// OTHER_ENEMY_FOLLOWERS and THE_ENEMY_FOLLOWER require the parent to be something that identifies an enemy follower, ie. clash or follower strike

	/**
	 * THIS_FOLLOWER AND OTHER_ALLIES is only valid if the source (which holds the effect) is a follower. <br>
	 * This will be checked when match tries to execute the ability.
	 * 
	 */
	public enum FollowerTargeting {
		THIS_FOLLOWER, ALLIED_FOLLOWERS, OTHER_ALLIED_FOLLOWERS, ENEMY_FOLLOWERS, THE_FOLLOWER, THE_ENEMY_FOLLOWER, OTHER_ENEMY_FOLLOWERS;

		public String getGameText() {
			switch (this) {
			case THIS_FOLLOWER:
				return "this follower";
			case ALLIED_FOLLOWERS:
				return "all allied followers";
			case OTHER_ALLIED_FOLLOWERS:
				return "all other allied followers";
			case ENEMY_FOLLOWERS:
				return "all enemy followers";
			case THE_ENEMY_FOLLOWER:
				return "the enemy follower";
			case OTHER_ENEMY_FOLLOWERS:
				return "all other enemy followers";
			default:
				return "{unknown follower-targeting}";
			}
		}
	}

	public Follower THAT_FOLLOWER; // Used by targetings that inherently provide a target (e.g. ETB_ALLIED_FOLLOWER)

	public FollowerTargeting targeting;

	private ActionOnFollower action = null;

	public EffectOnFollower(FollowerTargeting targeting) {
		this.targeting = targeting;
	}

	public EffectOnFollower(EffectOnFollower src) {
		targeting = src.targeting;
		action = new ActionOnFollower(src.action);
	}

	public void setAction(ActionOnFollower action) {
		if (action == null) {
			throw new InvalidArgumentException("action can't be null");
		}
		this.action = action;
	}

	@Override
	public Effect cloneObject() {
		return new EffectOnFollower(this);
	}

	public ActionOnFollower getAction() {
		return action;
	}

	@Override
	public String toString() {
		if (action == null) {
			return "{no action}";
		}

		switch (action.actionType) {
		case BUFF:
			return String.format("Give +%o/+%o to %s", action.atkBuff, action.defBuff, targeting.getGameText());
		case DO_DAMAGE:
			return String.format("Do %o damage to %s", action.amount, targeting.getGameText());
		case GIVE_APPLIED_EFFECT:
			return "{give_applied_effect actions not supported yet}";
		case HEAL_DEFENSE:
			return String.format("Restore %o defense to %s", action.amount, targeting.getGameText());
		default:
			return noStringRepText;
		}
	}

}
