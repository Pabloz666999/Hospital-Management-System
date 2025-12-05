package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PoliDao {

    public static class Poli {
        private final int id;
        private final String name;
        private final String description;
        private final String prefix;

        public Poli(int id, String name, String description, String prefix) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.prefix = prefix;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public List<Poli> findAll() {
        String sql = "SELECT poli_id, poli_name, description, queue_prefix FROM poli ORDER BY poli_id";
        List<Poli> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Poli(
                        rs.getInt("poli_id"),
                        rs.getString("poli_name"),
                        rs.getString("description"),
                        rs.getString("queue_prefix")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Poli findById(int id) {
        String sql = "SELECT poli_id, poli_name, description, queue_prefix FROM poli WHERE poli_id = ?";
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Poli(
                            rs.getInt("poli_id"),
                            rs.getString("poli_name"),
                            rs.getString("description"),
                            rs.getString("queue_prefix")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

