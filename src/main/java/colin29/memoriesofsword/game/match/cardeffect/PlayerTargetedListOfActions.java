package colin29.memoriesofsword.game.match.cardeffect;

import java.util.ArrayList;
import java.util.List;

public class PlayerTargetedListOfActions extends TargetedListOfActions {

	public enum Targeting {
		OWN_LEADER, ENEMY_LEADER;
	}

	Targeting targeting;

	List<ActionOnPlayer> listOfActions = new ArrayList<ActionOnPlayer>();

	public PlayerTargetedListOfActions(Targeting targeting) {
		this.targeting = targeting;
	}

	public PlayerTargetedListOfActions(PlayerTargetedListOfActions src) {
		targeting = src.targeting;
		for (ActionOnPlayer action : src.listOfActions) {
			listOfActions.add(new ActionOnPlayer(action));
		}
	}

	@Override
	public TargetedListOfActions cloneObject() {
		return new PlayerTargetedListOfActions(this);
	}

}
