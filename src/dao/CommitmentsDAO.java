package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.CommitmentBean;

/**
 * The Class CommitmentsDao handles the interaction with the database dealing
 * with the table Commitments.
 * 
 * @author jmfoste2, lim92
 */
public class CommitmentsDAO extends DAO {

    /**
     * Instantiates a new commitments dao.
     *
     * @param conn
     *            the conn
     */
    public CommitmentsDAO(Connection conn) {
	super(conn);
    }

    /**
     * Inserts a commitment into the database.
     *
     * @param commitment
     *            the commitment
     * @throws SQLException
     *             the SQL exception
     */
    public void insert(CommitmentBean commitment) throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("INSERT INTO Commitments (id, startHour, endHour, commitmentDate, description) VALUES (?, ?, ?, ?, ?)");
	stmt.setInt(1, commitment.getClinicianID());
	stmt.setInt(2, commitment.getStartHour());
	stmt.setInt(3, commitment.getEndHour());
	stmt.setDate(4, new java.sql.Date(commitment.getDate().getTime()));
	stmt.setString(5, commitment.getDescription());
	stmt.execute();
	stmt.close();
    }

    /**
     * Load all the commitments associated with a particular clinician id from
     * the database.
     *
     * @param clinicianID
     *            the clinician id
     * @return the list
     * @throws SQLException
     *             the SQL exception
     */
    public List<CommitmentBean> loadCommitments(int clinicianID)
	    throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("SELECT startHour, endHour, commitmentDate, description FROM Commitments WHERE id = ?");
	stmt.setInt(1, clinicianID);
	stmt.execute();
	ResultSet results = stmt.getResultSet();

	List<CommitmentBean> commitments = new ArrayList<CommitmentBean>();
	while (results.next()) {
	    commitments.add(loadCommitment(clinicianID, results));
	}
	stmt.close();

	return commitments;
    }

    /**
     * Loads a commitment from a resultset.
     *
     * @param clinicianID
     *            the clinician id
     * @param results
     *            the results
     * @return the commitment
     * @throws SQLException
     *             the SQL exception
     */
    private CommitmentBean loadCommitment(int clinicianID, ResultSet results)
	    throws SQLException {
	int startHour = results.getInt("startHour");
	int endHour = results.getInt("endHour");
	Date date = results.getDate("commitmentDate");
	String description = results.getString("description");
	return new CommitmentBean(clinicianID, startHour, endHour, date,
		description);
    }

    /**
     * Delete the commitments associated with a particular clinicianID from the
     * database.
     *
     * @param clinicianID
     *            the clinician id
     * @throws SQLException
     *             the SQL exception
     */
    public void delete(int clinicianID) throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("DELETE FROM Commitments WHERE id = ?");
	stmt.setInt(1, clinicianID);
	stmt.execute();
	stmt.close();
    }
}
