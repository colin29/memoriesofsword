package colin29.memoriesofsword.game.match;

import colin29.memoriesofsword.GameException;

public class InvalidArgumentException extends GameException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	InvalidArgumentException() {
		super();
	}

	InvalidArgumentException(String msg) {
		super(msg);
	}

}
