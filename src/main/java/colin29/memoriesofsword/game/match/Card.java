package colin29.memoriesofsword.game.match;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.match.cardeffect.CardEffect;
import colin29.memoriesofsword.game.match.cardeffect.EffectSource;

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
public abstract class Card implements EffectSource, CardInfo {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private String name;
	private int cost;

	private String text;

	protected final Match match;

	private Player owner;

	/**
	 * Technically is allowed to be null, if the card was say, created manually
	 */
	private final CardListing cardListing;

	public Card(CardListing listing, Player owner, Match match) {
		name = listing.getName();
		cost = listing.getCost();

		cardListing = listing;

		this.owner = owner;
		this.match = match;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getCost() {
		return cost;
	}

	@Override
	public Player getOwner() {
		return owner;
	}

	public Match getMatch() {
		return this.match;
	}

	public String getText() {
		return text;
	}

	@Override
	public abstract String generateOrigEffectsText();

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

	protected abstract void copyEffectsFromCardListing(CardListing listing);

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

	public abstract List<? extends CardEffect> getEffects();

	public abstract boolean isPermanent();

	public CardListing getCardListing() {
		return cardListing;
	}
}
