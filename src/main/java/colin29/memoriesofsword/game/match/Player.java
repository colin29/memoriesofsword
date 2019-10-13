package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.match.cardeffect.FollowerOrPlayer;
import colin29.memoriesofsword.game.matchscreen.PermanentOrPlayer;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * A player in the context of a match. Atm is mostly a data class for Match: contains all the match state information that can be divided off to a
 * player
 *
 */
public class Player implements Attackable, FollowerOrPlayer, PermanentOrPlayer {

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
	List<Permanent<?>> field = new ArrayList<Permanent<?>>();

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
	 * @return whether the card was successfully played. Note that this could return false if the card had triggered effects (turning this into an
	 *         async call)
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

		if (card.isPermanent()) {

			// If card is permanent, we create the permanent and activate / start targeting for its fanfare effects

			Permanent<?> permanent;
			boolean asyncCallMade = false;

			permanent = makePermanent(card);

			if (card instanceof FollowerCard) {
				asyncCallMade = match.activateFanfareEffects((Follower) permanent, () -> {
					finishResolvingPermanentCard(card, permanent, ignoreCost);
				});
			}

			// If an async call was made, we delay actually playing the card until all targeting is finished (or user cancels the targeting -->
			// cancels playing the card)

			if (asyncCallMade) {
				return false;
			} else {
				finishResolvingPermanentCard(card, permanent, ignoreCost);
				return true;
			}

		} else if (card instanceof SpellCard) {

			SpellCard cardSpell = (SpellCard) card;

			if (!cardSpell.areAllSpellCardTargetingEffectsMet()) {
				logger.info("Spell card has not all targeting requirments met, play canceled.");
				return false;
			}

			boolean asyncCallMade = match.activateOnCastEffects(cardSpell, () -> {
				finishResolvingSpellCard(card, ignoreCost);
			});
			if (asyncCallMade) {
				return false;
			} else {
				finishResolvingSpellCard(card, ignoreCost);
				return true;
			}
		} else {
			logger.warn("Unsupported card type");
			return false;
		}
	}

	/**
	 * Should be called after fanfare effects are added to the effect queue
	 */
	private void finishResolvingPermanentCard(Card card, Permanent<?> permanent, boolean ignoreCost) {
		actuallyPlayTheCard(card, permanent, ignoreCost);
		match.processEffectQueue(); // execute fanfare effects
		match.checkForFollowerETBEffects(permanent);
	}

	/**
	 * Should be called after fanfare effects are added to the effect queue
	 */
	private void finishResolvingSpellCard(Card card, boolean ignoreCost) {
		actuallyPlayTheCard(card, null, ignoreCost);
		match.processEffectQueue();
	}

	/**
	 * This does not activate any effects
	 * 
	 * @param card
	 * @param permanent
	 *            must be provided if the card is a permanent type, otherwise should be null
	 * @param ignoreCost
	 */
	private void actuallyPlayTheCard(Card card, Permanent<?> permanent, boolean ignoreCost) {
		if (!ignoreCost) {
			playPoints -= card.getCost();
			simple.notifyPlayPointsModified(playerNumber);
		}
		hand.remove(card);

		if (card.isPermanent()) {
			field.add(permanent);
		}
		notifyForPlayToFieldAction();

		logger.debug("Card '{}' was played " + (ignoreCost ? "(ignoring cost)" : ""), card.getName());

	}

	private Permanent<?> makePermanent(Card card) {
		Permanent<?> permanent;
		if (card instanceof FollowerCard) {
			permanent = new Follower((FollowerCard) card);
		} else if (card instanceof AmuletCard) {
			permanent = new Amulet((AmuletCard) card);
			match.activateFanfareEffects((Amulet) permanent);
		} else {
			throw new InvalidArgumentException(card.getClass().getName() + " is not a recognized permanent card type");
		}
		return permanent;
	}

	/**
	 * Summons a permanent described by the cardListing. Generates the parent card(s) automatically.
	 */
	public Permanent<?> summonPermanent(CardListing cardListing) {
		Card card = match.createCard(cardListing, this);
		return summonPermanent(card);
	}

	/**
	 * Summons multiple copies described by the cardListing. Generates the parent card(s) automatically.
	 */
	public void summonPermanents(CardListing cardListing, int count) {
		for (int i = 0; i < count; i++) {
			Card card = match.createCard(cardListing, this);
			summonPermanent(card);
		}
	}

	/**
	 * Note: You cannot summon multiple permanents with the same Card, use summonPermanent(CardListing) instead.
	 * 
	 * @param parentCard
	 */
	public Permanent<?> summonPermanent(Card parentCard) {
		Permanent<?> permanent;
		try {
			permanent = makePermanent(parentCard);
		} catch (InvalidArgumentException e) {
			throw new InvalidArgumentException("Tried to summon permanent but parent card wasn't a permanent:", e);
		}

		field.add(permanent);
		simple.notifyFieldModified(playerNumber);

		logger.debug("Permanent '{}' was summoned ", parentCard.getName());

		// no fanfare effects, EtB effects like in MtG do not exist atm.
		match.checkForFollowerETBEffects(permanent);

		return permanent;
	}

	void onTurnStart() {
		getAllFollowers().forEach((follower) -> follower.resetAttacksPerTurn());
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

	/**
	 * Deals damage to the player, applying any modification effects on the follower.
	 * 
	 * @return The actual amount of damage dealt (can be overkill).
	 */
	@Override
	public int dealDamage(int damage) {

		if (damage < 0) {
			logger.warn("Tried to damage for a negative amount {}. Ignoring.", damage);
			return 0;
		}

		hp -= damage;
		logger.debug("Dealt {} damage to Player {}", damage, playerNumber);
		match.simple.notifyPlayerHPModified(playerNumber);
		return damage;
	}

	/**
	 * 
	 * @param healAmount
	 * @return The amount actually healed (doesn't count overheal)
	 */
	@Override
	public int heal(int healAmount) {

		if (healAmount < 0) {
			logger.warn("Tried to heal for a negative amount {}. Ignoring.", healAmount);
			return 0;
		}

		int oldHp = hp;
		hp = Math.min(hp + healAmount, maxHp);

		int amountHealed = hp - oldHp;
		logger.debug("Healed {} damage on Player {}", amountHealed, playerNumber);

		if (amountHealed != 0) {
			match.simple.notifyPlayerHPModified(playerNumber);
		}
		return amountHealed;
	}

	private void notifyForDrawAction() {
		simple.notifyHandModified(playerNumber);
		simple.notifyDeckModified(playerNumber);
	}

	private void notifyForPlayToFieldAction() {
		simple.notifyHandModified(playerNumber);
		simple.notifyFieldModified(playerNumber);
	}

	public boolean isOnMyField(Permanent<?> permanent) {
		return field.contains(permanent);
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns something like "p2" for player 2
	 */
	public String getPNum() {
		return "p" + playerNumber;
	}

	/**
	 * Returns something like "Player 2" for player 2
	 */
	public String getPlayerNum() {
		return "Player " + playerNumber;
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

	public List<Permanent<?>> getFieldInfo() {
		return new ArrayList<Permanent<?>>(field);
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public List<Follower> getAllFollowers() {
		List<Follower> followers = new ArrayList<Follower>();
		for (Permanent<?> p : field) {
			if (p instanceof Follower) {
				followers.add((Follower) p);
			}
		}
		return followers;
	}

	public Match getMatch() {
		return match;
	}

	@Override
	public Player getOwner() {
		return this;
	}

}
