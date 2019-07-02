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
	 * @param callback
	 * @param effect
	 *            This is just used to provide the name / string-rep of the effect. The actual continuation should be specified in the callback
	 */
	void promptUserForFollowerSelect(FollowerCallback callback, EffectOnFollower effect);
}
