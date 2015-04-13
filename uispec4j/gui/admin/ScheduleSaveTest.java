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
import java.sql.SQLException;

import org.uispec4j.Button;
import org.uispec4j.MenuBar;
import org.uispec4j.MenuItem;
import org.uispec4j.RadioButton;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;

import runner.AdminApplicationRunner;
import dao.ConnectionFactory;

public class ScheduleSaveTest extends UISpecTestCase {
	
	private TestDataGenerator gen;
	private Connection conn;
	
	protected void setUp() throws Exception {
		setAdapter(new MainClassAdapter(AdminApplicationRunner.class, new String[0]));
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
	}
	
	public void testSaveECScheduleOverwrite() throws IOException {
		String s = "Hello World!";
		byte data[] = s.getBytes();
		OutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get("tmp.png"), StandardOpenOption.CREATE, StandardOpenOption.APPEND));
		out.write(data, 0, data.length);
		out.close();
		
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		String filePath = new File("tmp.png").getAbsolutePath();
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton editEC = window.getRadioButton("Edit/print EC schedule");
		editEC.click();
		Window ecSchedule = WindowInterceptor.run(runTask.triggerClick());
		
		MenuBar menuBar = ecSchedule.getMenuBar();
		MenuItem menu = menuBar.getMenu("Options");
		MenuItem save = menu.getSubMenu("Save");
		WindowInterceptor
			.init(save.triggerClick())
			.process(FileChooserHandler.init()
			.titleEquals("Save Schedule")
			.assertAcceptsFilesOnly()
			.select(filePath))		
		.run();
		File f = new File(filePath);
		assertTrue(f.exists());
		assertTrue(f.getTotalSpace() > data.length);
	}
	
	public void testSaveECSchedule() {
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		String filePath = new File("tmp.png").getAbsolutePath();
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton editEC = window.getRadioButton("Edit/print EC schedule");
		editEC.click();
		Window ecSchedule = WindowInterceptor.run(runTask.triggerClick());
		
		MenuBar menuBar = ecSchedule.getMenuBar();
		MenuItem menu = menuBar.getMenu("Options");
		MenuItem save = menu.getSubMenu("Save");
		WindowInterceptor
			.init(save.triggerClick())
			.process(FileChooserHandler.init()
			.titleEquals("Save Schedule")
			.assertAcceptsFilesOnly()
			.select(filePath))
		.run();
		File f = new File(filePath);
		assertTrue(f.exists());
	}
	
	public void testSaveIASchedule() {
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		String filePath = new File("tmp.png").getAbsolutePath();
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton editEC = window.getRadioButton("Edit/print IA schedule");
		editEC.click();
		Window ecSchedule = WindowInterceptor.run(runTask.triggerClick());
		
		MenuBar menuBar = ecSchedule.getMenuBar();
		MenuItem menu = menuBar.getMenu("Options");
		MenuItem save = menu.getSubMenu("Save");
		WindowInterceptor
			.init(save.triggerClick())
			.process(FileChooserHandler.init()
			.titleEquals("Save Schedule")
			.assertAcceptsFilesOnly()
			.select(filePath))
		.run();
		File f = new File(filePath);
		assertTrue(f.exists());
	}
	
	public void testCancelSaveIASchedule() {
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		String filePath = new File("tmp.png").getAbsolutePath();
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton editEC = window.getRadioButton("Edit/print IA schedule");
		editEC.click();
		Window ecSchedule = WindowInterceptor.run(runTask.triggerClick());
		
		MenuBar menuBar = ecSchedule.getMenuBar();
		MenuItem menu = menuBar.getMenu("Options");
		MenuItem save = menu.getSubMenu("Save");
		WindowInterceptor
			.init(save.triggerClick())
			.process(FileChooserHandler.init()
			.titleEquals("Save Schedule")
			.assertAcceptsFilesOnly()
			.cancelSelection())
		.run();
		File f = new File(filePath);
		assertFalse(f.exists());
	}
	
	public void testCancelSaveECSchedule() {
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		String filePath = new File("tmp.png").getAbsolutePath();
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton editEC = window.getRadioButton("Edit/print EC schedule");
		editEC.click();
		Window ecSchedule = WindowInterceptor.run(runTask.triggerClick());
		
		MenuBar menuBar = ecSchedule.getMenuBar();
		MenuItem menu = menuBar.getMenu("Options");
		MenuItem save = menu.getSubMenu("Save");
		WindowInterceptor
			.init(save.triggerClick())
			.process(FileChooserHandler.init()
			.titleEquals("Save Schedule")
			.assertAcceptsFilesOnly()
			.cancelSelection())
		.run();
		File f = new File(filePath);
		assertFalse(f.exists());
	}
	
	protected void tearDown() throws SQLException {
		gen.clearTables();
		File f = new File("tmp.png");
		f.delete();
	}
}