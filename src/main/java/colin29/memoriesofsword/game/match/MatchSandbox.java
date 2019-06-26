package colin29.memoriesofsword.game.match;

import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.util.template.AppWithResources;
import colin29.memoriesofsword.util.template.BaseScreen;

/**
 * Creates a new match and does random things with it
 * 
 * @author Colin Ta
 *
 */
public class MatchSandbox extends BaseScreen {

	public MatchSandbox(AppWithResources game, CardRepository cardRepo) {
		super(game);
		runTestMatch(cardRepo);
	}

	private void runTestMatch(CardRepository cardRepo) {
		Match match = new Match(cardRepo);
		match.useTestDecks();

		match.beginMatch();
		match.nextTurn();
		match.nextTurn();
		match.nextTurn();
		// match.playCard(null);

		match.traceCurrentState();
	}

	@Override
	public void render(float delta) {
	}

}
