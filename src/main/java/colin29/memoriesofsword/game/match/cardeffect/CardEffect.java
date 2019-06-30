package colin29.memoriesofsword.game.match.cardeffect;

/**
 * A Card effect is something which is held by Card listings, Cards, or permanents.
 * 
 * A common type of CardEffect is a Triggered effect like a Fanfare or Clash
 * 
 * A Triggered effect differs from an Effect in that a triggered effect includes a condition. When that condition happens, the match adds the a copy
 * of the contained Effect to the stack
 * 
 * In a real game, all effects originate from CardEffects.
 * 
 * @author Colin Ta
 *
 */
public abstract class CardEffect {

	@Override
	public abstract String toString();
}
