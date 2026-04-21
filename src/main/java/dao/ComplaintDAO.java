package dao;
import db.DBConnection;
import model.Complaint;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ComplaintDAO {
    public boolean fileComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (user_id, title, description, status, date) " +
                     "VALUES (?, ?, ?, 'Pending', NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaint.getUserId());
            ps.setString(2, complaint.getTitle());
            ps.setString(3, complaint.getDescription());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO] fileComplaint error: " + e.getMessage());
            return false;
        }
    }
    public List<Complaint> getComplaintsByUser(int userId) {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO] getComplaintsByUser error: " + e.getMessage());
        }
        return list;
    }
    public List<Complaint> getAllComplaints() {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.*, u.name AS user_name " +
                     "FROM complaints c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "ORDER BY c.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Complaint c = mapRow(rs);
                c.setUserName(rs.getString("user_name"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO] getAllComplaints error: " + e.getMessage());
        }
        return list;
    }
    public boolean updateStatus(int complaintId, String newStatus) {
        String sql = "UPDATE complaints SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, complaintId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO] updateStatus error: " + e.getMessage());
            return false;
        }
    }
    public Complaint searchById(int complaintId) {
        String sql = "SELECT c.*, u.name AS user_name " +
                     "FROM complaints c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Complaint c = mapRow(rs);
                    c.setUserName(rs.getString("user_name"));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO] searchById error: " + e.getMessage());
        }
        return null;
    }
    public boolean deleteComplaint(int complaintId) {
        String sql = "DELETE FROM complaints WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO] deleteComplaint error: " + e.getMessage());
            return false;
        }
    }
    private Complaint mapRow(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        c.setDate(rs.getString("date"));
        return c;
    }
}