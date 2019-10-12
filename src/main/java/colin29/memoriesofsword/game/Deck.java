package colin29.memoriesofsword.game;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stores a list of ids referring to Card Listings, along with the number of copies of those cards.
 * 
 * @author Colin Ta
 *
 */
public class Deck {
	private final Map<Integer, Integer> cardCounts = new TreeMap<Integer, Integer>();

	public void setCardCount(int cardId, int count) {
		cardCounts.put(cardId, count);
	}

	public void clear() {
		cardCounts.clear();
	}

	public Map<Integer, Integer> getCardCounts() {
		return Collections.unmodifiableMap(cardCounts);
	}
}
