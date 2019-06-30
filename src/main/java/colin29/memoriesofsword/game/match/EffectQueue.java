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

	private boolean overflowFlag = false;

	/*
	 * If size exceeds 100, queue assumes that an infinite loop is occuring and empties the array
	 */
	private static final int QUEUE_OVERFLOW_THRESHOLD = 100;

	/**
	 * Remember effects' source must be set before adding them
	 * 
	 * @param effect
	 */
	public void add(Effect effect) {
		if (overflowFlag) {
			return;
		}

		if (effects.size() >= QUEUE_OVERFLOW_THRESHOLD) {
			overflowFlag = true;
			effects.clear();
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

	public boolean isOverflowDetected() {
		return overflowFlag;
	}

	/**
	 * Use this to restart normal operation after an overflow
	 */
	public void reset() {
		overflowFlag = false;
	}

}
