package colin29.memoriesofsword.util.exceptions;

import colin29.memoriesofsword.GameException;

public class InvalidArgumentException extends GameException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidArgumentException() {
		super();
	}

	public InvalidArgumentException(String msg) {
		super(msg);
	}

	public InvalidArgumentException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
