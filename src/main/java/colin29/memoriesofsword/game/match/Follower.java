package colin29.memoriesofsword.game.match;

/**
 * An follower instance that exists on the battlefield
 * 
 * @author Colin Ta
 *
 */
public class Follower extends Permanent implements FollowerInfo {

	// Later on in development, all buffs will be treated as enchantments that stay attached to the follower. Instead of being modified discreetly,
	// atk will be calculated when requested

	Match match;

	private int atk;
	private int def;
	/**
	 * Can be modified, is independent of the defense stat of the originating card.
	 */
	private int maxDef;

	Follower(Card parentCard) {
		super(parentCard);
		this.atk = parentCard.getAtk();
		this.maxDef = parentCard.getDef();
		def = maxDef;
	}

	/**
	 * These values should return the current atk. Later on, the internal field atk will become origAtk, and getAtk will be calculated from origAtk
	 * and applied effects
	 * 
	 * @return
	 */
	public int getAtk() {
		return atk;
	}

	public int getDef() {
		return def;
	}

	public int getMaxDef() {
		return maxDef;
	}

}
