package colin29.memoriesofsword.game.match.cardeffect;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An effect that is held by a Follower on the field, or by a Follower-type Card
 * 
 * @author Colin Ta
 *
 */
public class FollowerEffect {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public enum Type {
		FANFARE, CLASH, STRIKE, FOLLOWER_STRIKE, LEADER_STRIKE, LAST_WORD, TRIGGERED_EFFECT
	}

	public Type type;

	private List<TargetedAction> listOfTargetedEffects = new ArrayList<TargetedAction>();

	public FollowerEffect(Type type) {
		this.type = type;
	}

	/**
	 * Copy constructor
	 */
	public FollowerEffect(FollowerEffect src) {
		this.type = src.type;
		listOfTargetedEffects = new ArrayList<TargetedAction>();
		for (TargetedAction part : src.listOfTargetedEffects) {
			listOfTargetedEffects.add(part.cloneObject());
		}

	}

	public List<TargetedAction> getParts() {
		return listOfTargetedEffects;
	}

	public void addPart(TargetedAction effect) {
		if (effect == null) {
			logger.warn("Tried to add null targeted Effect.");
			return;
		}
		listOfTargetedEffects.add(effect);
	}

}
