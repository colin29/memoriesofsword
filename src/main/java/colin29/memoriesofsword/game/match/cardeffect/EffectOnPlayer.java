package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.game.match.Player;
import colin29.memoriesofsword.game.matchscreen.PermanentOrPlayer;
import colin29.memoriesofsword.util.StringUtil;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 *
 *
 * Note: getPredicate() don't exist for EffectOnPlayer, because there are only 2 players. If you wanted to restrict that any further, you could just
 * specify a player
 * 
 * @author Colin Ta
 *
 */

public class EffectOnPlayer extends Effect {

	public enum Targeting {
		OWN_LEADER, ENEMY_LEADER, SELECTED_LEADER;

		public String getGameText() {
			switch (this) {
			case OWN_LEADER:
				return "your leader";
			case ENEMY_LEADER:
				return "the enemy leader";
			case SELECTED_LEADER:
				return "a leader";
			default:
				return "{unknown player-targeting}";
			}
		}

		public boolean isUsingUserTargeting() {
			return (this == SELECTED_LEADER);
		}
	}

	public Targeting targeting;

	ActionOnPlayer action = null;

	public Player SELECTED_PLAYER;

	public EffectOnPlayer(Targeting targeting) {
		this.targeting = targeting;
	}

	public EffectOnPlayer(EffectOnPlayer src) {
		targeting = src.targeting;
		action = new ActionOnPlayer(src.action);

		SELECTED_PLAYER = src.SELECTED_PLAYER;
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

	@Override
	public boolean isUsingUserTargeting() {
		return targeting.isUsingUserTargeting();
	}

	@Override
	public boolean isValidTarget(PermanentOrPlayer target) {
		return target instanceof Player;
	}

}
