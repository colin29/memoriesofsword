package colin29.memoriesofsword.game.match;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.game.match.cardeffect.Effect;

public class EffectQueue {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private Queue<Effect> effects = new LinkedList<Effect>();

	/**
	 * If more than this number of effects added before completed() is called, then it considers it an infinite loop
	 */
	private static final int DETECT_INFINITE_LOOP_THRESHOLD = 100;
	/**
	 * Tracks the total number of effects added during this chain
	 */
	private int effectsAddedCount = 0;

	/**
	 * Remember effects' source must be set before adding them
	 * 
	 * @param effect
	 */
	public void add(Effect effect) {
		effectsAddedCount += 1;

		if (effectsAddedCount > DETECT_INFINITE_LOOP_THRESHOLD) {
			effects.clear();
			logger.info("Infinite loop detected, queue emptied and add ignored");
			return;
		}

		if (effect.getSource() == null) {
			logger.warn("Effect is missing source, add ignored.");
			return;
		}
		effects.add(effect);
	}

	public Effect remove() {
		return effects.remove();
	}

	public List<Effect> removeAll() {
		List<Effect> allItems = new LinkedList<Effect>(effects);
		effects.clear();
		return allItems;
	}

	public boolean isEmpty() {
		return effects.isEmpty();
	}

	/**
	 * Use this to mark that all the effects have been executed.
	 * 
	 * If this queue is empty, then the queue will know everything is done. This is useful because if the cycle of adds never finishes, it will
	 * consider it an infinite loop
	 */
	public void finishedExecutingEffects() {
		if (effects.isEmpty()) {
			resetInfiniteLoopCounter();
		}
	}

	private void resetInfiniteLoopCounter() {
		effectsAddedCount = 0;
	}

	public int size() {
		return effects.size();
	}
}
