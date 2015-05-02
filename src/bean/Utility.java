package bean;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;

import action.InvalidFormDataException;

/**
 * Contains commonly used static methods.
 *
 * @author Yusheng Hou and Kevin Lim
 */
public final class Utility {

    /**
     * Parses the integer from the given string. If it is invalid, throws
     * InvalidFormDataException with fieldname and context.
     *
     * @param text
     *            the text
     * @param field
     *            the field
     * @param context
     *            the context
     * @return the int
     * @throws InvalidFormDataException
     *             the invalid form data exception
     */
    public static int parseInt(String text, String field, String context)
	    throws InvalidFormDataException {
	int value;
	String errorText = "You must enter a valid integer for " + field;
	if (text == null || text.isEmpty()) {
	    throw new InvalidFormDataException(errorText, context);
	}
	try {
	    value = Integer.parseInt(text);
	} catch (NumberFormatException e) {
	    throw new InvalidFormDataException(errorText, context);
	}
	return value;
    }

    /**
     * Parses the time represented in timeString of the format
     * (\d{1,2}:\d{2}[a,p]m). Rounds down to the nearest hour, unless roundUp is
     * specified, in which it will round up to the nearest hour.
     *
     * @param timeString
     *            the time string
     * @param roundUp
     *            whether or not to round up
     * @return the int representing the (rounded) 24 hour time of the given time
     *         string
     */
    public static int parseTime(String timeString, boolean roundUp) {
	int colonIndex = timeString.indexOf(":");
	int time = Integer.parseInt(timeString.substring(0, colonIndex));
	if (roundUp && timeString.contains(":30")) {
	    time = (Integer.parseInt(timeString.substring(0, colonIndex)) + 1);
	}
	if (timeString.contains("pm") && time < 12) {
	    time += 12;
	}
	return time;
    }

    /**
     * Converts from ListModel of strings to List of strings
     *
     * @param model
     *            the model
     * @return the list
     */
    public static List<String> toStringList(ListModel<String> model) {
	List<String> list = new ArrayList<String>();
	for (int i = 0; i < model.getSize(); i++) {
	    list.add(model.getElementAt(i));
	}
	return list;
    }
}
