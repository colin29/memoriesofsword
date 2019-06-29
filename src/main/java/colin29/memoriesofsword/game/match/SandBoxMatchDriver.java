package colin29.memoriesofsword.game.match;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.App;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.AmuletEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerTargetedAction;
import colin29.memoriesofsword.game.match.cardeffect.FollowerTargetedAction.FollowerTargeting;
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
		Card c2 = hand.get(1);
		Card c3 = hand.get(2);

		addTestEffectToCard(c1);
		addFanfareBuffEffectToAmulet(c2);
		addTestEffectToCard(c3);
		//
		// player1.playCardWithoutPayingCost(c1);
		// player1.playCardWithoutPayingCost(c2);
	}

	private void addTestEffectToCard(Card card) {
		FollowerEffect myEffect = new FollowerEffect(FollowerEffect.TriggerType.FANFARE);

		FollowerTargetedAction damageEnemyFollowers = new FollowerTargetedAction(FollowerTargeting.ENEMY_FOLLOWERS);
		myEffect.addTriggeredAction(damageEnemyFollowers);

		ActionOnFollower doDamage = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		doDamage.amount = 2;
		damageEnemyFollowers.setAction(doDamage);

		card.addFollowerEffect(myEffect);
	}

	private void addFanfareBuffEffectToAmulet(Card card) {
		AmuletEffect myEffect = new AmuletEffect(AmuletEffect.TriggerType.FANFARE);

		FollowerTargetedAction damageEnemyFollowers = new FollowerTargetedAction(FollowerTargeting.ALLIED_FOLLOWERS);
		myEffect.addTriggeredAction(damageEnemyFollowers);

		ActionOnFollower buff = new ActionOnFollower(ActionOnFollower.ActionType.BUFF);
		buff.atkBuff = 2;
		buff.defBuff = 1;
		damageEnemyFollowers.setAction(buff);

		card.addAmuletEffect(myEffect);
	}

}
