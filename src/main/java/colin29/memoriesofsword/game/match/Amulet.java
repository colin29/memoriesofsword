package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;

import colin29.memoriesofsword.game.match.cardeffect.AmuletEffect;

public class Amulet extends Permanent<AmuletEffect> {

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

}
