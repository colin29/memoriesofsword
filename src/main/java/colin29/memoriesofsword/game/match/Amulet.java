package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;

import colin29.memoriesofsword.game.match.cardeffect.AmuletEffect;

public class Amulet extends Permanent {

	/**
	 * These effects were added to this follower from the parent card at cast time
	 */
	private final ArrayList<AmuletEffect> origEffects = new ArrayList<AmuletEffect>();
	/**
	 * Effects added later
	 */
	private final ArrayList<AmuletEffect> appliedEffects = new ArrayList<AmuletEffect>();

	Amulet(Card parentCard) {
		super(parentCard);

		copyEffectsFromParentCard();
	}

	private void copyEffectsFromParentCard() {
		for (AmuletEffect effect : parentCard.getAmuletEffects()) {
			origEffects.add(new AmuletEffect(effect));
		}
	}

	// Returns a list of all effects on this follower (in order)
	public List<AmuletEffect> getEffects() {
		List<AmuletEffect> effects = new ArrayList<AmuletEffect>();
		effects.addAll(origEffects);
		effects.addAll(appliedEffects);
		return effects;
	}

	public void addAppliedEffect(AmuletEffect effect) {
		appliedEffects.add(effect);
	}

	@Override
	public String generateOrigEffectsText() {
		return "amulet effect string-rep not supported yet";
	}

}
