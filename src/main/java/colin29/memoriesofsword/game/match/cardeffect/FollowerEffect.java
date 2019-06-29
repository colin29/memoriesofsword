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
public class FollowerEffect extends Effect {

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

	public enum TriggerType {
		FANFARE, CLASH, STRIKE, FOLLOWER_STRIKE, LEADER_STRIKE, LAST_WORD;

		public String getGameText() {
			switch (this) {
			case FANFARE:
				return "Fanfare";
			case CLASH:
				return "Clash";
			case STRIKE:
				return "Strike";
			case FOLLOWER_STRIKE:
				return "Follower Strike";
			case LEADER_STRIKE:
				return "Leader Strike";
			case LAST_WORD:
				return "Last Words";
			default:
				return "{no string-rep for this trigger-type}";

			}
		}
	}

	public enum PropertyEffectType { // none atm
	}

	public final Type type;

	// For triggered effects only
	public TriggerType triggerType;
	private List<TargetedAction> triggeredActions = new ArrayList<TargetedAction>();

	public PropertyEffectType propertyEffectType;

	/**
	 * Creates a Triggered effect
	 */
	public FollowerEffect(TriggerType triggeredEffectType) {
		type = Type.TRIGGERED_EFFECT;
		this.triggerType = triggeredEffectType;
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
		this.triggerType = src.triggerType;
		triggeredActions = new ArrayList<TargetedAction>();
		for (TargetedAction part : src.triggeredActions) {
			triggeredActions.add(part.cloneObject());
		}

	}

	public List<TargetedAction> getTriggeredActions() {
		return triggeredActions;
	}

	public void addTriggeredAction(TargetedAction effect) {
		if (effect == null) {
			logger.warn("Tried to add null targeted Effect.");
			return;
		}
		triggeredActions.add(effect);
	}

	@Override
	public String toString() {
		switch (type) {
		case TRIGGERED_EFFECT:
			StringBuilder s = new StringBuilder();
			s.append(triggerType.getGameText() + ": ");
			for (TargetedAction targetedAction : triggeredActions) {
				s.append(targetedAction.toString() + ". ");
			}
			return s.toString();
		default:
			return type.name() + " string rep not supported yet";
		}
	}
}
