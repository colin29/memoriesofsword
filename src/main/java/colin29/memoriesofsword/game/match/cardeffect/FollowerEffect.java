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

	/**
	 * 
	 * Stat Changes: effect is an applied effect that modifies atk/def.
	 * 
	 * Triggered Abilities: Includes fanfare, clash, as well as custom triggered effects like on other follower EtB
	 * 
	 * Properties: Things that don't do anything, but rather are checked. "Bane, Ward, Rush, Storm" as well as: "Can't attack, Can attack twice, can't
	 * attack the enemy leader, etc."
	 *
	 */
	public enum Type {
		STAT_CHANGE, PROPERTY, TRIGGERED_EFFECT;
	}

	public enum TriggeredEffectType {
		FANFARE, CLASH, STRIKE, FOLLOWER_STRIKE, LEADER_STRIKE, LAST_WORD
	}

	public enum PropertyEffectType { // none atm
	}

	public final Type type;

	// For triggered effects only
	public TriggeredEffectType triggeredEffectType;
	private List<TargetedAction> triggeredAction = new ArrayList<TargetedAction>();

	/**
	 * Creates a Triggered effect
	 */
	public FollowerEffect(TriggeredEffectType triggeredEffectType) {
		type = Type.TRIGGERED_EFFECT;
		this.triggeredEffectType = triggeredEffectType;
	}

	public FollowerEffect(Type type) {
		if (type == Type.STAT_CHANGE) {
			throw new UnsupportedOperationException("Stat change effects not supported (they are unrecorded)");
		}
		this.type = type;
	}

	/**
	 * Copy constructor
	 */
	public FollowerEffect(FollowerEffect src) {
		this.type = src.type;
		this.triggeredEffectType = src.triggeredEffectType;
		triggeredAction = new ArrayList<TargetedAction>();
		for (TargetedAction part : src.triggeredAction) {
			triggeredAction.add(part.cloneObject());
		}

	}

	public List<TargetedAction> getTriggeredActions() {
		return triggeredAction;
	}

	public void addTriggeredAction(TargetedAction effect) {
		if (effect == null) {
			logger.warn("Tried to add null targeted Effect.");
			return;
		}
		triggeredAction.add(effect);
	}

}
