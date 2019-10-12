package game.match;

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

	@BeforeEach
	void setupMatch() {

		CardListing c1 = CardListing.makeFollowerTempCardListing("Goblin", 1, 1, 2);
		cardRepo.addCard(c1);

		// make deck
		var deck = new Deck();
		deck.setCardCount(c1.getId(), 10);

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

		expectedHand.remove(0);
		assert (hand.equals(expectedHand));

		// test that a follower was added to the battlefield
		assert (player.getAllFollowers().size() == 1);
		assert (player.getAllFollowers().get(0).getParentCard() == card);
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
