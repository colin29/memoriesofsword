package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.GameException;
import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.Deck;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnPlayer;
import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.Effect;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower.FollowerTargeting;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectSource;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect.TriggerType;
import colin29.memoriesofsword.game.match.cardeffect.FollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.InvalidTargetingTypeException;
import colin29.memoriesofsword.game.matchscreen.PromptableForUserSelection;
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

	final EffectQueue effectQueue = new EffectQueue();

	final SimpleMatchStateNotifier simple = new SimpleMatchStateNotifier();

	private final PromptableForUserSelection userUI;

	/**
	 * If users are not provided, a match will construct two players unassociated with any user
	 */
	public Match(PromptableForUserSelection userUI, CardRepository cardRepo) {
		player1 = new Player(this, simple, 1);
		player2 = new Player(this, simple, 2);

		this.userUI = userUI;
		this.cardRepo = cardRepo;
	}

	// public Match(User user1, Deck deck1, User user2, Deck deck2, CardRepository cardRepo) {
	// // TODO: incorporate actual user info
	// player1 = new Player(this, simple, 1);
	// player2 = new Player(this, simple, 2);
	// this.cardRepo = cardRepo;
	// }

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
		testListings.add(cardRepo.getCardById(9004));

		// Let's make a deck of 10 cards, alternating between these

		for (int n = 0; n < 10; n++) {
			int index = n % testListings.size();
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

		player.onTurnStart();

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

	void activateFanfareEffects(Follower newPermanent) {
		for (FollowerCardEffect cardEffect : newPermanent.getCardEffects()) {
			if (cardEffect.triggerType == FollowerCardEffect.TriggerType.FANFARE) {
				for (Effect effect : cardEffect.getTriggeredEffects()) {
					addToEffectQueue(effect, newPermanent);
				}
			}
		}
		processEffectQueue();
	}

	void activateFanfareEffects(Amulet newPermanent) {
		for (AmuletCardEffect cardEffect : newPermanent.getCardEffects()) {
			if (cardEffect.triggerType == AmuletCardEffect.TriggerType.FANFARE) {
				for (Effect effect : cardEffect.getTriggeredEffects()) {
					addToEffectQueue(effect, newPermanent);
				}
			}
		}
		processEffectQueue();
	}

	void checkForThisFollowerBuffedEffects(Follower follower) {
		for (FollowerCardEffect cardEffect : follower.getCardEffects()) {
			if (cardEffect.triggerType == FollowerCardEffect.TriggerType.THIS_FOLLOWER_BUFFED) {
				for (Effect effect : cardEffect.getTriggeredEffects()) {
					addToEffectQueue(effect, follower);
				}
			}
		}
		processEffectQueue();
	}

	private void addToEffectQueue(Effect effect, EffectSource source) {
		Effect copy = effect.cloneObject();
		copy.setSource(source);
		effectQueue.add(copy);
	}

	public void checkForFollowerETBEffects(Permanent<?> permanent) {
		if (permanent instanceof Follower) {
			checkForAlliedETBEffects((Follower) permanent);
		}
	}

	public void checkForClashEffects(Follower attacker, Follower defender) {

		for (FollowerCardEffect c : attacker.getCardEffects()) {
			if (c.triggerType == TriggerType.CLASH) {
				activateAllEffects(c.getTriggeredEffects(), attacker, defender);
			}
		}
		if (!attacker.isOnOwnersBattlefield() || !defender.isOnOwnersBattlefield()) {
			logger.debug("A follower died after attacker's clash effects, defender effects skipped");
			return;
		}
		for (FollowerCardEffect c : defender.getCardEffects()) {
			if (c.triggerType == TriggerType.CLASH) {
				activateAllEffects(c.getTriggeredEffects(), defender, attacker);
			}
		}
	}

	public void checkForStrikeEffects(Follower source) {
		for (FollowerCardEffect c : source.getCardEffects()) {
			if (c.triggerType == TriggerType.STRIKE) {
				activateAllEffects(c.getTriggeredEffects(), source, null);
			}
		}
	}

	/**
	 * @param follower
	 */
	private void checkForAlliedETBEffects(Follower newFollower) {
		for (Permanent<?> permanent : activePlayer.field) {
			if (permanent instanceof Follower) {
				Follower source = (Follower) permanent;
				if (source == newFollower)
					continue;
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
	 * Adds all effects to the effect queue, then calls processEffectQueue()
	 * 
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

	/**
	 * When this is set, the executing method (any method that calls processEffectQueue) should finish doing what is appropriate and exit out
	 * 
	 * You probably want to implement this trigger type by trigger type
	 * 
	 */
	private boolean asyncCallMade = false;

	void processEffectQueue() {

		if (effectQueue.isFrozen()) {
			logger.debug("Effect Queue frozen -- skipping");
			return;
		}

		outer: while (!effectQueue.isEmpty()) {
			logger.debug("Processing Effect Queue");
			List<Effect> effects = effectQueue.removeAll();

			for (Effect effect : effects) {
				executeEffect(effect);
				if (asyncCallMade) {
					logger.debug("async call made, breaking out. The callback will call processEffectQueue() to finish.");
					break outer;
				}
			}
			effectQueue.finishedExecutingEffects();
		}

	}

	private void executeEffect(Effect effect) {

		logger.debug("Executing effect: " + effect.toString() + " (from " + effect.getSource().getSourceName() + ")");

		if (effect.getSource() == null) {
			throw new MissingEffectSourceException();
		}

		EffectSource source = effect.getSource();
		final Player player;

		if (source instanceof Player) {
			player = (Player) source;
		} else if (source instanceof Permanent) {
			player = ((Permanent<?>) source).parentCard.getOwner();
		} else {
			throw new AssertionError();
		}
		Player enemyPlayer = getOtherPlayer(player);

		if (effect instanceof EffectOnFollower) {

			EffectOnFollower effectOnFollower = (EffectOnFollower) effect;
			List<Follower> targets;

			switch (effectOnFollower.targeting) {
			case ALLIED_FOLLOWERS:
				targets = player.getAllFollowers();
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
				targets = new ArrayList<Follower>();
				targets.add(effectOnFollower.THAT_FOLLOWER);
				break;
			case ETB_FOLLOWER:
				targets = new ArrayList<Follower>();
				targets.add(effectOnFollower.THAT_FOLLOWER);
				break;
			case SELECTED_FOLLOWER:
				targets = new ArrayList<Follower>();
				promptUserForFollowerTarget(effectOnFollower, true);
				break;
			default:
				throw new AssertionError("unhandled effect type");

			}

			targets.forEach((target) -> executeActionOnFollower(effectOnFollower.getAction(), target));
		}
		if (effect instanceof EffectOnPlayer) {

			EffectOnPlayer effectOnPlayer = (EffectOnPlayer) effect;

			List<Player> targets = new ArrayList<Player>();

			switch (effectOnPlayer.targeting) {
			case ENEMY_LEADER:
				targets.add(enemyPlayer);
				break;
			case OWN_LEADER:
				targets.add(player);
				break;
			default:
				throw new AssertionError("unhandled effect type");
			}
			targets.forEach((target) -> executeActionOnPlayer(effectOnPlayer.getAction(), target));
		}
		if (effect instanceof EffectOnFollowerOrPlayer) {

			EffectOnFollowerOrPlayer e = (EffectOnFollowerOrPlayer) effect;

			List<FollowerOrPlayer> targets = new ArrayList<FollowerOrPlayer>();

			switch (e.targeting) {
			case ALL_ALLIES:
				targets.add(player);
				targets.addAll(player.getAllFollowers());
				break;
			case ALL_ENEMIES:
				targets.add(enemyPlayer);
				targets.addAll(enemyPlayer.getAllFollowers());
				break;
			default:
				throw new AssertionError("unhandled effect type");

			}
			targets.forEach((target) -> executeActionOnFollowerOrPlayer(e.getAction(), target));
		}
	}

	private void promptUserForFollowerTarget(EffectOnFollower effect, boolean isPromptCancellable) {

		Consumer<Follower> callback = (Follower selectedFollower) -> {
			effect.SELECTED_FOLLOWER = selectedFollower;
			executeTargetedEffect(effect);
			processEffectQueue(); // finish processing the event queue
		};

		userUI.promptUserForFollowerSelect(callback, effect);

	}

	private void executeTargetedEffect(EffectOnFollower effect) {
		if (effect.targeting != FollowerTargeting.SELECTED_FOLLOWER) {
			logger.warn("effect isn't a targeted effect. Ignoring");
			return;
		}
		if (effect.SELECTED_FOLLOWER == null) {
			logger.warn("selected follower was null. Ignoring.");
			return;
		}
		executeActionOnFollower(effect.getAction(), effect.SELECTED_FOLLOWER);
	}

	private void executeActionOnFollowerOrPlayer(ActionOnFollowerOrPlayer action, FollowerOrPlayer target) {
		switch (action.actionType) {
		case DO_DAMAGE:
			target.dealDamage(action.amount);
			break;
		case HEAL_DEFENSE:
			target.heal(action.amount);
			break;
		default:
			throw new AssertionError("Unhandled FollowerOrPlayer action type");
		}

	}

	private void executeActionOnPlayer(ActionOnPlayer action, Player target) {
		switch (action.actionType) {
		case DO_DAMAGE:
			target.dealDamage(action.amount);
			break;
		case DRAW_CARD:
			target.drawCardsFromDeck(action.amount);
			break;
		case HEAL_DEFENSE:
			target.heal(action.amount);
			break;
		default:
			break;

		}

	}

	private void executeActionOnFollower(ActionOnFollower action, Follower follower) {

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
			follower.buffStats(action.atkBuff, action.defBuff);
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

		logger.debug("{} '{}' died and was removed from the battlefield.", follower.getLeader().getPNum(), follower.getName());

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
		onEndOfTurn();
		if (activePlayer == player1) {
			beginTurn(player2);
		} else {
			beginTurn(player1);
		}
	}

	private void onEndOfTurn() {
		activePlayer.getAllFollowers().forEach((follower) -> follower.removeSummoningSickness());
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

	public Player getNonActivePlayer() {
		return getOtherPlayer(activePlayer);
	}

	public int getNonActivePlayerNumber() {
		return getNonActivePlayer().playerNumber;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

}
