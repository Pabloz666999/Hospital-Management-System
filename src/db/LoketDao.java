package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoketDao {

    public static class Loket {
        private final int id;
        private final String name;
        private final boolean active;

        public Loket(int id, String name, boolean active) {
            this.id = id;
            this.name = name;
            this.active = active;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }
    }

    public List<Loket> findAllActive() {
        String sql = "SELECT loket_id, loket_name, is_active FROM loket WHERE is_active = 1 ORDER BY loket_id";
        List<Loket> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Loket(
                        rs.getInt("loket_id"),
                        rs.getString("loket_name"),
                        rs.getInt("is_active") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}

