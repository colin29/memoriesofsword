package colin29.memoriesofsword.game.matchscreen;

import java.util.function.Predicate;

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
	 * @param targetPredicate
	 *            this is used to filter out the targets.
	 * @param effect
	 *            This is just used to provide the name / string-rep of the effect. The actual continuation should be specified in the callback
	 * @param callback
	 */
	void promptUserForFollowerSelect(EffectOnFollower effect, Predicate<PermanentOrPlayer> targetPredicate, FollowerCallback callback,
			Runnable onCancelled);

	void promptUserForPlayerSelect(EffectOnPlayer effect, Predicate<PermanentOrPlayer> targetPredicate, PlayerCallback callback,
			Runnable onCancelled);

	void promptUserForFollowerOrPlayerSelect(EffectOnFollowerOrPlayer effect, Predicate<PermanentOrPlayer> targetPredicate,
			FollowerOrPlayerCallback callback, Runnable onCancelled);
}
