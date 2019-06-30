package colin29.memoriesofsword;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.AmuletCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerCardEffect;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.EffectOnFollower.FollowerTargeting;

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

		cardRepo.addCard(c1, c2, c3, c4);
	}

	private static FollowerCardEffect createAoeDamageFollowerEffect() {
		FollowerCardEffect myEffect = new FollowerCardEffect(FollowerCardEffect.TriggerType.FANFARE);

		EffectOnFollower targetedEffect = new EffectOnFollower(FollowerTargeting.ENEMY_FOLLOWERS);
		myEffect.addTriggeredEffect(targetedEffect);

		ActionOnFollower doDamage = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		doDamage.amount = 2;
		targetedEffect.setAction(doDamage);

		return myEffect;
	}

	private static AmuletCardEffect createHornOfUnicaEffect() {
		AmuletCardEffect myEffect = new AmuletCardEffect(AmuletCardEffect.TriggerType.FANFARE);

		EffectOnFollower targetedAction = new EffectOnFollower(FollowerTargeting.ALLIED_FOLLOWERS);
		myEffect.addTriggeredEffect(targetedAction);

		ActionOnFollower buff = new ActionOnFollower(ActionOnFollower.ActionType.BUFF);
		buff.atkBuff = 2;
		buff.defBuff = 1;
		targetedAction.setAction(buff);

		return myEffect;
	}

}
