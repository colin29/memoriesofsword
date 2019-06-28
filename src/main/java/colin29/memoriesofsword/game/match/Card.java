package colin29.memoriesofsword.game.match;

/**
 * A Card in the context of a match.
 * 
 * A Card is what you would find in player's hand, deck or graveyard. A card on the field is represented instead by a {@link Permanent}.
 * 
 * Multiple copies of a card in a deck are represented by multiple card objects (originally holding the same information).
 * 
 * @author Colin Ta
 *
 */
public class Card implements CardInfo {

	/**
	 * Name should really only be set on card construction.
	 */
	private String name = "";
	private int cost;
	private int atk;
	private int def;

	private String text;

	private final Match match;

	private Player owner;

	public enum Type { // Card includes all the fields. Unused fields for a type (such as 'atk' for a spell) will simply be left default.
		FOLLOWER, AMULET, SPELL
	}

	public final Type type;

	public Card(String name, Card.Type type, int cost, int atk, int def, String text, Player owner, Match match) {
		this.name = name;
		this.cost = cost;
		this.atk = atk;
		this.def = def;

		this.text = text;

		this.owner = owner;
		this.match = match;

		this.type = type;
	}

	/**
	 * TODO: static methods to create cards of each type
	 */

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getCost() {
		return cost;
	}

	@Override
	public int getAtk() {
		return atk;
	}

	@Override
	public int getDef() {
		return def;
	}

	@Override
	public Player getOwner() {
		return owner;
	}

	public Match getMatch() {
		return this.match;
	}

	@Override
	public Type getType() {
		return type;
	}

}
