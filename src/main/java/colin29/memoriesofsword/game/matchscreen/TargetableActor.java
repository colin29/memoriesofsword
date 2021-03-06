package colin29.memoriesofsword.game.matchscreen;

/**
 * Represents an actor that is associated with a target. When the user is prompted for targeting, and user clicks on the actor, the target will be
 * supplied back to the game
 * 
 * All things classes that implement this interface must be an Actor.
 * 
 * @author Colin Ta
 *
 */
public interface TargetableActor {
	PermanentOrPlayer getTarget();

}
