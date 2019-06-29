package colin29.memoriesofsword.game.match.cardeffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an action that includes what targets to hit. This class only deals with follower targets
 * 
 * 
 * 
 * @author Colin Ta
 *
 */
public class FollowerTargetedListOfActions extends TargetedListOfActions {

	// OTHER_ENEMY_FOLLOWERS and THE_ENEMY_FOLLOWER require the parent to be something that identifies an enemy follower, ie. clash or follower strike

	/**
	 * The first follower means that the source is a follower (which adds additional options like SELF, OTHER_ALLIED_FOLLOWERS) <br>
	 * The second follower is the same as class name, means that the target(s) are followers
	 */
	public enum FollowerFollowerTargeting {
		SELF, ALLIED_FOLLOWERS, OTHER_ALLIED_FOLLOWERS, ENEMY_FOLLOWERS, OTHER_ENEMY_FOLLOWERS, THE_ENEMY_FOLLOWER
	}

	public FollowerFollowerTargeting targeting;

	private List<ActionOnFollower> listOfActions = new ArrayList<ActionOnFollower>();

	public FollowerTargetedListOfActions(FollowerFollowerTargeting targeting) {
		this.targeting = targeting;
	}

	public FollowerTargetedListOfActions(FollowerTargetedListOfActions src) {
		this.targeting = src.targeting;
		for (ActionOnFollower action : src.listOfActions) {
			listOfActions.add(new ActionOnFollower(action));
		}
	}

	public void addAction(ActionOnFollower action) {
		listOfActions.add(action);
	}

	@Override
	public TargetedListOfActions cloneObject() {
		return new FollowerTargetedListOfActions(this);
	}

	public List<ActionOnFollower> getListOfActions() {
		return Collections.unmodifiableList(listOfActions);
	}

}
