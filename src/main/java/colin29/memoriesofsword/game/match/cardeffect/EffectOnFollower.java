package colin29.memoriesofsword.game.match.cardeffect;

import java.util.function.Predicate;

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
	 * Inherent Targeting: <br>
	 * ETB_FOLLOWER is used for etb card effects <br>
	 * 
	 * THE_ENEMY_FOLLOWER is used for clash and follower-strike
	 * 
	 * SELECTED_FOLLOWER is a special targeting that indicates the user should be prompted and will chose a follower <br>
	 * Because of complexity in async callbacks and task resuming, SELECTED FOLLOWER is ONLY supported with trigger types: FANFARE atm.
	 * 
	 */
	public enum FollowerTargeting {
		THIS_FOLLOWER, ALLIED_FOLLOWERS, OTHER_ALLIED_FOLLOWERS, ENEMY_FOLLOWERS, ETB_FOLLOWER, THE_ENEMY_FOLLOWER, OTHER_ENEMY_FOLLOWERS, SELECTED_FOLLOWER, SELECTED_ALLIED_FOLLOWER, SELECTED_ENEMY_FOLLOWER;

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
			case ETB_FOLLOWER:
				return "it";
			case SELECTED_FOLLOWER:
				return "a follower";
			case SELECTED_ENEMY_FOLLOWER:
				return "an enemy follower";
			case SELECTED_ALLIED_FOLLOWER:
				return "an allied follower";
			default:
				return "{unknown follower-targeting}";

			}
		}

		public boolean isUsingUserTargeting() {
			return (this == SELECTED_FOLLOWER);
		}
	}

	final public FollowerTargeting targeting;

	private ActionOnFollower action = null;

	/**
	 * Used by targetings that inherently provide a follower target (e.g. ETB_ALLIED_FOLLOWER, CLASH)
	 */
	public Follower THAT_FOLLOWER;

	/**
	 * Used by user prompt targeting (ie. SELECTED_FOLLOWER)
	 */
	public Follower SELECTED_FOLLOWER;

	public EffectOnFollower(FollowerTargeting targeting) {
		this.targeting = targeting;
	}

	public EffectOnFollower(EffectOnFollower src) {
		targeting = src.targeting;
		action = new ActionOnFollower(src.action);

		THAT_FOLLOWER = src.THAT_FOLLOWER;
		SELECTED_FOLLOWER = src.SELECTED_FOLLOWER;
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

	@Override
	public boolean isUsingUserTargeting() {
		return targeting.isUsingUserTargeting();
	}

	private Predicate<Follower> getTargetingPredicate() {

		if (!isUsingUserTargeting()) {
			return (Follower f) -> {
				return true;
			};
		}

		return (Follower f) -> {
			switch (targeting) {
			case SELECTED_FOLLOWER:
				return true;
			case SELECTED_ENEMY_FOLLOWER:
				return f.getOwner() != getSource().getOwner();
			case SELECTED_ALLIED_FOLLOWER:
				return f.getOwner() == getSource().getOwner();
			default:
				throw new AssertionError("Targeting predicate: unsupported targeting type");

			}
		};
	}

	/**
	 * Get the predicate used to determine whether a given target is valid.
	 * 
	 * This is the combination of targeting predicate (e.g. for SELECTED_ALLIED_FOLLOWER) and any filters
	 * 
	 * @return
	 */
	public Predicate<Follower> getPredicate() {
		return (Follower f) -> {
			return getTargetingPredicate().test(f);
		};
	}

}
