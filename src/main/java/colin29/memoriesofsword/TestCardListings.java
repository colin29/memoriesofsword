package colin29.memoriesofsword;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnPlayer;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnPlayer.ActionType;
import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower.FollowerTargeting;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollowerOrPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollowerOrPlayer.FollowerOrPlayerTargeting;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer.Targeting;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect.PropertyType;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect.TriggerType;
import colin29.memoriesofsword.game.match.cardeffect.SpellCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.filter.FollowerFilter;
import colin29.memoriesofsword.game.match.cardeffect.filter.FollowerFilter.CompareStat;
import colin29.memoriesofsword.game.match.cardeffect.filter.FollowerFilter.ComparisonType;

/**
 * Static class that creates the test card listings
 * 
 * @author Colin Ta
 *
 */
public class TestCardListings {

	public static void addTestCardsToRepo(CardRepository cardRepo) {
		CardListing c1 = CardListing.makeFollowerTempCardListing("Forest Striker", 3, 2, 3);
		c1.addEffect(createForestStrikerFanfareTargetedEffect());
		c1.addEffect(createForestStrikerEffect());

		CardListing c2 = CardListing.makeFollowerTempCardListing("Shield Dude", 4, 1, 7);
		c2.addEffect(new FollowerCardEffect(PropertyType.RUSH));
		c2.addEffect(createShieldDudeEffect());

		CardListing c3 = CardListing.makeFollowerTempCardListing("Goblin", 1, 1, 2);
		c3.addEffect(createAoeDamageEffect());

		CardListing c4 = CardListing.makeAmuletTempCardListing("Horn of Unica", 3);
		c4.addEffect(createHornOfUnicaEffect());
		c4.addEffect(createHornOfUnicaAlliedETBEffect());

		CardListing c5 = CardListing.makeFollowerTempCardListing("Dagger master", 4, 2, 5);
		c5.addEffect(createDaggerMasterEffect());
		CardListing c6 = CardListing.makeFollowerTempCardListing("Healing angel", 4, 3, 4);
		c6.addEffect(createHealingAngelEffect());
		CardListing c7 = CardListing.makeSpellTempCardListing("Frost Bolt", 2);
		c7.addEffect(createFrostBoltEffect());

		cardRepo.addCard(c1, c2, c3, c4, c5, c6, c7);
	}

	private static FollowerCardEffect createHealingAngelEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(TriggerType.FANFARE);

		EffectOnFollowerOrPlayer e = new EffectOnFollowerOrPlayer(FollowerOrPlayerTargeting.SELECTED_ALLY);
		ActionOnFollowerOrPlayer heal = new ActionOnFollowerOrPlayer(ActionOnFollowerOrPlayer.ActionType.HEAL_DEFENSE);
		heal.amount = 2;
		e.setAction(heal);

		effect.addTriggeredEffect(e);

