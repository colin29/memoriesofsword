package colin29.memoriesofsword.game.match;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.App;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.FollowerEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerTargetedAction;
import colin29.memoriesofsword.game.match.cardeffect.FollowerTargetedAction.FollowerFollowerTargeting;
import colin29.memoriesofsword.game.matchscreen.MatchScreen;

/**
 * Just a place to run testing code that tinkers with Match, instead of putting it in MatchScreen
 * 
 * @author Colin Ta
 *
 */
public class SandBoxMatchDriver {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	MatchScreen matchScreen;

	public SandBoxMatchDriver(App app, CardRepository cardRepo) {
		MatchScreen matchScreen = new MatchScreen(app, cardRepo);
		app.setScreen(matchScreen);

		Match match = matchScreen.getMatch();

		// Sandbox Area
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();

		Player player2 = match.getPlayer(2);
		player2.playCardWithoutPayingCost((Card) player2.getHand().get(0));
		player2.playCardWithoutPayingCost((Card) player2.getHand().get(0));

		match.nextTurn();
		Player player1 = match.getPlayer1Sandboxing();
		player1.playCardWithoutPayingCost((Card) player1.getHand().get(0));
		player1.playCardWithoutPayingCost((Card) player1.getHand().get(0));

		Follower f1 = (Follower) player1.getFieldInfo().get(0);
		Follower f2 = (Follower) player1.getFieldInfo().get(1);

		f1.dealDamage(5);
		f2.dealDamage(5);
		f2.heal(3);

		List<Card> hand = player1.hand.getCards();

		Card c1 = hand.get(0);
		Card c2 = hand.get(2);

		addTestEffectToCard(c1);
		addTestEffectToCard(c2);
		//
		// player1.playCardWithoutPayingCost(c1);
		// player1.playCardWithoutPayingCost(c2);
	}

	private void addTestEffectToCard(Card card) {
		FollowerEffect myEffect = new FollowerEffect(FollowerEffect.Type.FANFARE);

		FollowerTargetedAction damageEnemyFollowers = new FollowerTargetedAction(FollowerFollowerTargeting.ENEMY_FOLLOWERS);
		myEffect.addPart(damageEnemyFollowers);

		ActionOnFollower doDamage = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		doDamage.amount = 2;
		damageEnemyFollowers.setAction(doDamage);

		card.addFollowerEffect(myEffect);
	}

}
