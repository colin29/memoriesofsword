package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.GameException;
import colin29.memoriesofsword.game.match.Card.Type;

/**
 * A player in the context of a match. Atm is mostly a data class for Match: contains all the match state information that can be divided off to a
 * player
 *
 */
public class Player {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	int hp;
	int maxHp;

	int maxPP;
	int playPoints;

	final int playerNumber;

	ListOfCards hand = new ListOfCards();
	ListOfCards deck = new ListOfCards(); // when it may be confusing, can also refer to deck as a 'library'
	ListOfCards graveyard = new ListOfCards();

	String name = "Default-Player-Name";

	SimpleMatchStateNotifier simple;

	final Match match;

	Player(Match match, SimpleMatchStateNotifier simple, int playerNumber) {
		this.simple = simple;
		this.playerNumber = playerNumber;

		this.match = match;
	}

	/**
	 * List of permanents the player owns that are on the battlefield
	 */
	List<Permanent> field = new ArrayList<Permanent>();

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(String.format("Hp: %d/%d%n", hp, maxHp));
		str.append(String.format("PP: %d/%d%n", playPoints, maxPP));
		return str.toString();
	}

	/**
	 * Throw an exception if the request itself was improper (requesting to play a card not in hand)
	 * 
	 * @returns: Whether the card was played. False if the card cost too much, rules prevented playing, etc.
	 */
	public boolean playCard(Card card) {
		return playCard(card, false);
	}

	public boolean playCardWithoutPayingCost(Card card) {
		return playCard(card, true);
	}

	/**
	 * 
	 * @return whether the card was successfully played
	 */
	private boolean playCard(Card card, boolean ignoreCost) {
		logger.debug("Player " + playerNumber + " tries to play card '{}'", card.getName());

		if (!hand.contains(card)) {
			throw new CardNotFoundInHandException();
		}

		if (!ignoreCost && card.getCost() > playPoints) {
			logger.debug("Card cost ({}) more than current pp ({}), could not not play card.", card.getCost(), playPoints);
			return false;
		}

		// Actually play the card

		if (card.type == Type.FOLLOWER || card.type == Type.AMULET) {
			if (!ignoreCost) {
				playPoints -= card.getCost();
				simple.notifyPlayPointsModified(playerNumber);
			}
			hand.remove(card);

			Permanent permanent;

			if (card.type == Type.FOLLOWER) {
				permanent = new Follower(card);
			} else if (card.type == Type.AMULET) {
				permanent = new Amulet(card);
			} else {
				throw new GameException("Spell Type not supported yet. But this shouldn't happen anyways");
			}
			field.add(permanent);
			notifyForPlayToFieldAction();

			logger.debug("Card '{}' was played " + (ignoreCost ? "(ignoring cost)" : ""), card.getName());

			// Execute fanfare effects

			if (card.type == Type.FOLLOWER) {
				match.executeFanfareEffects((Follower) permanent);
			} else if (card.type == Type.AMULET) {
				match.executeFanfareEffects((Amulet) permanent);
			}

			return true;
		} else {
			logger.debug("Not yet supported: playing spells");
			return false;
		}
	}

	public void drawFromDeck() {
		hand.addCardToBottom(deck.drawFromTop());
		notifyForDrawAction();
	}

	public void drawCardsFromDeck(int count) {
		for (int i = 0; i < count; i++) {
			drawFromDeck();
		}
		notifyForDrawAction();
	}

	private void notifyForDrawAction() {
		simple.notifyHandModified(playerNumber);
		simple.notifyDeckModified(playerNumber);
	}

	private void notifyForPlayToFieldAction() {
		simple.notifyHandModified(playerNumber);
		simple.notifyFieldModified(playerNumber);
	}

	public boolean isOnMyField(Permanent permanent) {
		return field.contains(permanent);
	}

	public String getName() {
		return name;
	}

	public int getHp() {
		return hp;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public int getPlayPoints() {
		return playPoints;
	}

	public int getMaxPlayPoints() {
		return maxPP;
	}

	public List<CardInfo> getHand() {
		return hand.getCardInfos();
	}

	public List<CardInfo> getDeck() {
		return deck.getCardInfos();
	}

	public List<CardInfo> getGraveyard() {
		return graveyard.getCardInfos();
	}

	public List<Permanent> getFieldInfo() {
		return new ArrayList<Permanent>(field);
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public List<Follower> getAllFollowers() {
		List<Follower> followers = new ArrayList<Follower>();
		for (Permanent p : field) {
			if (p instanceof Follower) {
				followers.add((Follower) p);
			}
		}
		return followers;
	}

	public Match getMatch() {
		return match;
	}

}
