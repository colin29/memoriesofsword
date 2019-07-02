package colin29.memoriesofsword.game.match;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect.PropertyType;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect.Type;

/**
 * An follower instance that exists on the battlefield <br>
 * <br>
 * 
 * @author Colin Ta
 *
 */
public class Follower extends Permanent<FollowerCardEffect> implements FollowerInfo, Attackable {

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
			logger.warn("Tried to damage, permanent not on owner's battlefield.");
			return 0;
		}

		if (damage < 0) {
			logger.warn("Tried to damage for a negative amount {}. Ignoring.", damage);
			return 0;
		}

		def -= damage;

		logger.debug("Dealt {} damage to {}", damage, getPNumName());
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
		def = Math.min(def + healAmount, maxDef);

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

	/**
	 * You should use this method to buff if you need to buff both stats, to avoid doubling triggering
	 * 
	 * @param atkBuff
	 * @param defBuff
	 */
	public void buffStats(int atkBuff, int defBuff) {
		if (atkBuff < 0 || defBuff < 0) {
			logger.warn("Buff stats doesn't permit negative numbers: atkBuff {} defBuff {}", atkBuff, defBuff);
			return;
		}
		buffAtk(atkBuff);
		buffDef(defBuff);
		match.checkForThisFollowerBuffedEffects(this);
		match.simple.notifyCardStatsModified();
	}

	private void buffAtk(int amount) {
		this.atk += amount;
	}

	private void buffDef(int amount) {
		this.def += amount;
		this.maxDef += amount;

	}

	public void attack(Player target) {
		if (getLeader() == target) {
			logger.warn("Follower can't attack own Leader. Ignoring");
			return;
		}
		if (!canAttackPlayers()) {
			logger.warn("Follower is unable to attack followers. Ignoring.");
			return;
		}

		logger.debug(getLeader().getPNum() + " '{}' attacks " + target.getPlayerNum(), getName());

		// TODO: Activate strike (generic) triggers

		attackCount += 1;
		target.dealDamage(atk);
		match.simple.notifyUnitAttacked();
	}

	public void attack(Attackable target) {
		if (target instanceof Follower) {
			attack((Follower) target);
		} else if (target instanceof Player) {
			attack((Player) target);
		} else {
			logger.warn("Unusupported Attackable type {}", target.getClass());
		}
	}

	public void attack(Follower other) {
		if (getLeader() == other.getLeader()) {
			logger.warn("Follower can't attack allied follower. Ignoring");
			return;
		}
		if (!canAttackFollowers()) {
			logger.warn("Follower is unable to attack followers. Ignoring.");
			return;
		}

		logger.debug(getLeader().getPNum() + " '{}' attacks " + getEnemyLeader().getPNum() + " '{}'", this.getName(),
				other.getName());

		// TODO: Activate Follower strike triggers
		match.checkForClashEffects(this, other);

		if (!isOnOwnersBattlefield() || !other.isOnOwnersBattlefield()) {
			logger.debug("A follower died during pre-combat effects, combat ended");
			return;
		}

		// Do damage to each other simultaneously
		match.effectQueue.freeze();
		attackCount += 1;
		this.dealDamage(other.atk); // We want the active follower's last word triggers to happen first, so we have it take damage first
		other.dealDamage(atk);
		match.effectQueue.unfreeze();
		match.processEffectQueue();

		match.simple.notifyUnitAttacked();
	}

	private boolean summoningSickness = true;
	private int attackCount = 0;

	int getMaxAttacksPerTurn() {
		return 1;
	}

	void resetAttacksPerTurn() {
		attackCount = 0;
	}

	/**
	 * Call at the start end of turns
	 */
	void removeSummoningSickness() {
		summoningSickness = false;
	}

	boolean hasRush() {
		return hasProperty(PropertyType.RUSH);
	}

	boolean hasStorm() {
		return hasProperty(PropertyType.STORM);
	}

	boolean hasProperty(PropertyType propertyType) {
		List<FollowerCardEffect> effects = getCardEffects();
		boolean propertyFound = false;
		for (FollowerCardEffect effect : effects) {
			if (effect.type == Type.PROPERTY && effect.propertyType == propertyType) {
				propertyFound = true;
			}
		}
		return propertyFound;
	}

	public boolean canAttackFollowers() {
		return hasAttacksRemaining() && (!summoningSickness || (hasRush() || hasStorm()));
	}

	public boolean canAttackPlayers() {
		return hasAttacksRemaining() && (!summoningSickness || hasStorm());
	}

	private boolean hasAttacksRemaining() {
		return attackCount < getMaxAttacksPerTurn();
	}

	private void copyEffectsFromParentCard() {
		for (FollowerCardEffect effect : parentCard.getFollowerEffects()) {
			origEffects.add(new FollowerCardEffect(effect));
		}
	}

	public void addAppliedEffect(FollowerCardEffect effect) {
		appliedEffects.add(effect);
	}

}
