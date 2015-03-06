package forms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ListModel;

/**
 * Contains commonly used static methods
 * @author Yusheng Hou and Kevin Lim
 *
 */
public final class Utility {
	private static SimpleDateFormat dateFormat;
	
	static {
		dateFormat = new SimpleDateFormat("EEEE, MMMM d, Y");
	}
	
	/**
	 * Formats the date as a string in "Dayofweek, month date, year" format
	 * @param date
	 * @return date formatted as string
	 */
	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}
	
	public static List<Commitment> toCommitmentList(ListModel<Commitment> model) {
		List<Commitment> cmts = new ArrayList<Commitment>();
		for (int i = 0; i < model.getSize(); i++) {
			cmts.add(model.getElementAt(i));
		}
		return cmts;
	}
	
	public static List<TimeAway> toTimeAwayList(ListModel<TimeAway> model) {
		List<TimeAway> tsAway = new ArrayList<TimeAway>();
		for (int i = 0; i < model.getSize(); i++) {
			tsAway.add(model.getElementAt(i));
		}
		return tsAway;
	}
}
