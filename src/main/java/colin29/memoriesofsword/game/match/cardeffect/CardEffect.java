package colin29.memoriesofsword.game.match.cardeffect;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Card effect is something which is held by Card listings, Cards, or permanents.
 * 
 * A common type of CardEffect is a Triggered effect like a Fanfare or Clash
 * 
 * A Triggered effect differs from an Effect in that a triggered effect includes a condition. When that condition happens, the match adds the a copy
 * of the contained Effect to the stack
 * 
 * In a real game, all effects originate from CardEffects.
 * 
 * @author Colin Ta
 *
 */
public abstract class CardEffect {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected List<Effect> triggeredEffects = new ArrayList<Effect>();

	@Override
	public abstract String toString();

	/**
	 * Sub-classes may override this method to check and reject non-supported trigger + effect combinations, etc.
	 * 
	 * @param effect
	 */
	public void addTriggeredEffect(Effect effect) {
		if (effect == null) {
			logger.warn("Tried to add null targeted Effect.");
			return;
		}
		triggeredEffects.add(effect);
	}

	public List<Effect> getTriggeredEffects() {
		return triggeredEffects;
	}
}
