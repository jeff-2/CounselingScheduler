package validator;

/**
 * Exception to be thrown when an invalid date range is provided.
 * 
 * @author jmfoste2
 * @author nbeltr2
 */
public class InvalidDateRangeException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7022376673173182715L;

	/**
	 * Instantiates a new invalid date range exception.
	 *
	 * @param message
	 *            the message
	 */
	public InvalidDateRangeException(String message) {
		super(message);
	}
}
