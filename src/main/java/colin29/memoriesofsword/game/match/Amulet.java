package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;

import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;

public class Amulet extends Permanent<AmuletCardEffect> {

	Amulet(Card parentCard) {
		super(parentCard);

		copyEffectsFromParentCard();
	}

	private void copyEffectsFromParentCard() {
		for (AmuletCardEffect effect : parentCard.getAmuletEffects()) {
			origEffects.add(new AmuletCardEffect(effect));
		}
	}

	// Returns a list of all effects on this follower (in order)
	public List<AmuletCardEffect> getEffects() {
		List<AmuletCardEffect> effects = new ArrayList<AmuletCardEffect>();
		effects.addAll(origEffects);
		effects.addAll(appliedEffects);
		return effects;
	}

	public void addAppliedEffect(AmuletCardEffect effect) {
		appliedEffects.add(effect);
	}

}
