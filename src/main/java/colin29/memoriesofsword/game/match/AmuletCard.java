package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;

public class AmuletCard extends Card {

	private final List<AmuletCardEffect> effects = new ArrayList<AmuletCardEffect>();

	public AmuletCard(CardListing listing, Player owner, Match match) {
		super(listing, owner, match);

		copyEffectsFromCardListing(listing);
	}

	public void addAmuletEffect(AmuletCardEffect effect) {
		if (type != Type.AMULET) {
			logger.warn("Tried to add Amulet effect but card is not Amulet type, ignoring. (effect would never be used anyways");
			return;
		}
		effects.add(effect);
	}

	@Override
	public List<AmuletCardEffect> getEffects() {
		return Collections.unmodifiableList(effects);
	}

	@Override
	public String generateOrigEffectsText() {
		return generateTextForListOfEffects(effects);
	}

	@Override
	protected void copyEffectsFromCardListing(CardListing listing) {
		for (AmuletCardEffect effect : listing.getAmuletEffects()) {
			effects.add(new AmuletCardEffect(effect));
		}
	}

}
