package db;

import model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDao {

    public boolean authenticate(String username, String password) {
        return findByCredentials(username, password) != null;
    }

    public Admin findByCredentials(String username, String password) {
        String sql = "SELECT admin_id, full_name, username, password " +
                "FROM admin WHERE username = ? AND password = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("admin_id");
                    String fullName = rs.getString("full_name");
                    String user = rs.getString("username");
                    String pass = rs.getString("password");
                    return new Admin(id, fullName, user, pass);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
