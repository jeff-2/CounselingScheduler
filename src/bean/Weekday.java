package bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Enum Weekday represents the weekdays in a week. Provides utilities for
 * converting to and from different representations (e.g. string, date) to a
 * Weekday and back.
 *
 * @author jmfoste2, lim92
 */
public enum Weekday {
    Monday, Tuesday, Wednesday, Thursday, Friday;

    /** The Constant format. */
    private static final SimpleDateFormat format = new SimpleDateFormat(
	    "EEEEEEEEE");

    /**
     * Gets the weekday represented by a particular date. Throws
     * IllegalArgumentException if the provided date is not a weekday
     *
     * @param d
     *            the d
     * @return the weekday
     */
    public static Weekday getWeekday(Date d) {
	if (!Weekday.isWeekday(d)) {
	    throw new IllegalArgumentException(
		    "Weekdays are not Saturday/Sunday");
	}
	return Weekday.valueOf(format.format(d));
    }

    /**
     * Checks if the provided date is a weekday.
     *
     * @param d
     *            the d
     * @return true, if is weekday
     */
    public static boolean isWeekday(Date d) {
	String day = format.format(d).trim();
	return !(day.equals("Saturday") || day.equals("Sunday"));
    }

    /**
     * Returns string representation of the Weekday provided by the given date.
     *
     * @param d
     *            the d
     * @return the string
     */
    public static String dayName(Date d) {
	return format.format(d);
    }

    /**
     * Returns true if the name of a weekday is contained in the given string.
     *
     * @param value
     *            the value
     * @return true, if is contained in
     */
    public static boolean isContainedIn(String value) {
	for (Weekday w : Weekday.values()) {
	    if (value.contains(w.name())) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns true if the provided value is equal to the name of a weekday.
     *
     * @param value
     *            the value
     * @return true, if successful
     */
    public static boolean contains(String value) {
	for (Weekday w : Weekday.values()) {
	    if (w.name().equals(value)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Converts this ordinal to the int constant used with calendar to represent
     * weekdays.
     *
     * @return the int
     */
    public int toCalendarWeekday() {
	return ordinal() + 2;
    }

    /**
     * Returns the index of the day name (ignoring case): Monday=0, Tuesday=1,
     * etc.
     *
     * @param dayName
     *            the day name
     * @return the index of day
     */
    public static int getIndexOfDay(String dayName) {
	String lower = dayName.toLowerCase();
	if (lower.equals("monday")) {
	    return 0;
	}
	if (lower.equals("tuesday")) {
	    return 1;
	}
	if (lower.equals("wednesday")) {
	    return 2;
	}
	if (lower.equals("thursday")) {
	    return 3;
	}
	if (lower.equals("friday")) {
	    return 4;
	}
	return -1;
    }
}
