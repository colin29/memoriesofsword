package colin29.memoriesofsword.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.Card.Type;
import colin29.memoriesofsword.game.match.cardeffect.AmuletEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerEffect;
import colin29.memoriesofsword.game.match.cardeffect.SpellEffect;

/**
 * A Card outside the context of a match. Unlike a cards within a match, a card listing is generally not modified.
 * 
 * When a match begins, the Match code takes a card listing and generates however many Card objects, depending on how many copies are in a deck.
 * 
 * @author Colin Ta
 *
 */
public class CardListing {

	Logger logger = LoggerFactory.getLogger(this.getClass());

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

	private final List<FollowerEffect> followerEffects = new ArrayList<FollowerEffect>();
	private final List<AmuletEffect> amuletEffects = new ArrayList<AmuletEffect>();
	private final List<SpellEffect> spellEffects = new ArrayList<SpellEffect>();

	CardListing(String name, int cost, int atk, int def, int id, Card.Type type) {
		this.name = name;
		this.cost = cost;
		this.atk = atk;
		this.def = def;
		this.id = id;

		this.type = type;
	}

	public static CardListing makeFollowerTempCardListing(String name, int cost, int atk, int def) {
		return new CardListing(name, cost, atk, def, generateNextTempId(), Type.FOLLOWER);
	}

	public static CardListing makeAmuletTempCardListing(String name, int cost) {
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

	public void addEffect(FollowerEffect effect) {
		if (effect == null) {
			logger.warn("Can't add a null effect. Ignored.");
			return;
		}
		if (type != Type.FOLLOWER) {
			logger.warn("Tried to add a Follower effect to a {} listing. Ignored.", type.name());
			return;
		}
		followerEffects.add(effect);
	}

	public void addEffect(AmuletEffect effect) {
		if (effect == null) {
			logger.warn("Can't add a null effect. Ignored.");
			return;
		}
		if (type != Type.AMULET) {
			logger.warn("Tried to add an Amulet effect to a {} listing. Ignored.", type.name());
			return;
		}
		amuletEffects.add(effect);
	}

	public void addEffect(SpellEffect effect) {
		if (effect == null) {
			logger.warn("Can't add a null effect. Ignored.");
			return;
		}
		if (type != Type.SPELL) {
			logger.warn("Tried to add Spell effect to a {} listing. Ignored.", type.name());
			return;
		}
		spellEffects.add(effect);
	}

	public List<FollowerEffect> getFollowerEffects() {
		return Collections.unmodifiableList(followerEffects);
	}

	public List<AmuletEffect> getAmuletEffects() {
		return Collections.unmodifiableList(amuletEffects);
	}

	public List<SpellEffect> getSpellEffects() {
		return Collections.unmodifiableList(spellEffects);
	}

}
