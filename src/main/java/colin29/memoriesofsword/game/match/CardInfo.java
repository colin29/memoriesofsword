package colin29.memoriesofsword.game.match;

public interface CardInfo {

	String getName();

	int getCost();

	Player getOwner();

	String generateOrigEffectsText();

}