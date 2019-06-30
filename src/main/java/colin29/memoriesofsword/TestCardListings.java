package colin29.memoriesofsword;

import colin29.memoriesofsword.game.CardListing;
import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.cardeffect.ActionOnFollower;
import colin29.memoriesofsword.game.match.cardeffect.AmuletEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerTargetedAction;
import colin29.memoriesofsword.game.match.cardeffect.FollowerTargetedAction.FollowerTargeting;

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

	private static FollowerEffect createAoeDamageFollowerEffect() {
		FollowerEffect myEffect = new FollowerEffect(FollowerEffect.TriggerType.FANFARE);

		FollowerTargetedAction targetedEffect = new FollowerTargetedAction(FollowerTargeting.ENEMY_FOLLOWERS);
		myEffect.addTriggeredAction(targetedEffect);

		ActionOnFollower doDamage = new ActionOnFollower(ActionOnFollower.ActionType.DO_DAMAGE);
		doDamage.amount = 2;
		targetedEffect.setAction(doDamage);

		return myEffect;
	}

	private static AmuletEffect createHornOfUnicaEffect() {
		AmuletEffect myEffect = new AmuletEffect(AmuletEffect.TriggerType.FANFARE);

		FollowerTargetedAction targetedAction = new FollowerTargetedAction(FollowerTargeting.ALLIED_FOLLOWERS);
		myEffect.addTriggeredAction(targetedAction);

		ActionOnFollower buff = new ActionOnFollower(ActionOnFollower.ActionType.BUFF);
		buff.atkBuff = 2;
		buff.defBuff = 1;
		targetedAction.setAction(buff);

		return myEffect;
	}

}
