package bean;

import java.util.Calendar;
import java.util.Date;

/**
 * The Enum Semester represent the different Semesters available.
 */
public enum Semester {

	Fall, Winter, Spring, Summer;

	/**
	 * Gets the semester starting closest to the provided date.
	 *
	 * @param date
	 *            the date
	 * @return the semester starting closest to the provided date
	 */
	public static Semester getSemesterStartingClosestTo(Date date) {
		Calendar calendar = Calendar.getInstance();
		Date current = calendar.getTime();
		Semester closest = Semester.values()[0];
		long closestDiff = Long.MAX_VALUE;
		for (Semester semester : Semester.values()) {
			long currentDiff = semester.getStartDate().getTime()
					- current.getTime();
			if (currentDiff > 0 && currentDiff < closestDiff) {
				closest = semester;
				closestDiff = currentDiff;
			}
		}
		return closest;
	}

	/**
	 * Gets the start date of this semester.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.MONTH) < Calendar.AUGUST) {
			calendar.add(Calendar.YEAR, -1);
		}
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);

		if (this == Winter) {
			calendar.add(Calendar.WEEK_OF_YEAR, 17);
		}
		if (this == Spring) {
			calendar.add(Calendar.WEEK_OF_YEAR, 21);
		} else if (this == Summer) {
			calendar.add(Calendar.WEEK_OF_YEAR, 38);
		}
		return calendar.getTime();
	}

	/**
	 * Gets the end date of this semester.
	 *
	 * @return the end date
	 */
	public Date getEndDate() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.MONTH) < Calendar.AUGUST) {
			calendar.add(Calendar.YEAR, -1);
		}
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);

		if (this == Fall) {
			calendar.add(Calendar.WEEK_OF_YEAR, 17);
		} else if (this == Winter) {
			calendar.add(Calendar.WEEK_OF_YEAR, 21);
		}
		if (this == Spring) {
			calendar.add(Calendar.WEEK_OF_YEAR, 38);
		} else if (this == Summer) {
			calendar.add(Calendar.WEEK_OF_YEAR, 50);
		}
		return calendar.getTime();
	}
}
