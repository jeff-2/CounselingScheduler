package dao;

/**
 * The Class ConnectionConfigException used to indicate an invalid Connection Configuration was given.
 */
public class ConnectionConfigException extends Exception {

	private static final long serialVersionUID = -5770497542398153318L;
	
	/**
	 * Instantiates a new connection config exception.
	 *
	 * @param message the message
	 */
	public ConnectionConfigException(String message) {
		super(message);
	}
}
