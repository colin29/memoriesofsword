package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;

import colin29.memoriesofsword.game.match.cardeffect.CardEffect;
import colin29.memoriesofsword.game.match.cardeffect.EffectSource;
import colin29.memoriesofsword.game.matchscreen.PermanentOrPlayer;
import colin29.memoriesofsword.util.exceptions.InvalidArgumentException;

/**
 * Represents the entity that exists on the battlefield when a card is played. When the Permanent is destroyed or leaves the battlefield, this object
 * ceases to exist, and the associated card goes from the battlefield to the graveyard. <br>
 * <br>
 * 
 * In Shadowverse, tokens-permanents are identical to casted cards, so a spawn effect would also create Card instances for every permanent instance it
 * creates. <br>
 * <br>
 * 
 * All permanents take up a slot on their owner's field. <br>
 * 
 * The parameterized type just represents the corresponding card effect type. There is not a tight coupling between the two, just permanent who stores
 * effects
 * 
 * @author Colin Ta
 *
 */
public abstract class Permanent<T extends CardEffect> implements EffectSource, PermanentOrPlayer {

	/**
	 * A permanent on the field has a 1-1 association with a parentCard. Two permanents cannot be associated with the same card.
	 * 
	 * There are basically nothing that will change a parentCard when it is on the field.
	 */
	final Card parentCard;

	protected final Match match;

	/**
	 * These effects were added to this follower from the parent card at cast time
	 */
	protected final List<T> origEffects = new ArrayList<T>();
	/**
	 * Effects added later
	 */
	protected final List<T> appliedEffects = new ArrayList<T>();

	Permanent(Card parentCard) {
		if (parentCard == null) {
			throw new InvalidArgumentException("Parent card cannot be null");
		}
		this.parentCard = parentCard;
		this.match = parentCard.getMatch();
	}

	public Card getParentCard() {
		return parentCard;
	}

	public int getCost() {
		return parentCard.getCost();
	}

	// Permanents only exist on the battlefield, so this method is used just for checking validity (e.g. not damaging a permanent when it's off the
	// field).
	protected boolean isOnOwnersBattlefield() {
		Player owner = this.getParentCard().getOwner();
		return owner.isOnMyField(this);
	}

	@Override
	public String getName() {
		return parentCard.getName();
	}

	public String generateOrigEffectsText() {
		return Card.generateTextForListOfEffects(origEffects);
	};

	/**
	 * Returns a list of all effects on this follower, in order
	 */
	public final List<T> getCardEffects() {
		List<T> effects = new ArrayList<T>();
		effects.addAll(origEffects);
		effects.addAll(appliedEffects);
		return effects;
	}

	public Player getLeader() {
		return parentCard.getOwner();
	}

	public Player getEnemyLeader() {
		return match.getOtherPlayer(parentCard.getOwner());
	}

	@Override
	public String getSourceName() {
		return getPNumName();
	}

	@Override
	public Card getSourceCard() {
		return getParentCard();
	}

	/**
	 * Get the permanent's name, prepended with the player number: "p2"
	 */
	public String getPNumName() {
		return getLeader().getPNum() + " " + getName();
	}

	@Override
	public Player getOwner() {
		return getParentCard().getOwner();
	}

}
