package colin29.memoriesofsword.game.match;

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
 * @author Colin Ta
 *
 */
public abstract class Permanent {

	/**
	 * A permanent on the field has a 1-1 association with a parentCard. Two permanents cannot be associated with the same card.
	 * 
	 * There are basically nothing that will change a parentCard when it is on the field.
	 */
	private final Card parentCard;

	protected final Match match;

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

	public String getName() {
		return parentCard.getName();
	}

}
