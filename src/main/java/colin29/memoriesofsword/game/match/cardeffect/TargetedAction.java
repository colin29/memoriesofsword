package colin29.memoriesofsword.game.match.cardeffect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Also called a "part" of an effect
 *
 */
public abstract class TargetedAction {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public abstract TargetedAction cloneObject();
}
