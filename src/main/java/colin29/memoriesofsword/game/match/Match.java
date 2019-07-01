package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.GameException;
import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.Deck;
import colin29.memoriesofsword.game.User;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.Effect;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectSource;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect.TriggerType;
import colin29.memoriesofsword.game.match.cardeffect.InvalidTargetingTypeException;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * Contains all the state for a match <br>
 * Is responsible for offering functionality to advance a match and enforce rules.
 * 
 * @author Colin Ta
 *
 */
public class Match {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

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

	private EffectQueue effectQueue = new EffectQueue();

	final SimpleMatchStateNotifier simple = new SimpleMatchStateNotifier();

	/**
	 * If users are not provided, a match will construct two players unassociated with any user
	 */
	public Match(CardRepository cardRepo) {
		player1 = new Player(this, simple, 1);
		player2 = new Player(this, simple, 2);

		this.cardRepo = cardRepo;
	}

	public Match(User user1, Deck deck1, User user2, Deck deck2, CardRepository cardRepo) {
		// TODO: incorporate actual user info
		player1 = new Player(this, simple, 1);
		player2 = new Player(this, simple, 2);
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
		return new Card(listing, owner, this);
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

	void activateFanfareEffects(Follower follower) {
		for (FollowerCardEffect cardEffect : follower.getCardEffects()) {
			if (cardEffect.triggerType == FollowerCardEffect.TriggerType.FANFARE) {
				for (Effect effect : cardEffect.getTriggeredEffects()) {
					Effect copy = effect.cloneObject();
					copy.setSource(follower);
					effectQueue.add(copy);
				}
			}
		}
		processEffectQueue();
	}

	void activateFanfareEffects(Amulet amulet) {
		for (AmuletCardEffect cardEffect : amulet.getCardEffects()) {
			if (cardEffect.triggerType == AmuletCardEffect.TriggerType.FANFARE) {
				for (Effect effect : cardEffect.getTriggeredEffects()) {
					Effect copy = effect.cloneObject();
					copy.setSource(amulet);
					effectQueue.add(copy);
				}
			}
		}
		processEffectQueue();
	}

	public void checkForETBEffects(Permanent<?> permanent) {
		if (permanent instanceof Follower) {
			checkForAlliedETBEffects((Follower) permanent);
		} else if (permanent instanceof Amulet) {
			logger.debug("No Amulet_ETB trigger type yet");
			return;
		}
	}

	/**
	 * @param follower
	 */
	private void checkForAlliedETBEffects(Follower newFollower) {
		for (Permanent<?> permanent : activePlayer.field) {

			List<Effect> triggeredEffects;

			if (permanent instanceof Follower) {
				Follower source = (Follower) permanent;
				for (FollowerCardEffect c : source.getCardEffects()) {
					if (c.triggerType == TriggerType.ETB_ALLIED_FOLLOWER) {
						activateAllEffects(c.getTriggeredEffects(), source, newFollower);
					}
				}
			} else if (permanent instanceof Amulet) {
				Amulet source = (Amulet) permanent;

				for (AmuletCardEffect c : source.getCardEffects()) {
					if (c.triggerType == AmuletCardEffect.TriggerType.ETB_ALLIED_FOLLOWER) {
						activateAllEffects(c.getTriggeredEffects(), source, newFollower);
					}
				}
			} else {
				throw new AssertionError();
			}

		}
	}

	/**
	 * 
	 * @param effects
	 * @param source
	 * @param THAT_FOLLOWER
	 *            Some triggerTypes (ETB_ALLIED_FOLLOWER, inherently provide a follower, which is passed to effects that use that targeting). Can't be
	 *            null
	 */
	private void activateAllEffects(List<Effect> effects, EffectSource source, Follower THAT_FOLLOWER) {
		for (Effect effect : effects) {
			Effect copy = effect.cloneObject();
			copy.setSource(source);

			if (copy instanceof EffectOnFollower) {
				((EffectOnFollower) copy).THAT_FOLLOWER = THAT_FOLLOWER;
			}
			effectQueue.add(copy);
		}
		processEffectQueue();
	}

	private void processEffectQueue() {

		while (!effectQueue.isEmpty()) {

			logger.debug("Processing effect Queue");

			List<Effect> effects = effectQueue.removeAll();

			for (Effect effect : effects) {
				executeEffect(effect);
			}
		}

	}

	private void executeEffect(Effect effect) {

		logger.debug("Executing effect: " + effect.toString());

		if (effect.getSource() == null) {
			throw new MissingEffectSourceException();
		}

		EffectSource source = effect.getSource();
		Player owner;

		if (source instanceof Player) {
			owner = (Player) source;
		} else if (source instanceof Permanent) {
			owner = ((Permanent<?>) source).parentCard.getOwner();
		} else {
			throw new AssertionError();
		}

		if (effect instanceof EffectOnFollower) {

			Player enemyPlayer = getOtherPlayer(owner);

			EffectOnFollower effectOnFollower = (EffectOnFollower) effect;
			List<Follower> targets;

			switch (effectOnFollower.targeting) {
			case ALLIED_FOLLOWERS:
				targets = owner.getAllFollowers();
				break;
			case ENEMY_FOLLOWERS:
				targets = enemyPlayer.getAllFollowers();
				break;
			case OTHER_ALLIED_FOLLOWERS:
				throw new UnsupportedOperationException();
			case OTHER_ENEMY_FOLLOWERS:
				throw new UnsupportedOperationException();
			case THIS_FOLLOWER:
				targets = new ArrayList<Follower>();
				if (source instanceof Follower) {
					targets.add((Follower) source);
				} else {
					throw new InvalidTargetingTypeException("Source must be a Follower to use THIS_FOLLOWER");
				}
				break;
			case THE_ENEMY_FOLLOWER:
				throw new UnsupportedOperationException();
			case ETB_FOLLOWER:
				targets = new ArrayList<Follower>();
				targets.add(effectOnFollower.THAT_FOLLOWER);
				break;
			default:
				throw new AssertionError("unhandled effect type");
			}

			logger.debug("Found {} targets.", targets.size());

			for (Follower target : targets) {
				executeActionOnFollower(effectOnFollower.getAction(), target);
			}
		}

	}

	public void executeActionOnFollower(ActionOnFollower action, Follower follower) {

		if (!follower.isOnOwnersBattlefield()) {
			logger.debug("Target follower was not on owner's battlefield, so effect fizzled");
			return;
		}

		switch (action.actionType) {
		case DO_DAMAGE:
			follower.dealDamage(action.amount);
			break;
		case HEAL_DEFENSE:
			follower.heal(action.amount);
			break;
		case BUFF:
			follower.buffAtk(action.atkBuff);
			follower.buffDef(action.defBuff);
			break;
		case GIVE_APPLIED_EFFECT:
			logger.info("Executing giving applied effect to a follower isn't supported yet.");
			break;
		default:
			break;
		}

	}

	/**
	 * Handles the removal of a follower and moving the parent card to the graveyard
	 * 
	 * @param follower
	 */
	void handleDeath(Follower follower) {

		Card card = follower.getParentCard();
		Player owner = card.getOwner();
		removeFromField(follower);
		owner.graveyard.addCardToTop(card);

		logger.debug("Follower '{}' died and was removed from the battlefield.", follower.getName());

		simple.notifyFieldModified(owner.playerNumber);
		simple.notifyGraveyardModified(owner.playerNumber);
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

	public Player getOtherPlayer(int playerNumber) {
		return getOtherPlayer(getPlayer(playerNumber));
	}

	public Player getOtherPlayer(Player player) {
		if (player == player1) {
			return player2;
		} else if (player == player2) {
			return player1;
		} else {
			throw new InvalidArgumentException("Player given not player1 or player2");
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
