package colin29.memoriesofsword.game.match.cardeffect;

import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.Player;

/**
 * Marker interface: things that can be the source of an effect (ie. cards and permanents)
 * 
 * When a spell is cast, the source is considered the original spell card
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

	public Card getSourceCard();
}
