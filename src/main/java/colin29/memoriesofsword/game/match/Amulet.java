package colin29.memoriesofsword.game.match;

import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;

public class Amulet extends Permanent<AmuletCardEffect> {

	private final AmuletCard parentCard;

	Amulet(AmuletCard parentCard) {
		super(parentCard);
		this.parentCard = parentCard;
		copyEffectsFromParentCard();
	}

	private void copyEffectsFromParentCard() {
		for (AmuletCardEffect effect : parentCard.getEffects()) {
			origEffects.add(new AmuletCardEffect(effect));
		}
	}

	public void addAppliedEffect(AmuletCardEffect effect) {
		appliedEffects.add(effect);
	}

}
