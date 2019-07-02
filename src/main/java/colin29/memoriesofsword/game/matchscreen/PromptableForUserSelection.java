package colin29.memoriesofsword.game.matchscreen;

import java.util.function.Consumer;

import colin29.memoriesofsword.game.match.Follower;

/**
 * 
 * @author Colin Ta
 *
 */
public interface PromptableForUserSelection {

	void promptUserForFollowerSelect(Consumer<Follower> callback);
}
