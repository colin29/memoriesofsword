package colin29.memoriesofsword.game.match.cardeffect;

/**
 * An action (as part of an effect) against a player
 * 
 * @author Colin Ta
 *
 */
public class ActionOnPlayer {
	public enum ActionType {
		HEAL_DEFENSE, DO_DAMAGE, DRAW_CARD
	}

	public int amount; // used by heal_defense, do_damage, draw_card

	ActionType actionType;

	public ActionOnPlayer(ActionType actionType) {
		this.actionType = actionType;
	}

	public ActionOnPlayer(ActionOnPlayer src) {
		actionType = src.actionType;
		amount = src.amount;
	}

}
