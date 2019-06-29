package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * Represents an action that includes what targets to hit. This class only deals with follower targets
 * 
 * 
 * 
 * @author Colin Ta
 *
 */
public class FollowerTargetedAction extends TargetedAction {

	// OTHER_ENEMY_FOLLOWERS and THE_ENEMY_FOLLOWER require the parent to be something that identifies an enemy follower, ie. clash or follower strike

	/**
	 * The first follower means that the source is a follower (which adds additional options like SELF, OTHER_ALLIED_FOLLOWERS) <br>
	 * The second follower is the same as class name, means that the target(s) are followers
	 */
	public enum FollowerFollowerTargeting {
		SELF, ALLIED_FOLLOWERS, OTHER_ALLIED_FOLLOWERS, ENEMY_FOLLOWERS, OTHER_ENEMY_FOLLOWERS, THE_ENEMY_FOLLOWER
	}

	public FollowerFollowerTargeting targeting;

	private ActionOnFollower action = null;

	public FollowerTargetedAction(FollowerFollowerTargeting targeting) {
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
