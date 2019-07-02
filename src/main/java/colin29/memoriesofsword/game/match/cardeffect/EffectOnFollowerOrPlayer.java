package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

public class EffectOnFollowerOrPlayer extends Effect {

	/**
	 * 
	 * @author Colin Ta
	 *
	 */
	public enum FollowerOrPlayerTargeting {
		ALL_ENEMIES, ALL_ALLIES;

		public String getGameText() {
			switch (this) {
			case ALL_ALLIES:
				return "all allies";
			case ALL_ENEMIES:
				return "all enemies";
			default:
				return "{Unknown FollowerOrPlayer effect}";
			}
		}
	}

	final public FollowerOrPlayerTargeting targeting;
	private ActionOnFollowerOrPlayer action;

	public EffectOnFollowerOrPlayer(FollowerOrPlayerTargeting targeting) {
		this.targeting = targeting;
	}

	public EffectOnFollowerOrPlayer(EffectOnFollowerOrPlayer src) {
		targeting = src.targeting;
		action = new ActionOnFollowerOrPlayer(src.action);
	}

	public void setAction(ActionOnFollowerOrPlayer action) {
		if (action == null) {
			throw new InvalidArgumentException("action can't be null");
		}
		this.action = action;
	}

	@Override
	public Effect cloneObject() {
		return new EffectOnFollowerOrPlayer(this);
	}

	public ActionOnFollowerOrPlayer getAction() {
		return action;
	}

	@Override
	public String toString() {
		switch (action.actionType) {
		case DO_DAMAGE:
			return String.format("Do %o damage to %s", action.amount, targeting.getGameText());
		case HEAL_DEFENSE:
			return String.format("Restore %o defense to %s", action.amount, targeting.getGameText());
		default:
			return noStringRepText;
		}
	}

}
