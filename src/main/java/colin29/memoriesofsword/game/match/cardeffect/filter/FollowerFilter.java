package colin29.memoriesofsword.game.match.cardeffect.filter;

import java.util.function.Predicate;

import colin29.memoriesofsword.game.match.Follower;

public class FollowerFilter {

	public enum Type {
		STATS_COMPARISON, HAS_PROPERTY, LACKS_PROPERTY,
	}

	// StatType, Comparison, and amount are all used for Stats_Comparison only
	public enum CompareStat {
		ATTACK, DEFENSE, COST;

		/**
		 * @param plural
		 *            if the text is applying to multiple targets (ie. a filter on an aoe effect) Returns something along the lines of "that costs" or
		 *            "with defense"
		 */
		public String getGameText(boolean isPlural) {
			switch (this) {
			case COST:
				return isPlural ? "that cost" : "that costs";
			case ATTACK:
				return "with attack";
			case DEFENSE:
				return "with defense";
			default:
				return "{no string-rep for this compare-stat type}";

			}
		}
	}

	public enum ComparisonType {
		GREATER_THAN_OR_EQUAL, EQUAL, LESS_THAN_OR_EQUAL
	}

	final Type type;

	/**
	 * Amount to compare to
	 */
	public int amount;

	public CompareStat statToCompare;
	public ComparisonType comparison;

	// Only type stats comparison supported atm
	// public FollowerFilter(Type type) {
	// this.type = type;
	// }

	public FollowerFilter(CompareStat statToCompare, ComparisonType comparison, int amount) {
		type = Type.STATS_COMPARISON;
		this.statToCompare = statToCompare;
		this.comparison = comparison;
		this.amount = amount;
	}

	public FollowerFilter(FollowerFilter src) {
		type = src.type;
		statToCompare = src.statToCompare;
		comparison = src.comparison;
		amount = src.amount;
	}

	public FollowerFilter cloneObject() {
		return new FollowerFilter(this);
	}

	public Predicate<Follower> getPredicate() {
		if (type == Type.STATS_COMPARISON) {
			return (Follower f) -> {
				switch (statToCompare) {
				case COST:
					return evaluateStatComparison(f.getCost(), amount, comparison);
				case ATTACK:
					return evaluateStatComparison(f.getAtk(), amount, comparison);
				case DEFENSE:
					return evaluateStatComparison(f.getDef(), amount, comparison);
				default:
					throw new AssertionError("Filter predicate: unsupported compare-stat type");

				}
			};
		}

		throw new AssertionError("Filter predicate: unsupported Filter type");
	}

	public boolean evaluateStatComparison(int v1, int v2, ComparisonType comparison) {
		switch (comparison) {
		case EQUAL:
			return v1 == v2;
		case GREATER_THAN_OR_EQUAL:
			return v1 >= v2;
		case LESS_THAN_OR_EQUAL:
			return v1 <= v2;
		default:
			throw new AssertionError("Filter predicate: unsupported comparison type");
		}
	}

	public String toString(boolean isPlural) {
		if (type == Type.STATS_COMPARISON) {
			switch (comparison) {
			case EQUAL:
				return statToCompare.getGameText(isPlural) + " " + amount;
			case GREATER_THAN_OR_EQUAL:
				return statToCompare.getGameText(isPlural) + " at least " + amount;
			case LESS_THAN_OR_EQUAL:
				return statToCompare.getGameText(isPlural) + " " + amount + " or less";
			default:
				return "{No string-rep for  Comparison type}";

			}
		}
		return "{No string-rep for this Filter type '" + type + "'}";
	}

}
