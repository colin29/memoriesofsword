package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * Represents an action that targets a follower
 * 
 * The source can be anything (it isn't tracked atm): A Spell-type Card, A Follower, or an Amulet.
 * 
 * @author Colin Ta
 *
 */
public class FollowerTargetedAction extends TargetedAction {

	// OTHER_ENEMY_FOLLOWERS and THE_ENEMY_FOLLOWER require the parent to be something that identifies an enemy follower, ie. clash or follower strike

	/**
	 * THIS_FOLLOWER AND OTHER_ALLIES is only valid if the source (which holds the effect) is a follower. <br>
	 * This will be checked when match tries to execute the ability.
	 */
	public enum FollowerTargeting {
		THIS_FOLLOWER, ALLIED_FOLLOWERS, OTHER_ALLIED_FOLLOWERS, ENEMY_FOLLOWERS, OTHER_ENEMY_FOLLOWERS, THE_ENEMY_FOLLOWER
	}

	public FollowerTargeting targeting;

	private ActionOnFollower action = null;

	public FollowerTargetedAction(FollowerTargeting targeting) {
		this.targeting = targeting;
	}

	public FollowerTargetedAction(FollowerTargetedAction src) {
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
	public TargetedAction cloneObject() {
		return new FollowerTargetedAction(this);
	}

	public ActionOnFollower getAction() {
		return action;
	}

}
