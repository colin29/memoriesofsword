package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.GameException;

/**
 * A generic collection of cards. Can be a player's library, graveyard, hand, etc. Is ordered.
 * 
 * This class enforces no duplicates, if caller tries to add a duplicate, that is a logic flaw. This class will log a warning and ignore the add.
 * 
 * @author Colin Ta
 *
 */
public class ListOfCards {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	Random randomGenerator = new Random();

	/**
	 * Underlying collection of cards
	 */
	private ArrayList<Card> cards = new ArrayList<Card>();

	void addCardToTop(Card card) {
		if (cards.contains(card)) {
			GameException e = new GameException("Tried to add duplicate card '" + card.getName() + "' to deck, this should not happen.");
			logger.warn(e.getMsgAndStackTrace());
			return;
		}
		cards.add(0, card);
	}

	void addCardToBottom(Card card) {
		if (cards.contains(card)) {
			GameException e = new GameException("Tried to add duplicate card '" + card.getName() + "' to deck, this should not happen.");
			logger.warn(e.getMsgAndStackTrace());
			return;
		}
		cards.add(card);
	}

	void addCardToNthPositionFromTop(Card card, int position) {
		if (cards.contains(card)) {
			GameException e = new GameException("Tried to add duplicate card '" + card.getName() + "' to deck, this should not happen.");
			logger.warn(e.getMsgAndStackTrace());
			return;
		}
		cards.add(position, card);
	}

	void addCardToRandomPosition(Card card) {
		if (cards.contains(card)) {
			GameException e = new GameException("Tried to add duplicate card '" + card.getName() + "' to deck, this should not happen.");
			logger.warn(e.getMsgAndStackTrace());
			return;
		}
		int position = randomGenerator.nextInt(cards.size() + 1); // the highest possible value is cards.size(), which means insert after the last
																	// card
		cards.add(position, card);
	}

	Card drawFromTop() {
		if (cards.isEmpty()) {
			throw new ListOfCardsEmptyException();
		}
		return cards.remove(0);
	}

	Card drawFromBottom() {
		if (cards.isEmpty()) {
			throw new ListOfCardsEmptyException();
		}
		return cards.remove(cards.size() - 1);
	}

	/**
	 * @return The cards contained, in order. The cards themselves can be modified, but the list cannot.
	 */
	List<Card> getCards() {
		return Collections.unmodifiableList(cards);
	}

	/**
	 * Return a list of the cards in read-only format.
	 * 
	 * @return
	 */
	List<CardInfo> getCardInfos() {
		return new ArrayList<CardInfo>(cards);
	}

	public boolean contains(Card card) {
		return cards.contains(card);
	}

	void remove(Card card) {
		if (!cards.contains(card)) {
			throw new CardNotFoundInListException();
		}
		cards.remove(card);
	}

	class CardNotFoundInListException extends GameException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	}

}
