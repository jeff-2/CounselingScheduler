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

	public InvalidDateRangeException(String message) {
		super(message);
	}
}
