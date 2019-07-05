package colin29.memoriesofsword.game.match;

public interface CardInfo {

	String getName();

	int getCost();

	Player getOwner();

	Card.Type getType();

	String generateOrigEffectsText();

}