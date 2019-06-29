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

	protected final static String noStringRepText = "{No string representation implemented for this action type}";

	/**
	 * Returns a text representation that IS used to generated card text
	 */
	@Override
	public abstract String toString();

}
