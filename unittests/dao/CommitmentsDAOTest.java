package dao;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import validator.DateRangeValidator;
import bean.CommitmentBean;

/**
 * 
 * @author jmfoste2, lim92
 *
 */ 
public class CommitmentsDAOTest {

	private CommitmentsDAO commitmentsDAO;
	private Connection conn;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		commitmentsDAO = new CommitmentsDAO(conn);
		gen = new TestDataGenerator(conn);
		gen.clearCommitmentsTable();
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearCommitmentsTable();
	}
	
	@Test
	public void testInsertValidCommitment() throws Exception {
		CommitmentBean expected = new CommitmentBean(1, 4, 5, DateRangeValidator.parseDate("4/1/2015"), "Description");
		commitmentsDAO.insert(expected);
		
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(expected.getClinicianID());
		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments.add(expected);
		
		assertEquals(expectedCommitments, actualCommitments);
	}
	
	@Test
	public void testLoadCommitments() throws Exception {
		CommitmentBean commitmentBeanOne = new CommitmentBean(1, 8, 9, DateRangeValidator.parseDate("4/3/2015"), "desc");
		CommitmentBean commitmentBeanTwo = new CommitmentBean(1, 9, 10, DateRangeValidator.parseDate("4/3/2015"), "other desc");
		commitmentsDAO.insert(commitmentBeanOne);
		commitmentsDAO.insert(commitmentBeanTwo);
		
		List<CommitmentBean> actual = commitmentsDAO.loadCommitments(commitmentBeanOne.getClinicianID());
		List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
		expected.add(commitmentBeanOne);
		expected.add(commitmentBeanTwo);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoadCommitmentsEmpty() throws Exception {
		List<CommitmentBean> actual = commitmentsDAO.loadCommitments(1);
		List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteCommitment() throws Exception {
		CommitmentBean commitmentBean = new CommitmentBean(1, 8, 9, DateRangeValidator.parseDate("4/3/2015"), "desc");
		commitmentsDAO.insert(commitmentBean);
		commitmentsDAO.delete(commitmentBean.getClinicianID());
		
		List<CommitmentBean> actual = commitmentsDAO.loadCommitments(commitmentBean.getClinicianID());
		List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
		assertEquals(expected, actual);
	}
}
