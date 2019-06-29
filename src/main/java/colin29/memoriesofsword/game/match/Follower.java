package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.game.match.cardeffect.FollowerEffect;

/**
 * An follower instance that exists on the battlefield <br>
 * <br>
 * 
 * @author Colin Ta
 *
 */
public class Follower extends Permanent implements FollowerInfo {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	// Later on in development, all buffs will be treated as enchantments that stay attached to the follower. Instead of being modified discreetly,
	// atk will be calculated when requested

	private int atk;
	private int def;
	/**
	 * Can be modified, is independent of the defense stat of the originating card.
	 */
	private int maxDef;

	/**
	 * These effects were added to this follower from the parent card at cast time
	 */
	private final ArrayList<FollowerEffect> origEffects = new ArrayList<FollowerEffect>();
	/**
	 * Effects added later
	 */
	private final ArrayList<FollowerEffect> appliedEffects = new ArrayList<FollowerEffect>();

	/**
	 * On creation, the follower effects on the parent card are copied and added to the new follower
	 * 
	 * @param parentCard
	 */
	Follower(Card parentCard) {
		super(parentCard);
		this.atk = parentCard.getAtk();
		this.maxDef = parentCard.getDef();
		def = maxDef;

		copyEffectsFromParentCard();
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

	/**
	 * Deals damage to the follower, applying any modification effects on the follower.
	 * 
	 * @return The actual amount of damage dealt (can be overkill).
	 */
	public int dealDamage(int damage) {

		if (!isOnOwnersBattlefield()) {
			logger.warn("Tried to heal, permanent not on owner's battlefield.");
			return 0;
		}

		if (damage < 0) {
			logger.warn("Tried to damage for a negative amount {}. Ignoring.", damage);
			return 0;
		}

		def -= damage;

		logger.debug("Dealt {} damage to {}", damage, this.getName());
		match.simple.notifyCardStatsModified();
		if (def <= 0) {
			match.handleDeath(this);
		}
		return damage;
	}

	/**
	 * 
	 * @param healAmount
	 * @return The amount actually healed (doesn't count overheal)
	 */
	public int heal(int healAmount) {

		if (!isOnOwnersBattlefield()) {
			logger.warn("Tried to heal, permanent not on owner's battlefield.");
			return 0;
		}

		if (healAmount < 0) {
			logger.warn("Tried to heal for a negative amount {}. Ignoring.", healAmount);
			return 0;
		}

		int oldDef = def;
		def = Math.max(def + healAmount, maxDef);

		int amountHealed = def - oldDef;
		logger.debug("Healed {} damage on {}", amountHealed, this.getName());

		if (amountHealed != 0) {
			match.simple.notifyCardStatsModified();
		}
		return amountHealed;
	}

	public boolean isMaxDef() {
		return def == maxDef;
	}

	public boolean isWounded() {
		return !isMaxDef();
	}

	public boolean isAtkGreaterThanOrig() {
		return atk > parentCard.getAtk();
	}

	public boolean isDefGreaterThanOrig() {
		return def > parentCard.getDef();
	}

	public void buffAtk(int amount) {
		if (amount < 0) {
			logger.warn("Buff atk doesn't permit negative numbers");
			return;
		}
		this.atk += amount;
		match.simple.notifyCardStatsModified();
	}

	public void buffDef(int amount) {
		if (amount < 0) {
			logger.warn("Buff def doesn't permit negative numbers");
			return;
		}
		this.def += amount;
		this.maxDef += amount;
		match.simple.notifyCardStatsModified();
	}

	public Player getOwner() {
		return parentCard.getOwner();
	}

	private void copyEffectsFromParentCard() {
		for (FollowerEffect effect : parentCard.getFollowerEffects()) {
			origEffects.add(new FollowerEffect(effect));
		}
	}

	// Returns a list of all effects on this follower (in order)
	public List<FollowerEffect> getEffects() {
		List<FollowerEffect> effects = new ArrayList<FollowerEffect>();
		effects.addAll(origEffects);
		effects.addAll(appliedEffects);
		return effects;
	}

	public void addAppliedEffect(FollowerEffect effect) {
		appliedEffects.add(effect);
	}

	@Override
	public String generateOrigEffectsText() {

		StringBuilder s = new StringBuilder();

		boolean first = true;

		for (FollowerEffect effect : origEffects) {
			if (first) {
				first = false;
			} else {
				s.append("\n");
			}
			s.append(effect.toString());
		}
		return s.toString();
	}

}
