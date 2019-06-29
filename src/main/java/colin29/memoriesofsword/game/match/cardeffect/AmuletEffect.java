package colin29.memoriesofsword.game.match.cardeffect;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmuletEffect extends Effect {

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
	private List<TargetedAction> triggeredActions = new ArrayList<TargetedAction>();

	public PropertyEffectType propertyEffectType;

	/**
	 * Creates a Triggered effect
	 */
	public AmuletEffect(TriggerType triggeredEffectType) {
		type = Type.TRIGGERED_EFFECT;
		this.triggeredEffectType = triggeredEffectType;
	}

	public AmuletEffect(Type type) {
		this.type = type;
	}

	/**
	 * Copy constructor
	 */
	public AmuletEffect(AmuletEffect src) {
		this.type = src.type;
		this.triggeredEffectType = src.triggeredEffectType;
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
			s.append(triggeredEffectType.getGameText() + ": ");
			for (TargetedAction targetedAction : triggeredActions) {
				s.append(targetedAction.toString() + ". ");
			}
			return s.toString();
		default:
			return type.name() + " string rep not supported yet";
		}
	}
}
