package colin29.memoriesofsword.game.match.cardeffect;

public class PlayerTargetedAction extends TargetedAction {

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

	Targeting targeting;

	ActionOnPlayer action = null;

	public PlayerTargetedAction(Targeting targeting) {
		this.targeting = targeting;
	}

	public PlayerTargetedAction(PlayerTargetedAction src) {
		targeting = src.targeting;
		action = new ActionOnPlayer(src.action);
	}

	@Override
	public TargetedAction cloneObject() {
		return new PlayerTargetedAction(this);
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
				return String.format("%s draws %s", capitalize(targeting.getGameText()), xCards);
			case OWN_LEADER:
				return "Draw " + action.amount + " cards";
			default:
				return "{Unknown player-target for draw card}";
			}
		default:
			return noStringRepText;
		}

	}

	/**
	 * Converts the first character of a string to uppercase
	 */
	public static String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

}
