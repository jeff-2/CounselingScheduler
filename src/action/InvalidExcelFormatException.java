package action;

/**
 * Exception used to provide information when an excel sheet provides data of an
 * invalid format.
 */
public class InvalidExcelFormatException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -876532480101804183L;

    /**
     * Instantiates a new invalid excel format exception.
     *
     * @param message the message
     */
    public InvalidExcelFormatException(String message) {
	super(message);
    }
}
