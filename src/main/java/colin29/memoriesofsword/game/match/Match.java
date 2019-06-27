package colin29.memoriesofsword.game.match;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.GameException;
import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.Deck;
import colin29.memoriesofsword.game.User;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * Contains all the state for a match <br>
 * Is responsible for offering functionality to advance a match and enforce rules.
 * 
 * @author Colin Ta
 *
 */
public class Match {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private int turnNumber;

	private final Player player1;
	private final Player player2;

	private Deck deck1; // the original decks brought into the match. These are probably a copy and should not be modified anyways.
	private Deck deck2;

	private final CardRepository cardRepo;

	/**
	 * Represents the player's whose turn it is. Is null before the match starts and defined afterwards.
	 */
	private Player activePlayer;

	final SimpleMatchStateNotifier simple = new SimpleMatchStateNotifier();

	/**
	 * If users are not provided, a match will construct two players unassociated with any user
	 */
	public Match(CardRepository cardRepo) {
		player1 = new Player(simple, 1);
		player2 = new Player(simple, 2);

		this.cardRepo = cardRepo;
	}

	public Match(User user1, Deck deck1, User user2, Deck deck2, CardRepository cardRepo) {
		// TODO: incorporate actual user info
		player1 = new Player(simple, 1);
		player2 = new Player(simple, 2);
		this.cardRepo = cardRepo;
	}

	/**
	 * Two players can use the same deck without issue, they are merely read from at the start of the match.
	 * 
	 * After beginMatch() is called, setting the decks has no effect.
	 */
	public void setDecks(Deck deck1, Deck deck2) {
		this.deck1 = deck1;
		this.deck2 = deck2;
	}

	private boolean useTestDecks;

	/**
	 * Instead of needing to provide decks, use the built-in test decks.
	 */
	public void useTestDecks() {
		useTestDecks = true;
	}

	/**
	 * Both players should be set before beginMatch is called
	 */
	public void beginMatch() {
		if (player1 == null || player2 == null) {
			throw new GameException("Players must be defined before starting a match");
		}
		if (useTestDecks) {
			addTestCardsToDeck(player1);
			addTestCardsToDeck(player2);
		} else {
			if (deck1 == null || deck2 == null) {
				throw new GameException("Decks must be specified before starting a match");
			}
		}

		// TODO: Actually read from decks to generate players' initial libraries

		player1.drawCardsFromDeck(3);
		player2.drawCardsFromDeck(4);

		turnNumber = 0;

		player1.maxPP = 0;
		player1.maxHp = 20;
		player1.hp = player1.maxHp;

		player2.maxPP = 0;
		player2.maxHp = 20;
		player2.hp = player2.maxHp;

		beginTurn(player1);
	}

	private void addTestCardsToDeck(Player player) {
		ArrayList<CardListing> testListings = new ArrayList<CardListing>();

		testListings.add(cardRepo.getCardById(9000));
		testListings.add(cardRepo.getCardById(9001));
		testListings.add(cardRepo.getCardById(9002));
		testListings.add(cardRepo.getCardById(9003));

		// Let's make a deck of 10 cards, alternating between these

		for (int n = 0; n < 10; n++) {
			int index = n % 4;
			CardListing listing = testListings.get(index);
			player.deck.addCardToBottom((createCard(listing, player)));
		}
	}

	private Card createCard(CardListing listing, Player owner) {
		return new Card(listing.getName(), listing.getType(), listing.getCost(), listing.getAtk(), listing.getDef(), owner, this);
	}

	public void beginTurn(Player player) {
		if (player == player1) {
			turnNumber += 1;
		}
		activePlayer = player;

		logger.debug("Started Turn " + turnNumber + ", player " + getActivePlayerNumber());

		if (activePlayer.maxPP < 10)
			activePlayer.maxPP += 1;
		activePlayer.playPoints = activePlayer.maxPP;

		simple.notifyTurnedChanged();
		simple.notifyPlayPointsModified(player.playerNumber);

		// Player draws

		// lets disable deck out problems from new turn draw for now.
		try {
			activePlayer.drawFromDeck();
		} catch (ListOfCardsEmptyException e) {
			// do nothing
		}

	}

	/**
	 * Handles the removal of a follower and moving the parent card to the graveyard
	 * 
	 * @param follower
	 */
	void handleDeath(Follower follower) {
		executeLastWordEffects(follower);

		Card card = follower.getParentCard();
		Player owner = card.getOwner();
		removeFromField(follower);
		owner.graveyard.addCardToTop(card);

		logger.debug("Follower '{}' died and was removed from the battlefield.", follower.getName());

		simple.notifyFieldModified(owner.playerNumber);
		simple.notifyGraveyardModified(owner.playerNumber);
	}

	private void executeLastWordEffects(Follower follower) {
		// TODO: stub
	}

	/**
	 * Removes a permanent from field, effectively discarding it for good
	 * 
	 * @param permanent
	 */
	private void removeFromField(Permanent permanent) {
		Player owner = permanent.getParentCard().getOwner();
		if (!owner.field.contains(permanent)) {
			logger.warn("Tried removing a permanent from a player's field, permanent was not found. Ignoring.");
		}
		owner.field.remove(permanent);
	}

	public void nextTurn() {
		if (activePlayer == player1) {
			beginTurn(player2);
		} else {
			beginTurn(player1);
		}
	}

	/**
	 * Only for quick sandboxing purposes. WILL be removed in the future.
	 * 
	 * @return
	 */
	public Player getPlayer1Sandboxing() {
		return player1;
	}

	private boolean isItPlayersTurn(Player player) {
		return activePlayer == player;
	}

	public void traceCurrentState() {
		logger.trace("Match state: \n" + getStateAsString());
	}

	private String getStateAsString() {
		StringBuilder str = new StringBuilder();

		str.append("Turn number: " + turnNumber + "\n");
		str.append("Current Player: " + getActivePlayerNumber() + "\n");

		str.append("Player 1: \n");
		str.append(player1.toString());
		str.append("Player 2: \n");
		str.append(player2.toString());
		return str.toString();
	}

	public void addSimpleStateListener(SimpleMatchStateListener listener) {
		simple.addSimpleStateListener(listener);
	}

	public String getPlayer1Name() {
		return player1.name;
	}

	/**
	 * Throw exceptions if player number is outside of normal bounds
	 * 
	 * @param playerNumber
	 * @return
	 */
	public Player getPlayer(int playerNumber) {
		switch (playerNumber) {
		case 1:
			return player1;
		case 2:
			return player2;
		default:
			throw new InvalidArgumentException("Player number (" + playerNumber + ") is not valid");
		}
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public int getActivePlayerNumber() {
		return isItPlayersTurn(player1) ? 1 : 2;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

}
