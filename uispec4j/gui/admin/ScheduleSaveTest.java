package gui.admin;

import generator.TestDataGenerator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;

import org.uispec4j.MenuBar;
import org.uispec4j.MenuItem;
import org.uispec4j.TabGroup;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;

import runner.AdminApplicationRunner;
import dao.ConnectionFactory;

/**
 * The Class ScheduleSaveTest tests the save functionality for both IA and EC
 * schedules.
 */
public class ScheduleSaveTest extends UISpecTestCase {

	/** The gen. */
	private TestDataGenerator gen;

	/** The conn. */
	private Connection conn;

	/** The Constant filePath. */
	private static final String filePath = new File("tmp.png")
			.getAbsolutePath();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uispec4j.UISpecTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		setAdapter(new MainClassAdapter(AdminApplicationRunner.class,
				new String[0]));
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		gen.generateStandardDataset();
	}

	/**
	 * Test save EC schedule overwrites an existing saved schedule with the same
	 * name.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void testSaveECScheduleOverwrite() throws IOException {
		String s = "Hello World!";
		byte data[] = s.getBytes();
		OutputStream out = new BufferedOutputStream(Files.newOutputStream(
				Paths.get("tmp.png"), StandardOpenOption.CREATE,
				StandardOpenOption.APPEND));
		out.write(data, 0, data.length);
		out.close();

		Window window = this.getMainWindow();

		MenuBar menuBar = window.getMenuBar();
		MenuItem menu = menuBar.getMenu("File");
		MenuItem generate = menu.getSubMenu("Generate Schedule");
		MenuItem save = menu.getSubMenu("Save");
		generate.click();

		TabGroup tabbedPane = window.getTabGroup("tabbedPane");
		tabbedPane.selectTab("EC Schedule");

		WindowInterceptor
				.init(save.triggerClick())
				.process(
						FileChooserHandler.init().titleEquals("Save Schedule")
								.assertAcceptsFilesOnly().select(filePath))
				.run();
		File f = new File(filePath);
		assertTrue(f.exists());
		assertTrue(f.getTotalSpace() > data.length);
	}

	/**
	 * Tests saving an EC schedule.
	 */
	public void testSaveECSchedule() {
		Window window = this.getMainWindow();

		MenuBar menuBar = window.getMenuBar();
		MenuItem menu = menuBar.getMenu("File");
		MenuItem save = menu.getSubMenu("Save");
		MenuItem generate = menu.getSubMenu("Generate Schedule");
		generate.click();

		TabGroup tabbedPane = window.getTabGroup("tabbedPane");
		tabbedPane.selectTab("EC Schedule");

		WindowInterceptor
				.init(save.triggerClick())
				.process(
						FileChooserHandler.init().titleEquals("Save Schedule")
								.assertAcceptsFilesOnly().select(filePath))
				.run();
		File f = new File(filePath);
		assertTrue(f.exists());
	}

	/**
	 * Test saving an IA schedule.
	 */
	public void testSaveIASchedule() {
		Window window = this.getMainWindow();

		MenuBar menuBar = window.getMenuBar();
		MenuItem menu = menuBar.getMenu("File");
		MenuItem save = menu.getSubMenu("Save");
		MenuItem generate = menu.getSubMenu("Generate Schedule");
		generate.click();

		TabGroup tabbedPane = window.getTabGroup("tabbedPane");
		tabbedPane.selectTab("IA Schedule");

		WindowInterceptor
				.init(save.triggerClick())
				.process(
						FileChooserHandler.init().titleEquals("Save Schedule")
								.assertAcceptsFilesOnly().select(filePath))
				.run();
		File f = new File(filePath);
		assertTrue(f.exists());
	}

	/**
	 * Test cancel when saving an IA schedule.
	 */
	public void testCancelSaveIASchedule() {
		Window window = this.getMainWindow();

		MenuBar menuBar = window.getMenuBar();
		MenuItem menu = menuBar.getMenu("File");
		MenuItem save = menu.getSubMenu("Save");
		MenuItem generate = menu.getSubMenu("Generate Schedule");
		generate.click();

		TabGroup tabbedPane = window.getTabGroup("tabbedPane");
		tabbedPane.selectTab("IA Schedule");

		WindowInterceptor
				.init(save.triggerClick())
				.process(
						FileChooserHandler.init().titleEquals("Save Schedule")
								.assertAcceptsFilesOnly().cancelSelection())
				.run();
		File f = new File(filePath);
		assertFalse(f.exists());
	}

	/**
	 * Test canceling when saving an EC schedule.
	 */
	public void testCancelSaveECSchedule() {
		Window window = this.getMainWindow();

		MenuBar menuBar = window.getMenuBar();
		MenuItem menu = menuBar.getMenu("File");
		MenuItem save = menu.getSubMenu("Save");
		MenuItem generate = menu.getSubMenu("Generate Schedule");
		generate.click();

		TabGroup tabbedPane = window.getTabGroup("tabbedPane");
		tabbedPane.selectTab("EC Schedule");

		WindowInterceptor
				.init(save.triggerClick())
				.process(
						FileChooserHandler.init().titleEquals("Save Schedule")
								.assertAcceptsFilesOnly().cancelSelection())
				.run();
		File f = new File(filePath);
		assertFalse(f.exists());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uispec4j.UISpecTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		gen.clearTables();
		File f = new File(filePath);
		f.delete();
	}
}
