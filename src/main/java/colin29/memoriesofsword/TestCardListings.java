package colin29.memoriesofsword;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnPlayer;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnPlayer.ActionType;
import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower.FollowerTargeting;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnPlayer.Targeting;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect.PropertyType;

/**
 * Static class that creates the test card listings
 * 
 * @author Colin Ta
 *
 */
public class TestCardListings {

	public static void addTestCardsToRepo(CardRepository cardRepo) {
		CardListing c1 = CardListing.makeFollowerTempCardListing("Forest Striker", 3, 2, 3);
		c1.addEffect(createForestStrikerEffect());

		CardListing c2 = CardListing.makeFollowerTempCardListing("Shield Dude", 4, 1, 7);
		c2.addEffect(createShieldDudeEffect());

		CardListing c3 = CardListing.makeFollowerTempCardListing("Goblin", 1, 1, 2);
		c3.addEffect(createAoeDamageFollowerEffect());

		CardListing c4 = CardListing.makeAmuletTempCardListing("Horn of Unica", 3);
		c4.addEffect(createHornOfUnicaEffect());
		c4.addEffect(createHornOfUnicaAlliedETBEffect());

		cardRepo.addCard(c1, c2, c3, c4);
	}

	private static FollowerCardEffect createAoeDamageFollowerEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(FollowerCardEffect.TriggerType.FANFARE);

		EffectOnFollower e = new EffectOnFollower(FollowerTargeting.ENEMY_FOLLOWERS);
		effect.addTriggeredEffect(e);

		ActionOnFollower doDamage = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		doDamage.amount = 2;
		e.setAction(doDamage);

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

		EffectOnFollower e2 = new EffectOnFollower(FollowerTargeting.THIS_FOLLOWER);
		ActionOnFollower selfDmg = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		selfDmg.amount = 1;
		e2.setAction(selfDmg);

		effect.addTriggeredEffect(e1);
		effect.addTriggeredEffect(e2);

		// EffectOnFollower e3CausesInfiniteLoop = new EffectOnFollower(FollowerTargeting.THIS_FOLLOWER);
		// ActionOnFollower selfBuff = new ActionOnFollower(ActionOnFollower.ActionType.BUFF);
		// selfBuff.atkBuff = 1;
		// e3CausesInfiniteLoop.setAction(selfBuff);
		// effect.addTriggeredEffect(e3CausesInfiniteLoop);

		return effect;
	}

	private static FollowerCardEffect createShieldDudeEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(PropertyType.RUSH);
		return effect;
	}

}
