package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * DAO kecil untuk mengambil data agregasi yang tampil
 * di kartu statistik atas Dashboard Admin.
 */
public class DashboardStatsDao {

    public static class Overview {
        public final int totalPatients;
        public final int averageWaitMinutes;
        public final int activeCounters;
        public final int validQueues;

        public Overview(int totalPatients, int averageWaitMinutes,
                        int activeCounters, int validQueues) {
            this.totalPatients = totalPatients;
            this.averageWaitMinutes = averageWaitMinutes;
            this.activeCounters = activeCounters;
            this.validQueues = validQueues;
        }
    }

    public Overview loadOverview() {
        int totalPatients = 0;
        int averageWaitMinutes = 0;
        int activeCounters = 0;
        int validQueues = 0;

        try (Connection conn = DatabaseManager.getInstance().openConnection()) {
            // Total pasien terdaftar (seluruh waktu)
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM pasien");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalPatients = rs.getInt(1);
                }
            }

            // Rerata waktu tunggu (menit) untuk antrian hari ini
            // dihitung dari created_at ke waktu selesai / dipanggil.
            String avgSql = "SELECT AVG(TIMESTAMPDIFF(MINUTE, created_at, " +
                    "COALESCE(finished_at, called_at))) AS avg_wait " +
                    "FROM antrian " +
                    "WHERE queue_date = ? AND status IN ('DIPANGGIL','SELESAI')";
            try (PreparedStatement ps = conn.prepareStatement(avgSql)) {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getObject("avg_wait") != null) {
                        averageWaitMinutes = rs.getInt("avg_wait");
                    }
                }
            }

            // Jumlah loket aktif
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM loket WHERE is_active = 1");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    activeCounters = rs.getInt(1);
                }
            }

            // Total antrian valid (tidak dibatalkan) untuk hari ini
            String validSql = "SELECT COUNT(*) FROM antrian " +
                    "WHERE queue_date = ? AND status <> 'BATAL'";
            try (PreparedStatement ps = conn.prepareStatement(validSql)) {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        validQueues = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Overview(totalPatients, averageWaitMinutes, activeCounters, validQueues);
    }
}

