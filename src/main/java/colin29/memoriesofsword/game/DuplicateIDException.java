package colin29.memoriesofsword.game;

import colin29.memoriesofsword.GameException;

public class DuplicateIDException extends GameException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateIDException() {
		super();
	}

	public DuplicateIDException(String string) {
		super(string);
	}
}
