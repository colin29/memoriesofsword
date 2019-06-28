package colin29.memoriesofsword.game;

import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.Card.Type;

/**
 * A Card outside the context of a match. Unlike a cards within a match, a card listing is generally not modified.
 * 
 * When a match begins, the Match code takes a card listing and generates however many Card objects, depending on how many copies are in a deck.
 * 
 * @author Colin Ta
 *
 */
public class CardListing {

	private int cost;
	private int atk;
	private int def;

	private String text = "default card text";

	final private Card.Type type;

	/**
	 * Card listings may be stored on disk, however, their ids should always remain unique.
	 */
	private final int id;

	private String name;

	CardListing(String name, int cost, int atk, int def, int id, Card.Type type) {
		this.name = name;
		this.cost = cost;
		this.atk = atk;
		this.def = def;
		this.id = id;

		this.type = type;
	}

	public static CardListing makeTempFollowerCardListing(String name, int cost, int atk, int def) {
		return new CardListing(name, cost, atk, def, generateNextTempId(), Type.FOLLOWER);
	}

	public static CardListing makeTempAmuletCardListing(String name, int cost) {
		return new CardListing(name, cost, 0, 0, generateNextTempId(), Type.AMULET);
	}

	public int getAtk() {
		return atk;
	}

	public int getDef() {
		return def;
	}

	public int getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public Card.Type getType() {
		return type;
	}

	private static int curTempId = 9000;

	/**
	 * Produces an id from a range of numbers which are set aside for testing/temporary cards (ie. 9000-9999) in order to not overlap with real cards
	 */
	private static int generateNextTempId() {
		return curTempId++;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
