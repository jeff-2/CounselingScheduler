package action;

public class InvalidFormDataException extends Exception {

	private static final long serialVersionUID = -8341706410868259084L;
	private String context;
	
	public InvalidFormDataException(String msg, String context) {
		super(msg);
		this.context = context;
	}
	
	public String getContext() {
		return context;
	}
}
