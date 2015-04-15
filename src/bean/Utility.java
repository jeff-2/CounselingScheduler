package bean;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;

/**
 * Contains commonly used static methods
 * @author Yusheng Hou and Kevin Lim
 *
 */
public final class Utility {
	public static List<String> toStringList(ListModel<String> model) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < model.getSize(); i++) {
			list.add(model.getElementAt(i));
		}
		return list;
	}	
}
