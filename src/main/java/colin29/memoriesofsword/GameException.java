package colin29.memoriesofsword;

public class GameException extends RuntimeException {

	public GameException() {
		super();
	}

	public GameException(String string) {
		super(string);
	}

	public GameException(String string, Throwable cause) {
		super(string, cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getMsgAndStackTrace() {
		return this.getMessage() + "\n" + getFromMessage();
	}

	public String getFromMessage() {
		return "From:\n" + this.getFormattedStackTrace();
	}

	public String getFormattedStackTrace() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.getStackTrace().length; i++) {
			sb.append("    ");
			sb.append(this.getStackTrace()[i]).toString();
			if (i != this.getStackTrace().length - 1) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}