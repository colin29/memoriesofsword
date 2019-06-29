package colin29.memoriesofsword.game.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colin29.memoriesofsword.game.match.cardeffect.AmuletEffect;
import colin29.memoriesofsword.game.match.cardeffect.FollowerEffect;

/**
 * A Card in the context of a match.
 * 
 * A Card is what you would find in player's hand, deck or graveyard. A card on the field is represented instead by a {@link Permanent}.
 * 
 * Multiple copies of a card in a deck are represented by multiple card objects (originally holding the same information).
 * 
 * Not using inheritance for different card types because it's simple enough and this project has tons of card-type related inheritance already
 * already
 * 
 * @author Colin Ta
 *
 */
public class Card implements CardInfo {

	Logger logger = LoggerFactory.getLogger(this.getClass());

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

	/**
	 * For follower cards. When the card is played, these effects are copied and given to the new follower.
	 */
	private final List<FollowerEffect> followerEffects = new ArrayList<FollowerEffect>();

	/**
	 * Same as the previous field, but for amulets
	 */
	private final List<AmuletEffect> amuletEffects = new ArrayList<AmuletEffect>();

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

	public String getText() {
		return text;
	}

	public void addFollowerEffect(FollowerEffect effect) {
		if (type != Type.FOLLOWER) {
			logger.warn("Tried to add Follower effect but card is not Follower type, ignoring. (effect would never be used anyways");
			return;
		}
		followerEffects.add(effect);
	}

	public void addAmuletEffect(AmuletEffect effect) {
		if (type != Type.AMULET) {
			logger.warn("Tried to add Amulet effect but card is not Amulet type, ignoring. (effect would never be used anyways");
			return;
		}
		amuletEffects.add(effect);
	}

	public List<FollowerEffect> getFollowerEffects() {
		return Collections.unmodifiableList(followerEffects);
	}

	public List<AmuletEffect> getAmuletEffects() {
		return Collections.unmodifiableList(amuletEffects);
	}

}