		return effect;
	}

	private static FollowerCardEffect createDaggerMasterEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(TriggerType.FANFARE);

		EffectOnFollowerOrPlayer e = new EffectOnFollowerOrPlayer(FollowerOrPlayerTargeting.SELECTED_ENEMY);
		ActionOnFollowerOrPlayer doDamage = new ActionOnFollowerOrPlayer(ActionOnFollowerOrPlayer.ActionType.DO_DAMAGE);
		doDamage.amount = 2;
		e.setAction(doDamage);

		EffectOnPlayer e2 = new EffectOnPlayer(EffectOnPlayer.Targeting.SELECTED_LEADER);
		ActionOnPlayer doDamagePlayer = new ActionOnPlayer(ActionOnPlayer.ActionType.DO_DAMAGE);
		doDamagePlayer.amount = 2;
		e2.setAction(doDamagePlayer);

		effect.addTriggeredEffect(e);
		effect.addTriggeredEffect(e2);

		return effect;
	}

	private static FollowerCardEffect createAoeDamageEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(FollowerCardEffect.TriggerType.FANFARE);

		EffectOnFollower e = new EffectOnFollower(FollowerTargeting.ENEMY_FOLLOWERS);

		ActionOnFollower doDamage = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		doDamage.amount = 1;
		e.setAction(doDamage);
		e.addFilter(new FollowerFilter(CompareStat.ATTACK, ComparisonType.LESS_THAN_OR_EQUAL, 1));

		effect.addTriggeredEffect(e);

		// EffectOnFollowerOrPlayer e2 = new EffectOnFollowerOrPlayer(FollowerOrPlayerTargeting.ALL_ENEMIES);
		// effect.addTriggeredEffect(e2);
		//
		// ActionOnFollowerOrPlayer doDamageAllEnemies = new ActionOnFollowerOrPlayer(ActionOnFollowerOrPlayer.ActionType.DO_DAMAGE);
		// doDamageAllEnemies.amount = 2;
		// e2.setAction(doDamageAllEnemies);

		return effect;
	}

	private static AmuletCardEffect createHornOfUnicaEffect() {
		AmuletCardEffect effect = new AmuletCardEffect(AmuletCardEffect.TriggerType.FANFARE);

		EffectOnFollower e1 = new EffectOnFollower(FollowerTargeting.ALLIED_FOLLOWERS);
		effect.addTriggeredEffect(e1);

		ActionOnFollower buff = new ActionOnFollower(ActionOnFollower.ActionType.BUFF);
		buff.atkBuff = 2;
		buff.defBuff = 1;
		e1.setAction(buff);

		EffectOnPlayer e2 = new EffectOnPlayer(Targeting.OWN_LEADER);

		ActionOnPlayer heal = new ActionOnPlayer(ActionType.HEAL_DEFENSE);
		heal.amount = 3;
		e2.setAction(heal);

		effect.addTriggeredEffect(e2);

		return effect;
	}

	private static AmuletCardEffect createHornOfUnicaAlliedETBEffect() {
		AmuletCardEffect effect = new AmuletCardEffect(AmuletCardEffect.TriggerType.ETB_ALLIED_FOLLOWER);

		EffectOnFollower e = new EffectOnFollower(FollowerTargeting.ETB_FOLLOWER);
		effect.addTriggeredEffect(e);

		ActionOnFollower buff = new ActionOnFollower(ActionOnFollower.ActionType.BUFF);
		buff.atkBuff = 0;
		buff.defBuff = 1;
		e.setAction(buff);

		return effect;
	}

	private static FollowerCardEffect createForestStrikerEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(FollowerCardEffect.TriggerType.THIS_FOLLOWER_BUFFED);

		EffectOnPlayer e1 = new EffectOnPlayer(Targeting.ENEMY_LEADER);
		ActionOnPlayer dmg = new ActionOnPlayer(ActionOnPlayer.ActionType.DO_DAMAGE);
		dmg.amount = 2;
		e1.setAction(dmg);

		effect.addTriggeredEffect(e1);

		// EffectOnFollower e3CausesInfiniteLoop = new EffectOnFollower(FollowerTargeting.THIS_FOLLOWER);
		// ActionOnFollower selfBuff = new ActionOnFollower(ActionOnFollower.ActionType.BUFF);
		// selfBuff.atkBuff = 1;
		// e3CausesInfiniteLoop.setAction(selfBuff);
		// effect.addTriggeredEffect(e3CausesInfiniteLoop);

		return effect;
	}

	private static FollowerCardEffect createForestStrikerFanfareTargetedEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(FollowerCardEffect.TriggerType.FANFARE);

		EffectOnFollower e1 = new EffectOnFollower(FollowerTargeting.SELECTED_ENEMY_FOLLOWER);
		ActionOnFollower dmg = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		dmg.amount = 2;
		e1.setAction(dmg);
		e1.addFilter(new FollowerFilter(CompareStat.ATTACK, ComparisonType.LESS_THAN_OR_EQUAL, 1));

		effect.addTriggeredEffect(e1);
		effect.addTriggeredEffect(e1.cloneObject());
		return effect;
	}

	private static FollowerCardEffect createShieldDudeEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(TriggerType.STRIKE);

		EffectOnFollowerOrPlayer e2 = new EffectOnFollowerOrPlayer(FollowerOrPlayerTargeting.ALL_ENEMIES);
		effect.addTriggeredEffect(e2);

		ActionOnFollowerOrPlayer doDamageAllEnemies = new ActionOnFollowerOrPlayer(ActionOnFollowerOrPlayer.ActionType.DO_DAMAGE);
		doDamageAllEnemies.amount = 1;
		e2.setAction(doDamageAllEnemies);

		return effect;
	}

	private static SpellCardEffect createFrostBoltEffect() {

		SpellCardEffect effect = new SpellCardEffect(SpellCardEffect.TriggerType.ON_CAST);

		EffectOnFollower e1 = new EffectOnFollower(FollowerTargeting.SELECTED_ENEMY_FOLLOWER);
		ActionOnFollower action = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		action.amount = 2;
		e1.setAction(action);

		EffectOnFollower e2 = new EffectOnFollower(FollowerTargeting.ENEMY_FOLLOWERS);
		ActionOnFollower doDamage = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		doDamage.amount = 1;
		e2.setAction(doDamage);
		e2.addFilter(new FollowerFilter(CompareStat.ATTACK, ComparisonType.LESS_THAN_OR_EQUAL, 1));

		effect.addTriggeredEffect(e1);
		effect.addTriggeredEffect(e2);
		return effect;
	}

}
