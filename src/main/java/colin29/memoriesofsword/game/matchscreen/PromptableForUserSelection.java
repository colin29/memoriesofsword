package colin29.memoriesofsword.game.matchscreen;

import colin29.memoriesofsword.game.match.Match.FollowerCallback;
import colin29.memoriesofsword.game.match.Match.FollowerOrPlayerCallback;
import colin29.memoriesofsword.game.match.Match.PlayerCallback;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer;

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

	void promptUserForPlayerSelect(EffectOnPlayer effect, PlayerCallback callback, Runnable onCancelled);

	void promptUserForFollowerOrPlayerSelect(EffectOnFollowerOrPlayer effect, FollowerOrPlayerCallback callback, Runnable onCancelled);
}
