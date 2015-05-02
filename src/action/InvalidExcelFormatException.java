package action;

/**
 * Exception used to provide information when an excel sheet provides data of an
 * invalid format
 *
 */
public class InvalidExcelFormatException extends Exception {

    private static final long serialVersionUID = -876532480101804183L;

    public InvalidExcelFormatException(String message) {
	super(message);
    }
}
