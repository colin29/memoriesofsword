package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.game.match.Player;

/**
 * Marker interface: things that can be the source of an effect (ie. players and permanents)
 * 
 * When a spell is cast, the source is considered the casting player, not the card.
 *
 */
public interface EffectSource {
	/**
	 * Get a name of the source in this context
	 * 
	 * @return
	 */
	public String getSourceName();

	/**
	 * Get regular name
	 * 
	 */
	public String getName();

	public Player getOwner();
}
