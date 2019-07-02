package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.CardEffect;
import colin29.memoriesofsword.game.match.cardeffect.EffectSource;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.SpellEffect;

/**
 * A Card in the context of a match.
 * 
 * A Card is what you would find in player's hand, deck or graveyard. A card on the field is represented instead by a {@link Permanent}.
 * 
 * Multiple copies of a card in a deck are represented by multiple card objects (originally holding the same information).
 * 
 * Not using inheritance for different card types because it's simple enough and this project has tons of card-type related inheritance already
 * already
 * 
 * @author Colin Ta
 *
 */
public class Card implements EffectSource, CardInfo {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Name should really only be set on card construction.
	 */
	private String name = "";
	private int cost;
	private int atk;
	private int def;

	private String text;

	private final Match match;

	private Player owner;

	/**
	 * For follower cards. When the card is played, these effects are copied and given to the new follower.
	 */
	private final List<FollowerCardEffect> followerEffects = new ArrayList<FollowerCardEffect>();

	/**
	 * Same as the previous field, but for amulets
	 */
	private final List<AmuletCardEffect> amuletEffects = new ArrayList<AmuletCardEffect>();

	/**
	 * For Spell cards. When a spell is played, the effects are executed.
	 */
	private final List<SpellEffect> spellEffects = new ArrayList<SpellEffect>();

	public enum Type { // Card includes all the fields. Unused fields for a type (such as 'atk' for a spell) will simply be left default.
		FOLLOWER, AMULET, SPELL;

		public boolean isPermanent() {
			return (this == FOLLOWER || this == AMULET);
		}
	}

	public final Type type;

	public Card(CardListing listing, Player owner, Match match) {
		name = listing.getName();
		cost = listing.getCost();
		atk = listing.getAtk();
		def = listing.getDef();

		type = listing.getType();

		this.owner = owner;
		this.match = match;

		copyEffectsFromCardListing(listing);
	}

	/**
	 * TODO: static methods to create cards of each type
	 */

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getCost() {
		return cost;
	}

	@Override
	public int getAtk() {
		return atk;
	}

	@Override
	public int getDef() {
		return def;
	}

	@Override
	public Player getOwner() {
		return owner;
	}

	public Match getMatch() {
		return this.match;
	}

	@Override
	public Type getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public void addFollowerEffect(FollowerCardEffect effect) {
		if (type != Type.FOLLOWER) {
			logger.warn("Tried to add Follower effect but card is not Follower type, ignoring. (effect would never be used anyways");
			return;
		}
		followerEffects.add(effect);
	}

	public void addAmuletEffect(AmuletCardEffect effect) {
		if (type != Type.AMULET) {
			logger.warn("Tried to add Amulet effect but card is not Amulet type, ignoring. (effect would never be used anyways");
			return;
		}
		amuletEffects.add(effect);
	}

	public List<FollowerCardEffect> getFollowerEffects() {
		return Collections.unmodifiableList(followerEffects);
	}

	public List<AmuletCardEffect> getAmuletEffects() {
		return Collections.unmodifiableList(amuletEffects);
	}

	@Override
	public String generateOrigEffectsText() {
		switch (type) {
		case FOLLOWER:
			return generateTextForListOfEffects(this.followerEffects);
		case AMULET:
			return generateTextForListOfEffects(this.amuletEffects);
		case SPELL:
			return "No string-rep for Spell-type cards yet.";
		default:
			throw new AssertionError("Unknown card type!");

		}

	}

	/**
	 * One point of control formatting
	 * 
	 * @param effects
	 * @return
	 */
	public static String generateTextForListOfEffects(List<? extends CardEffect> effects) {
		StringBuilder s = new StringBuilder();

		boolean first = true;

		for (CardEffect effect : effects) {
			if (first) {
				first = false;
			} else {
				s.append("\n");
			}
			s.append(effect.toString());
		}
		return s.toString();
	}

	private void copyEffectsFromCardListing(CardListing listing) {
		for (FollowerCardEffect effect : listing.getFollowerEffects()) {
			followerEffects.add(new FollowerCardEffect(effect));
		}
		for (AmuletCardEffect effect : listing.getAmuletEffects()) {
			amuletEffects.add(new AmuletCardEffect(effect));
		}
		for (SpellEffect effect : listing.getSpellEffects()) {
			spellEffects.add(new SpellEffect(effect));
		}
	}

	@Override
	public String getSourceName() {
		return getPNumName();
	}

	/**
	 * Get the permanent's name, prepended with the player number: "p2"
	 */
	public String getPNumName() {
		return getOwner().getPNum() + " " + getName();
	}

	@Override
	public Card getSourceCard() {
		return this;
	}

}
