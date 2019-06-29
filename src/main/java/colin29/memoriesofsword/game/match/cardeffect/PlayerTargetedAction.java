package colin29.memoriesofsword.game.match.cardeffect;

public class PlayerTargetedAction extends TargetedAction {

	public enum Targeting {
		OWN_LEADER, ENEMY_LEADER;
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

}
