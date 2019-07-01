package colin29.memoriesofsword.game.match;

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

	public void addAppliedEffect(AmuletCardEffect effect) {
		appliedEffects.add(effect);
	}

}
