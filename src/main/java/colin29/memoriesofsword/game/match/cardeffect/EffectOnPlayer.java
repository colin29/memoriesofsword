package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.util.StringUtil;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

public class EffectOnPlayer extends Effect {

	public enum Targeting {
		OWN_LEADER, ENEMY_LEADER;

		public String getGameText() {
			switch (this) {
			case OWN_LEADER:
				return "your leader";
			case ENEMY_LEADER:
				return "the enemy leader";
			default:
				return "{unknown player-targeting}";
			}
		}
	}

	public Targeting targeting;

	ActionOnPlayer action = null;

	public EffectOnPlayer(Targeting targeting) {
		this.targeting = targeting;
	}

	public EffectOnPlayer(EffectOnPlayer src) {
		targeting = src.targeting;
		action = new ActionOnPlayer(src.action);
	}

	@Override
	public Effect cloneObject() {
		return new EffectOnPlayer(this);
	}

	public void setAction(ActionOnPlayer action) {
		if (action == null) {
			throw new InvalidArgumentException("action can't be null");
		}
		this.action = action;
	}

	public ActionOnPlayer getAction() {
		return action;
	}

	@Override
	public String toString() {
		if (action == null) {
			return "{no action}";
		}

		switch (action.actionType) {
		case DO_DAMAGE:
			return String.format("Do %o damage to %s", action.amount, targeting.getGameText());
		case HEAL_DEFENSE:
			return String.format("Restore %o defense to %s", action.amount, targeting.getGameText());
		case DRAW_CARD:
			String xCards = action.amount == 1 ? "a card" : action.amount + " cards";
			switch (targeting) {
			case ENEMY_LEADER:
				return String.format("%s draws %s", StringUtil.capitalize(targeting.getGameText()), xCards);
			case OWN_LEADER:
				return "Draw " + action.amount + " cards";
			default:
				return "{Unknown player-target for draw card}";
			}
		default:
			return noStringRepText;
		}

	}

}
