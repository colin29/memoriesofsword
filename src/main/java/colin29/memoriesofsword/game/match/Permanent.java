package colin29.memoriesofsword.game.match;

/**
 * Represents the entity that exists on the battlefield when a card is played. When the Permanent is destroyed or leaves the battlefield, this object
 * ceases to exist, and the associated card goes from the battlefield to the graveyard.
 * 
 * What happens when permanents are not played but spawned (ie. tokens)? In shadowverse tokens permanents function as fully fledged cards, so a spawn
 * effect would also create Card instances for every permanent instances it creates
 * 
 * All permanents take up a slot on their owner's field.
 * 
 * 
 * @author Colin Ta
 *
 */
public abstract class Permanent {

	/**
	 * A permanent on the field has a 1-1 association with a parentCard. Two permanents cannot be associated with the same card.
	 */
	private final Card parentCard;

	Permanent(Card parentCard) {
		if (parentCard == null) {
			throw new InvalidArgumentException("Parent card cannot be null");
		}
		this.parentCard = parentCard;
	}

	public Card getParentCard() {
		return parentCard;
	}

	public int getCost() {
		return parentCard.getCost();
	}

}
