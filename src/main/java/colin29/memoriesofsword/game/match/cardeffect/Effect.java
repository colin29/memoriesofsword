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

	private EffectSource source;

	/*
	 * Produces the a copy, with the same underlying type as the original object
	 */
	public abstract Effect cloneObject();

	protected final static String noStringRepText = "{No string representation implemented for this effect type}";

	/**
	 * Returns a text representation that IS used to generated card text
	 */
	@Override
	public abstract String toString();

	public EffectSource getSource() {
		return source;
	}

	/**
	 * This setter must be called before adding the effect to the effect queue
	 * 
	 * @param source
	 * @param owner
	 */
	public void setSource(EffectSource source) {
		if (source == null) {
			logger.warn("Can't set source to null");
		}
		this.source = source;
	}

	/**
	 * Returns true if the effect is of type such that it uses user targeting
	 */
	public abstract boolean isUsingUserTargeting();

}
