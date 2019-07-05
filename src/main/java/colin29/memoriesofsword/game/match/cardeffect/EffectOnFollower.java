package colin29.memoriesofsword.game.match.cardeffect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import colin29.memoriesofsword.game.match.Follower;
import colin29.memoriesofsword.game.match.cardeffect.filter.FollowerFilter;
import colin29.memoriesofsword.game.matchscreen.PermanentOrPlayer;
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
			switch (this) {
			case SELECTED_FOLLOWER:
			case SELECTED_ENEMY_FOLLOWER:
			case SELECTED_ALLIED_FOLLOWER:
				return true;
			default:
				return false;
			}
		}

		/**
		 * @return true if this targeting type is directed at a group and not a single target
		 */
		public boolean isAoETargeting() {
			switch (this) { // intentional use of fall-through
			case ALLIED_FOLLOWERS:
			case OTHER_ALLIED_FOLLOWERS:
			case ENEMY_FOLLOWERS:
			case OTHER_ENEMY_FOLLOWERS:
				return true;
			default:
				return false;
			}
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

	/**
	 * If the effect is a selected effect, filters restrict the targeting. <br>
	 * For other targeting types, they make the effect conditional
	 */
	private List<FollowerFilter> filters = new ArrayList<FollowerFilter>();

	public EffectOnFollower(FollowerTargeting targeting) {
		this.targeting = targeting;
	}

	public EffectOnFollower(EffectOnFollower src) {
		targeting = src.targeting;
		action = new ActionOnFollower(src.action);

		THAT_FOLLOWER = src.THAT_FOLLOWER;
		SELECTED_FOLLOWER = src.SELECTED_FOLLOWER;
		for (FollowerFilter f : src.filters) {
			filters.add(f.cloneObject());
		}
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

	public void addFilter(FollowerFilter filter) {
		if (!targeting.isUsingUserTargeting() && !targeting.isAoETargeting()) {
			logger.warn("Filters are only valid with Effects that use User Targeting or are AoE Targeting. AddFilter ignored.");
			return;
		}
		filters.add(filter);
	}

	@Override
	public String toString() {
		if (action == null) {
			return "{no action}";
		}

		switch (action.actionType) {
		case BUFF:
			return String.format("Give +%o/+%o to %s%s", action.atkBuff, action.defBuff, targeting.getGameText(), getFiltersText());
		case DO_DAMAGE:
			return String.format("Do %o damage to %s%s", action.amount, targeting.getGameText(), getFiltersText());
		case GIVE_APPLIED_EFFECT:
			return "{give_applied_effect actions not supported yet}";
		case HEAL_DEFENSE:
			return String.format("Restore %o defense to %s%s", action.amount, targeting.getGameText(), getFiltersText());
		default:
			return noStringRepText;
		}
	}

	private String getFiltersText() {
		String str = "";
		for (FollowerFilter filter : filters) {
			str += " " + filter.toString(targeting.isAoETargeting());
		}
		return str;
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

	public Predicate<Follower> getFiltersPredicate() {
		return (Follower f) -> {
			for (FollowerFilter filter : filters) {
				if (!filter.getPredicate().test(f)) {
					return false;
				}
			}
			return true;
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
			return getTargetingPredicate().test(f) && getFiltersPredicate().test(f);
		};
	}

	@Override
	public boolean isValidTarget(PermanentOrPlayer target) {
		if (target instanceof Follower) {
			return getPredicate().test((Follower) target);
		} else {
			return false;
		}
	}

}
