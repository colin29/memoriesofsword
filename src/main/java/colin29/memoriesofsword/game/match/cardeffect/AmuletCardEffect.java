package colin29.memoriesofsword.game.match.cardeffect;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmuletCardEffect extends CardEffect {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public enum Type {
		PROPERTY, TRIGGERED_EFFECT;
	}

	public enum TriggerType {
		FANFARE, LAST_WORD, ETB_ALLIED_FOLLOWER;

		public String getGameText() {
			switch (this) {
			case FANFARE:
				return "Fanfare";
			case LAST_WORD:
				return "Last words";
			case ETB_ALLIED_FOLLOWER:
				return "When an allied follower enters the battlefield";
			default:
				return "{No stringrep for this amulet trigger-type}";

			}
		}
	}

	public enum PropertyEffectType { // none atm
	}

	public final Type type;

	// For triggered effects only
	public TriggerType triggeredEffectType;
	private List<Effect> triggeredEffects = new ArrayList<Effect>();

	public PropertyEffectType propertyEffectType;

	/**
	 * Creates a Triggered effect
	 */
	public AmuletCardEffect(TriggerType triggeredEffectType) {
		type = Type.TRIGGERED_EFFECT;
		this.triggeredEffectType = triggeredEffectType;
	}

	public AmuletCardEffect(Type type) {
		this.type = type;
	}

	/**
	 * Copy constructor
	 */
	public AmuletCardEffect(AmuletCardEffect src) {
		this.type = src.type;
		this.triggeredEffectType = src.triggeredEffectType;
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
			s.append(triggeredEffectType.getGameText() + ": ");
			for (Effect targetedAction : triggeredEffects) {
				s.append(targetedAction.toString() + ". ");
			}
			return s.toString();
		default:
			return type.name() + " string rep not supported yet";
		}
	}
}
