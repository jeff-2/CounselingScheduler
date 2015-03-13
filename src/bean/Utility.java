package bean;

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
	
	public static List<CommitmentBean> toCommitmentList(ListModel<CommitmentBean> model) {
		List<CommitmentBean> cmts = new ArrayList<CommitmentBean>();
		for (int i = 0; i < model.getSize(); i++) {
			cmts.add(model.getElementAt(i));
		}
		return cmts;
	}
	
	public static List<TimeAwayBean> toTimeAwayList(ListModel<TimeAwayBean> model) {
		List<TimeAwayBean> tsAway = new ArrayList<TimeAwayBean>();
		for (int i = 0; i < model.getSize(); i++) {
			tsAway.add(model.getElementAt(i));
		}
		return tsAway;
	}
}
