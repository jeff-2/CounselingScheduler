package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.junit.Test;

import action.InvalidFormDataException;
import bean.Utility;

public class UtilityTest {

    private static final String FIELD = "field";
    private static final String CONTEXT = "context";

    @Test
    public void testToStringListEmpty() {
	DefaultListModel<String> model = new DefaultListModel<String>();
	List<String> expected = new ArrayList<String>();
	List<String> actual = Utility.toStringList(model);
	assertEquals(expected, actual);
    }

    @Test
    public void testToStringList() {
	DefaultListModel<String> model = new DefaultListModel<String>();
	List<String> expected = new ArrayList<String>();
	for (int i = 0; i < 10; i++) {
	    String element = "element" + i;
	    model.addElement(element);
	    expected.add(element);
	}
	List<String> actual = Utility.toStringList(model);
	assertEquals(expected, actual);
    }

    @Test
    public void testParseIntValid() throws InvalidFormDataException {
	int expected = 42;
	int actual = Utility.parseInt("42", FIELD, CONTEXT);
	assertEquals(expected, actual);
    }

    @Test
    public void testParseIntInvalid() {
	try {
	    Utility.parseInt("garbage235", FIELD, CONTEXT);
	    fail();
	} catch (InvalidFormDataException e) {
	    assertEquals("You must enter a valid integer for " + FIELD,
		    e.getMessage());
	    assertEquals(CONTEXT, e.getContext());
	}
    }

    @Test
    public void testParseValidTimeMorning() {
	int expected = 10;
	int actual = Utility.parseTime("10:30am", false);
	assertEquals(expected, actual);
    }

    @Test
    public void testParseValidTimeAfternoon() {
	int expected = 14;
	int actual = Utility.parseTime("2:00pm", false);
	assertEquals(expected, actual);
    }

    @Test
    public void testParseValidTimeMorningRoundUp() {
	int expected = 9;
	int actual = Utility.parseTime("8:30am", true);
	assertEquals(expected, actual);
    }

    @Test
    public void testParseValidTimeAfternoonRoundUp() {
	int expected = 15;
	int actual = Utility.parseTime("2:30pm", true);
	assertEquals(expected, actual);
    }
}
