package bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Class OperatingHours provides the set of times the counseling center is
 * open and utilities for converting between a time string (e.g. 8:00 am) and a
 * 24 hour representation of that time (8) and back.
 *
 * @author jmfoste2, lim92
 */
public class OperatingHours {

    /** The Constant stringToInteger. */
    private static final Map<String, Integer> stringToInteger;

    /** The Constant integerToString. */
    private static final Map<Integer, String> integerToString;

    static {
	Map<String, Integer> strToInt = new LinkedHashMap<String, Integer>();
	strToInt.put("8:00 am", 8);
	strToInt.put("9:00 am", 9);
	strToInt.put("10:00 am", 10);
	strToInt.put("11:00 am", 11);
	strToInt.put("12:00 pm", 12);
	strToInt.put("1:00 pm", 13);
	strToInt.put("2:00 pm", 14);
	strToInt.put("3:00 pm", 15);
	strToInt.put("4:00 pm", 16);
	strToInt.put("5:00 pm", 17);

	stringToInteger = Collections.unmodifiableMap(strToInt);

	Map<Integer, String> intToStr = new HashMap<Integer, String>();
	for (Map.Entry<String, Integer> entry : stringToInteger.entrySet()) {
	    intToStr.put(entry.getValue(), entry.getKey());
	}
	integerToString = Collections.unmodifiableMap(intToStr);
    }

    /**
     * Gets set of operating hours.
     *
     * @return the operating hours
     */
    public static Set<String> getOperatingHours() {
	return stringToInteger.keySet();
    }

    /**
     * Converts string representation of operating hour to 24 hour time (e.g.
     * 8:00am -> 8). Returns -1 if there is no corresponding operating hour.
     *
     * @param operatingHour
     *            the operating hour
     * @return the int
     */
    public static int toInt(String operatingHour) {
	Integer value = stringToInteger.get(operatingHour);
	if (value == null) {
	    return -1;
	}
	return value.intValue();
    }

    /**
     * Converts 24 hour time to a string (e.g. 8 -> 8:00 am).
     *
     * @param operatingHour
     *            the operating hour
     * @return the string
     */
    public static String toString(int operatingHour) {
	return integerToString.get(operatingHour);
    }
}
