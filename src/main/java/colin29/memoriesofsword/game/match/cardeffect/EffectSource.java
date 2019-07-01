package colin29.memoriesofsword.game.match.cardeffect;

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
}
