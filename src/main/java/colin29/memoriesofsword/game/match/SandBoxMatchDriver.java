package colin29.memoriesofsword.game.match;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.App;
import colin29.memoriesofsword.game.CardRepository;
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

		Player player2 = match.getPlayer(2);
		// player2.playCardWithoutPayingCost((Card) player2.getHand().get(1));
		// player2.playCardWithoutPayingCost((Card) player2.getHand().get(0));

		match.nextTurn();
		Player player1 = match.getPlayer1Sandboxing();
		// player1.playCardWithoutPayingCost((Card) player1.getHand().get(0));
		// player1.playCardWithoutPayingCost((Card) player1.getHand().get(0));
		//
		// player1.playCardWithoutPayingCost(player1.hand.getCards().get(1));
		// player1.playCardWithoutPayingCost(c2);

		match.nextTurn();
		match.nextTurn();
	}

}
