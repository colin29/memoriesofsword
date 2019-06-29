package colin29.memoriesofsword.game.match.cardeffect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TargetedListOfActions {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public abstract TargetedListOfActions cloneObject();
}
