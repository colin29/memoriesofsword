package game.match;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.Deck;
import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.CardInfo;
import colin29.memoriesofsword.game.match.Match;
import colin29.memoriesofsword.game.match.Match.FollowerCallback;
import colin29.memoriesofsword.game.match.Match.FollowerOrPlayerCallback;
import colin29.memoriesofsword.game.match.Match.PlayerCallback;
import colin29.memoriesofsword.game.match.Permanent;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer;
import colin29.memoriesofsword.game.matchscreen.PermanentOrPlayer;
import colin29.memoriesofsword.game.matchscreen.PromptableForUserSelection;

class MatchTest {

	MockPromptable mockPromptable = new MockPromptable();
	CardRepository cardRepo = new CardRepository();
	Deck deck;

	Match match;

	private static enum CardId {
		GOBLIN(1), BEAR(2);
		private final int id;

		CardId(int value) {
			this.id = value;
		}
	}

	private class Listings {
		CardListing GOBLIN, BEAR;
	}

	private final Listings listings = new Listings();

	@BeforeEach
	void setupMatch() {

		listings.GOBLIN = CardListing.makeFollowerCardListing("Goblin", 1, 1, 2, CardId.GOBLIN.id);
		listings.BEAR = CardListing.makeFollowerCardListing("Bear", 2, 2, 2, CardId.BEAR.id);

		cardRepo.addCard(listings.GOBLIN, listings.BEAR);

		// Set player's deck to a dummy deck of 30 goblins
		var deck = new Deck();
		deck.setCardCount(listings.GOBLIN.getId(), 30);

		match = new Match(mockPromptable, cardRepo);
		match.setDecks(deck, deck);
		match.beginMatch();
		// it is now player 1's turn
	}

	// Test that you can play a simple follower to your hand
	@Test
	void testPlaySimpleFollower() {
		var player = match.getActivePlayer();
		var hand = player.getHand();
		var expectedHand = new LinkedList<CardInfo>(player.getHand());

		// play a single card, which we know is a goblin follower
		Card card = (Card) hand.get(0);
		match.getPlayer(1).playCardWithoutPayingCost(card);

		// test that the card was removed from hand
		expectedHand.remove(card);
		assertIterableEquals(expectedHand, hand);

		// test that a follower was added to the battlefield
		assertEquals(1, player.getAllFollowers().size());
		assertEquals(card, player.getAllFollowers().get(0).getParentCard());
	}

	@Test
	void testSummonPermanent() {
		var player = match.getActivePlayer();
		var cardListing = listings.GOBLIN;

		assertEquals(0, player.getAllFollowers().size());

		Permanent<?> returnedValue = player.summonPermanent(cardListing);
		assertEquals(1, player.getAllFollowers().size());
		var permanent = player.getFieldInfo().get(0);
		assertEquals(permanent, returnedValue);
		assertEquals(cardListing, permanent.getParentCard().getCardListing());

	}

	@Test
	void testFollowerCanAttack() {
		var player = match.getActivePlayer();
		var opp = match.getNonActivePlayer();

		player.summonPermanent(listings.GOBLIN);
		opp.summonPermanent(listings.GOBLIN);

		match.nextTurn(); // need to wait a turn for summoning sickness to wear off
		match.nextTurn();

		var follower = player.getAllFollowers().get(0);
		var enemyFollower = opp.getAllFollowers().get(0);

		follower.attack(enemyFollower);

		assertEquals(1, follower.getDef());
		assertEquals(1, enemyFollower.getDef());
	}

	@Test
	void testFollowerDiesFromLethalDamage() {
		var player = match.getActivePlayer();
		player.summonPermanent(listings.GOBLIN);

		var follower = player.getAllFollowers().get(0);

		assertEquals(2, follower.getDef());
		follower.dealDamage(1);
		assertEquals(1, follower.getDef());
		follower.dealDamage(1);
		assertTrue(player.getAllFollowers().isEmpty());
	}

	private static class MockPromptable implements PromptableForUserSelection {
		@Override
		public void promptUserForFollowerSelect(EffectOnFollower effect, Predicate<PermanentOrPlayer> targetPredicate, FollowerCallback callback,
				Runnable onCancelled) {
		}

		@Override
		public void promptUserForPlayerSelect(EffectOnPlayer effect, Predicate<PermanentOrPlayer> targetPredicate, PlayerCallback callback,
				Runnable onCancelled) {
		}

		@Override
		public void promptUserForFollowerOrPlayerSelect(EffectOnFollowerOrPlayer effect, Predicate<PermanentOrPlayer> targetPredicate,
				FollowerOrPlayerCallback callback, Runnable onCancelled) {
		}
	}

}
