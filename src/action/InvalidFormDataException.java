package action;

/**
 * The Class InvalidFormDataException is used to indicate when the form data
 * entered by the user is invalid.
 * 
 */
public class InvalidFormDataException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8341706410868259084L;

    /** The context. */
    private String context;

    /**
     * Instantiates a new invalid form data exception.
     *
     * @param msg
     *            the msg
     * @param context
     *            the context
     */
    public InvalidFormDataException(String msg, String context) {
	super(msg);
	this.context = context;
    }

    /**
     * Gets the context.
     *
     * @return the context
     */
    public String getContext() {
	return context;
    }
}
