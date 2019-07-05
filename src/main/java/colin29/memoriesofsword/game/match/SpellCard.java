package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.match.cardeffect.Effect;
import colin29.memoriesofsword.game.match.cardeffect.SpellCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.SpellCardEffect.TriggerType;
import colin29.memoriesofsword.game.matchscreen.PermanentOrPlayer;

public class SpellCard extends Card {

	/**
	 * For Spell cards. When a spell is played, onCast effects are executed.
	 */
	private final List<SpellCardEffect> effects = new ArrayList<SpellCardEffect>();

	public SpellCard(CardListing listing, Player owner, Match match) {
		super(listing, owner, match);

		copyEffectsFromCardListing(listing);
	}

	@Override
	public List<SpellCardEffect> getEffects() {
		return Collections.unmodifiableList(effects);
	}

	@Override
	public String generateOrigEffectsText() {
		return generateTextForListOfEffects(effects);
	}

	/**
	 * Returns true if the spell's on_cast triggered effects that are user target, all have valid targets. <br>
	 * 
	 * The effects' sources do not need to be set. All effects will be assumed to have source of this card (as they are contained in this spell card)
	 * 
	 */
	public boolean areAllSpellCardTargetingEffectsMet() {

		List<PermanentOrPlayer> targets = match.getAllTargets();

		for (SpellCardEffect effect : effects) {
			if (effect.triggerType == TriggerType.ON_CAST) {
				for (Effect e : effect.getTriggeredEffects()) {
					if (e.isUsingUserTargeting()) {
						Effect copy = e.cloneObject();
						copy.setSource(this);
						boolean validTargetExists = false;
						for (PermanentOrPlayer target : targets) {
							if (copy.isValidTarget(target)) {
								validTargetExists = true;
							}
						}
						if (!validTargetExists) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void copyEffectsFromCardListing(CardListing listing) {
		for (SpellCardEffect effect : listing.getSpellEffects()) {
			effects.add(new SpellCardEffect(effect));
		}
	}

	@Override
	public final boolean isPermanent() {
		return false;
	}

}
