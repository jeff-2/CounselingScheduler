package bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class OperatingHours {
	
	private static final Map<String, Integer> stringToInteger;
	private static final Map<Integer, String> integerToString;
	
	static {
		Map<String, Integer> strToInt = new LinkedHashMap<String, Integer>();
		strToInt.put("8 am", 8);
		strToInt.put("9 am", 9);
		strToInt.put("10 am", 10);
		strToInt.put("11 am", 11);
		strToInt.put("12 pm", 12);
		strToInt.put("1 pm", 13);
		strToInt.put("2 pm", 14);
		strToInt.put("3 pm", 15);
		strToInt.put("4 pm", 16);
		strToInt.put("5 pm", 17);
		
		stringToInteger = Collections.unmodifiableMap(strToInt);
		
		Map<Integer, String> intToStr = new HashMap<Integer, String>();
	    for (Map.Entry<String, Integer> entry : stringToInteger.entrySet()) {
	    	intToStr.put(entry.getValue(), entry.getKey());
	    }
	    integerToString = Collections.unmodifiableMap(intToStr);
	}
	
	public static Set<String> getOperatingHours() {
		return stringToInteger.keySet();
	}
	
	public static int toInt(String operatingHour) {
		return stringToInteger.get(operatingHour);
	}
	
	public static String toString(int operatingHour) {
		return integerToString.get(operatingHour);
	}
}
