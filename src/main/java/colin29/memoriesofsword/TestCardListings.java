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

/**
 * Static class that creates the test card listings
 * 
 * @author Colin Ta
 *
 */
public class TestCardListings {

	public static void addTestCardsToRepo(CardRepository cardRepo) {
		CardListing c1 = CardListing.makeFollowerTempCardListing("Healing Angel", 3, 2, 3);
		CardListing c2 = CardListing.makeFollowerTempCardListing("Shield Dude", 4, 1, 7);
		CardListing c3 = CardListing.makeFollowerTempCardListing("Goblin", 1, 1, 2);
		c3.addEffect(createAoeDamageFollowerEffect());

		CardListing c4 = CardListing.makeAmuletTempCardListing("Horn of Unica", 3);
		c4.addEffect(createHornOfUnicaEffect());
		c4.addEffect(createHornOfUnicaAlliedETBEffect());

		cardRepo.addCard(c1, c2, c3, c4);
	}

	private static FollowerCardEffect createAoeDamageFollowerEffect() {
		FollowerCardEffect effect = new FollowerCardEffect(FollowerCardEffect.TriggerType.FANFARE);

		EffectOnFollower targetedEffect = new EffectOnFollower(FollowerTargeting.ENEMY_FOLLOWERS);
		effect.addTriggeredEffect(targetedEffect);

		ActionOnFollower doDamage = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		doDamage.amount = 2;
		targetedEffect.setAction(doDamage);

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

		EffectOnFollower targetedAction = new EffectOnFollower(FollowerTargeting.ETB_FOLLOWER);
		effect.addTriggeredEffect(targetedAction);

		ActionOnFollower buff = new ActionOnFollower(ActionOnFollower.ActionType.BUFF);
		buff.atkBuff = 0;
		buff.defBuff = 1;
		targetedAction.setAction(buff);

		return effect;
	}

}
