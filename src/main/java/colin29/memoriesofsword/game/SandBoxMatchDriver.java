package colin29.memoriesofsword.game;

import colin29.memoriesofsword.App;
import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.Follower;
import colin29.memoriesofsword.game.match.Match;
import colin29.memoriesofsword.game.match.Player;
import colin29.memoriesofsword.game.matchscreen.MatchScreen;

/**
 * Just a place to run testing code that tinkers with Match, instead of putting it in MatchScreen
 * 
 * @author Colin Ta
 *
 */
public class SandBoxMatchDriver {

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
		match.nextTurn();
		Player player1 = match.getPlayer1Sandboxing();
		player1.playCardWithoutPayingCost((Card) player1.getHand().get(0));
		player1.playCardWithoutPayingCost((Card) player1.getHand().get(0));

		Follower f1 = (Follower) player1.getFieldInfo().get(0);
		Follower f2 = (Follower) player1.getFieldInfo().get(1);

		f1.dealDamage(5);
		f2.dealDamage(5);
		f2.heal(3);

	}

}
