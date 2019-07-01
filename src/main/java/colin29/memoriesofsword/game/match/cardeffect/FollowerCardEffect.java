package colin29.memoriesofsword.game.match.cardeffect;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.util.StringUtil;

/**
 * An effect that is held by a Follower on the field, or by a Follower-type Card
 * 
 * @author Colin Ta
 *
 */
public class FollowerCardEffect extends CardEffect {

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
		FANFARE, CLASH, STRIKE, FOLLOWER_STRIKE, LEADER_STRIKE, LAST_WORD, ETB_ALLIED_FOLLOWER, THIS_FOLLOWER_BUFFED;

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
			case ETB_ALLIED_FOLLOWER:
				return "Whenever an allied follower enters the battlefield";
			case THIS_FOLLOWER_BUFFED:
				return "Whenever this follower's stats are increased";
			default:
				return "{no string-rep for this trigger-type}";

			}
		}
	}

	public enum PropertyType {
		RUSH, STORM;

		public String getGameText() {
			switch (this) {
			case RUSH:
				return "Rush";
			case STORM:
				return "Storm";
			default:
				return "{no string-rep for this property-type}";
			}

		}
	}

	public final Type type;

	// For triggered effects only
	public TriggerType triggerType;
	private List<Effect> triggeredEffects = new ArrayList<Effect>();

	public PropertyType propertyType;

	/**
	 * Creates a Triggered effect
	 */
	public FollowerCardEffect(TriggerType triggerType) {
		type = Type.TRIGGERED_EFFECT;
		this.triggerType = triggerType;
	}

	public FollowerCardEffect(PropertyType propertyType) {
		type = Type.PROPERTY;
		this.propertyType = propertyType;
	}

	public FollowerCardEffect(Type type) {
		if (type == Type.STAT_CHANGE) {
			throw new UnsupportedOperationException("Stat change effects not supported (they are unrecorded)");
		}
		this.type = type;
	}

	/**
	 * Copy constructor
	 */
	public FollowerCardEffect(FollowerCardEffect src) {
		this.type = src.type;
		this.triggerType = src.triggerType;
		this.propertyType = src.propertyType;
		triggeredEffects = new ArrayList<Effect>();
		for (Effect part : src.triggeredEffects) {
			triggeredEffects.add(part.cloneObject());
		}

	}

	public List<Effect> getTriggeredEffects() {
		return triggeredEffects;
	}

	public void addTriggeredEffect(Effect effect) {
		if (effect == null) {
			logger.warn("Tried to add null targeted Effect.");
			return;
		}
		triggeredEffects.add(effect);
	}

	@Override
	public String toString() {
		switch (type) {
		case TRIGGERED_EFFECT:
			StringBuilder s = new StringBuilder();
			if (triggerType == TriggerType.ETB_ALLIED_FOLLOWER) {
				s.append(triggerType.getGameText() + ", ");
				boolean first = true;
				for (Effect targetedAction : triggeredEffects) {
					if (first) {
						s.append(StringUtil.decapitalize(targetedAction.toString()) + ". ");
						first = false;
					} else {
						s.append(targetedAction.toString() + ". ");
					}
				}
			} else {
				s.append(triggerType.getGameText() + ": ");
				for (Effect targetedAction : triggeredEffects) {
					s.append(targetedAction.toString() + ". ");
				}
			}
			return s.toString();
		case PROPERTY:
			return propertyType.getGameText();
		default:
			return type.name() + " string rep not supported yet";
		}
	}
}
