package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect;

public class FollowerCard extends Card {

	private int atk;
	private int def;

	/**
	 * For follower cards. When the card is played, these effects are copied and given to the new follower.
	 */
	private final List<FollowerCardEffect> effects = new ArrayList<FollowerCardEffect>();

	public FollowerCard(CardListing listing, Player owner, Match match) {
		super(listing, owner, match);
		atk = listing.getAtk();
		def = listing.getDef();

		copyEffectsFromCardListing(listing);
	}

	public int getAtk() {
		return atk;
	}

	public int getDef() {
		return def;
	}

	public void addFollowerEffect(FollowerCardEffect effect) {
		if (type != Type.FOLLOWER) {
			logger.warn("Tried to add Follower effect but card is not Follower type, ignoring. (effect would never be used anyways");
			return;
		}
		effects.add(effect);
	}

	@Override
	public List<FollowerCardEffect> getEffects() {
		return Collections.unmodifiableList(effects);
	}

	@Override
	public String generateOrigEffectsText() {
		return generateTextForListOfEffects(effects);
	}

	@Override
	protected void copyEffectsFromCardListing(CardListing listing) {
		for (FollowerCardEffect effect : listing.getFollowerEffects()) {
			effects.add(new FollowerCardEffect(effect));
		}
	}

}
