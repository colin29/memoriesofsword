package colin29.memoriesofsword.game.match.cardeffect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Effects are a complete description of some effect which can be executed.
 * 
 * Effects are what are added to the Effect queue and executed.
 *
 */
public abstract class Effect {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public abstract Effect cloneObject();

	protected final static String noStringRepText = "{No string representation implemented for this action type}";

	/**
	 * Returns a text representation that IS used to generated card text
	 */
	@Override
	public abstract String toString();

}
