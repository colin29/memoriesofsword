package colin29.memoriesofsword.game;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all the card listings the game knows about
 * 
 * When card listings are loaded from disk (or anywhere), they are added to the game's CardRepository
 * 
 * All card listings in the Card Repository must have distinct ids. Ids never being reused allows us to rename cards while not effecting existing
 * decks
 * 
 * One can get cards by id?
 * 
 * @author Colin Ta
 *
 */
public class CardRepository {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	UniqueIDCardCollection cardsById = new UniqueIDCardCollection();

	public void addCard(CardListing cardListing) {
		try {
			cardsById.addCard(cardListing);
		} catch (DuplicateCardListingIDException e) {
			logger.warn("Tried to add card with duplicate id, add was ignored.");
		}
	}

	public void addCard(CardListing... listings) {
		for (CardListing cardListing : listings) {
			cardsById.addCard(cardListing);
		}
	}

	public CardListing getCardById(int id) {
		CardListing result = cardsById.getCardByID(id);
		if (result == null) {
			throw new CardListingNotFoundException();
		}
		return result;
	}

	/**
	 * <pre>
	 * Holds card listings 
	 * Enforces:
	 * -no duplicate card listings
	 * -no duplicate ids (technically this condition includes the previous)
	 * </pre>
	 */
	private static class UniqueIDCardCollection {

		Logger logger = LoggerFactory.getLogger(this.getClass());

		/**
		 * Do not modify
		 */
		private Map<Integer, CardListing> internalCardsById = new HashMap<Integer, CardListing>();

		public void addCard(CardListing cardListing) {

			final int id = cardListing.getId();
			if (internalCardsById.containsKey(id)) {
				if (internalCardsById.get(id) == cardListing) {
					logger.warn("Tried to add card listing to card repo which was already included");
				}
				throw new DuplicateCardListingIDException();
			}

			internalCardsById.put(id, cardListing);

			// if the id specified already exists, throw exception
			// Additionally if it's the same card listing object, log a warning
		}

		public CardListing getCardByID(int id) {
			return internalCardsById.get(id);
		}
	}
}
