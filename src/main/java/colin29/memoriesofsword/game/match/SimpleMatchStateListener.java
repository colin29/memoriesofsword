package colin29.memoriesofsword.game.match;

/**
 * Does not contain every detailed action a match (e.g a visual effects module would need a true play-by-play), but contains enough notification to
 * cover all the state changing events in a match
 * 
 * @author Colin Ta
 *
 */
public interface SimpleMatchStateListener {

	/**
	 * Called when card(s) or permanent(s) stats on the hand, or graveyard, or deck are modified. In contrast, the zoneModified methods are only
	 * called when a structural change occurs (card(s) added or removed)
	 * 
	 */
	public void cardOrPermanentStatsModified();

	public void fieldModified(int player);

	public void handModified(int player);

	public void graveyardModified(int player);

	public void deckModified(int player);

	public void playerHPModified(int player);

	public void playPointsModified(int player); // includes pp and max pp;

	public void turnChanged(); // means a turn has ended and a new one is starting

}
