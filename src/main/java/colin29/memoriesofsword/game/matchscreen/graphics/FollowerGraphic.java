package colin29.memoriesofsword.game.matchscreen.graphics;

import colin29.memoriesofsword.game.match.Follower;

public class FollowerGraphic extends PermanentGraphic {

	public FollowerGraphic(Follower parent) {
		super(parent);
	}

	Follower getFollower() {
		return (Follower) super.getPermanent();
	}

}
