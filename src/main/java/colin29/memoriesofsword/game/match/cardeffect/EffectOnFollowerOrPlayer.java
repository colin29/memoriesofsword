package colin29.memoriesofsword.game.match.cardeffect;

import java.util.function.Predicate;

import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

public class EffectOnFollowerOrPlayer extends Effect {

	/**
	 * 
	 * @author Colin Ta
	 *
	 */
	public enum FollowerOrPlayerTargeting {
		ALL_ENEMIES, ALL_ALLIES, SELECTED_TARGET;

		public String getGameText() {
			switch (this) {
			case ALL_ALLIES:
				return "all allies";
			case ALL_ENEMIES:
				return "all enemies";
			case SELECTED_TARGET:
				return "an ally or enemy";
			default:
				return "{Unknown FollowerOrPlayer effect}";
			}
		}

		public boolean isUsingUserTargeting() {
			return (this == SELECTED_TARGET);
		}
	}

	public FollowerOrPlayer SELECTED_TARGET;

	final public FollowerOrPlayerTargeting targeting;
	private ActionOnFollowerOrPlayer action;

	public EffectOnFollowerOrPlayer(FollowerOrPlayerTargeting targeting) {
		this.targeting = targeting;
	}

	public EffectOnFollowerOrPlayer(EffectOnFollowerOrPlayer src) {
		targeting = src.targeting;
		action = new ActionOnFollowerOrPlayer(src.action);

		SELECTED_TARGET = src.SELECTED_TARGET;
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

	@Override
	public boolean isUsingUserTargeting() {
		return targeting.isUsingUserTargeting();
	}

	public Predicate<FollowerOrPlayer> getPredicate() {
		return (FollowerOrPlayer) -> { // only SELECTED_TARGET exists, which can take any FollowerorPlayer
			return true;
		};
	}

}
