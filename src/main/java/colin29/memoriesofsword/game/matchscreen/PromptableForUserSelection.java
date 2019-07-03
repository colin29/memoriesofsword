package colin29.memoriesofsword.game.matchscreen;

import colin29.memoriesofsword.game.match.Match.FollowerCallback;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;

/**
 * 
 * @author Colin Ta
 *
 */
public interface PromptableForUserSelection {

	/**
	 * @param effect
	 *            This is just used to provide the name / string-rep of the effect. The actual continuation should be specified in the callback
	 * @param callback
	 */
	void promptUserForFollowerSelect(EffectOnFollower effect, FollowerCallback callback, Runnable onCancelled);
}
