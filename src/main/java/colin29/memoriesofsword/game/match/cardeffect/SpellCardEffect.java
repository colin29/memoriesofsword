package colin29.memoriesofsword.game.match.cardeffect;

/**
 * Only has one type: triggered_effect
 * 
 * @author Colin Ta
 *
 */
public class SpellCardEffect extends CardEffect {

	public enum TriggerType {
		ON_CAST;

		public String getGameText() {
			switch (this) {
			case ON_CAST:
				return ""; // no prefix text for on_cast
			default:
				return "{No string-rep for this Spell trigger type)";

			}
		}
	}

	public TriggerType triggerType;

	public SpellCardEffect(TriggerType triggerType) {
		this.triggerType = triggerType;
	}

	public SpellCardEffect(SpellCardEffect src) {
		triggerType = src.triggerType;
		for (Effect e : src.triggeredEffects) {
			triggeredEffects.add(e.cloneObject());
		}
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(triggerType.getGameText());
		for (Effect targetedAction : triggeredEffects) {
			s.append(targetedAction.toString() + ". ");
		}
		return s.toString();
	}

}
