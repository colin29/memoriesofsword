package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.game.match.Player;

public interface FollowerOrPlayer {
	public int dealDamage(int amount);

	public int heal(int amount);

	public Player getOwner();
}
