package db;

import model.EmergencyService;
import model.Polyclinic;
import model.QueueService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class QueueDao {

    public static class PatientData {
        public final String fullName;
        public final String phone;
        public final String nationalId;
        public final Integer age;
        public final String gender;
        public final String address;
        public final java.util.Date birthDate;

        public PatientData(String fullName, String phone, String nationalId,
                           Integer age, String gender, String address, java.util.Date birthDate) {
            this.fullName = fullName;
            this.phone = phone;
            this.nationalId = nationalId;
            this.age = age;
            this.gender = gender;
            this.address = address;
            this.birthDate = birthDate;
        }
    }

    public static class NowServingData {
        public final String nomorAntrian;
        public final String poliName;
        public final String loketName;

        public NowServingData(String nomorAntrian, String poliName, String loketName) {
            this.nomorAntrian = nomorAntrian;
            this.poliName = poliName;
            this.loketName = loketName;
        }
    }

    public static class QueueItem {
        public final long antrianId;
        public final String nomorAntrian;
        public final String poliName;
        public final String patientName;
        public final String status;
        public final Timestamp createdAt;
        public final Timestamp calledAt;
        public final Timestamp finishedAt;

        public QueueItem(long antrianId, String nomorAntrian, String poliName, String patientName,
                         String status, Timestamp createdAt,
                         Timestamp calledAt, Timestamp finishedAt) {
            this.antrianId = antrianId;
            this.nomorAntrian = nomorAntrian;
            this.poliName = poliName;
            this.patientName = patientName;
            this.status = status;
            this.createdAt = createdAt;
            this.calledAt = calledAt;
            this.finishedAt = finishedAt;
        }
    }

    public static class CounterItem {
        public final String loketName;
        public final String nomorAntrian;

        public CounterItem(String loketName, String nomorAntrian) {
            this.loketName = loketName;
            this.nomorAntrian = nomorAntrian;
        }
    }

    public static class PoliCount {
        public final String poliName;
        public final int total;

        public PoliCount(String poliName, int total) {
            this.poliName = poliName;
            this.total = total;
        }
    }

    public String registerNewQueue(PatientData patient, PoliDao.Poli poli) {
        if (poli == null || patient == null) {
            return null;
        }

        try (Connection conn = DatabaseManager.getInstance().openConnection()) {
            conn.setAutoCommit(false);
            long patientId = findOrCreatePatient(conn, patient);

            // Bentuk objek layanan berbasis model OOP (HospitalService + QueueService)
            QueueService service;
            String poliName = poli.getName() != null ? poli.getName() : "";
            String description = poli.getDescription();
            String prefixFromDb = poli.getPrefix();

            if (poliName.toLowerCase().contains("igd")) {
                // IGD menggunakan EmergencyService dengan prefix tetap 'G'
                service = new EmergencyService(poliName, description);
            } else {
                // Poli umum menggunakan Polyclinic, prefix diambil dari database jika ada
                service = new Polyclinic(poliName, description, prefixFromDb);
            }

            LocalDate today = LocalDate.now();
            int lastNumber = 0;
            String countSql = "SELECT COUNT(*) FROM antrian WHERE poli_id = ? AND queue_date = ?";
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                ps.setInt(1, poli.getId());
                ps.setDate(2, Date.valueOf(today));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        lastNumber = rs.getInt(1);
                    }
                }
            }

            // Nomor antrian dihasilkan oleh QueueService (polymorphism)
            String nomorAntrian = service.generateQueueNumber(lastNumber);

            String insertQueue = "INSERT INTO antrian " +
                    "(nomor_antrian, queue_date, queue_time, status, pasien_id, poli_id, loket_id, admin_id) " +
                    "VALUES (?, ?, ?, 'MENUNGGU', ?, ?, NULL, NULL)";
            try (PreparedStatement ps = conn.prepareStatement(insertQueue)) {
                ps.setString(1, nomorAntrian);
                ps.setDate(2, Date.valueOf(today));
                ps.setTime(3, Time.valueOf(LocalTime.now()));
                ps.setLong(4, patientId);
                ps.setInt(5, poli.getId());
                ps.executeUpdate();
            }

            conn.commit();
            return nomorAntrian;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private long findOrCreatePatient(Connection conn, PatientData patient) throws SQLException {
        Long existingId = null;
        if (patient.nationalId != null && !patient.nationalId.trim().isEmpty()) {
            String findSql = "SELECT pasien_id FROM pasien WHERE national_id = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setString(1, patient.nationalId.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getLong("pasien_id");
                    }
                }
            }
        }

        if (existingId != null) {
            return existingId;
        }

        String insertSql = "INSERT INTO pasien " +
                "(full_name, phone_num, national_id, age, gender, address, birth_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, patient.fullName);
            ps.setString(2, patient.phone);
            ps.setString(3, patient.nationalId);
            if (patient.age != null) {
                ps.setInt(4, patient.age);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, patient.gender);
            ps.setString(6, patient.address);
            if (patient.birthDate != null) {
                ps.setDate(7, new Date(patient.birthDate.getTime()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Gagal menyimpan data pasien");
    }

    public NowServingData loadNowServing() {
        String sql = "SELECT a.nomor_antrian, p.poli_name, l.loket_name " +
                "FROM antrian a " +
                "LEFT JOIN poli p ON a.poli_id = p.poli_id " +
                "LEFT JOIN loket l ON a.loket_id = l.loket_id " +
                "WHERE a.status = 'DIPANGGIL' " +
                "ORDER BY a.called_at DESC, a.queue_date DESC, a.queue_time DESC " +
                "LIMIT 1";
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new NowServingData(
                        rs.getString("nomor_antrian"),
                        rs.getString("poli_name"),
                        rs.getString("loket_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<QueueItem> loadWaitingQueue(int limit) {
        String sql = "SELECT a.antrian_id, a.nomor_antrian, p.poli_name, s.full_name AS patient_name, " +
                "a.status, a.created_at, a.called_at, a.finished_at " +
                "FROM antrian a " +
                "JOIN pasien s ON a.pasien_id = s.pasien_id " +
                "JOIN poli p ON a.poli_id = p.poli_id " +
                "WHERE a.status = 'MENUNGGU' " +
                "ORDER BY a.queue_date, a.queue_time " +
                "LIMIT ?";
        List<QueueItem> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new QueueItem(
                            rs.getLong("antrian_id"),
                            rs.getString("nomor_antrian"),
                            rs.getString("poli_name"),
                            rs.getString("patient_name"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("called_at"),
                            rs.getTimestamp("finished_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<CounterItem> loadCounterSummary() {
        String sql = "SELECT l.loket_name, " +
                "(SELECT a.nomor_antrian FROM antrian a " +
                " WHERE a.loket_id = l.loket_id AND a.status = 'DIPANGGIL' " +
                " ORDER BY a.called_at DESC, a.queue_date DESC, a.queue_time DESC LIMIT 1) AS nomor_antrian " +
                "FROM loket l ORDER BY l.loket_id";
        List<CounterItem> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new CounterItem(
                        rs.getString("loket_name"),
                        rs.getString("nomor_antrian")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<QueueItem> loadRecentActivities(int limit) {
        String sql = "SELECT a.antrian_id, a.nomor_antrian, p.poli_name, s.full_name AS patient_name, " +
                "a.status, a.created_at, a.called_at, a.finished_at " +
                "FROM antrian a " +
                "JOIN pasien s ON a.pasien_id = s.pasien_id " +
                "JOIN poli p ON a.poli_id = p.poli_id " +
                "ORDER BY a.created_at DESC " +
                "LIMIT ?";
        List<QueueItem> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new QueueItem(
                            rs.getLong("antrian_id"),
                            rs.getString("nomor_antrian"),
                            rs.getString("poli_name"),
                            rs.getString("patient_name"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("called_at"),
                            rs.getTimestamp("finished_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<PoliCount> loadPoliCountsToday() {
        String sql = "SELECT p.poli_name, COUNT(*) AS total " +
                "FROM antrian a " +
                "JOIN poli p ON a.poli_id = p.poli_id " +
                "WHERE a.queue_date = ? " +
                "GROUP BY p.poli_name " +
                "ORDER BY total DESC";
        List<PoliCount> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new PoliCount(
                            rs.getString("poli_name"),
                            rs.getInt("total")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean hasActiveForLoket(int loketId) {
        String sql = "SELECT 1 FROM antrian WHERE loket_id = ? AND status = 'DIPANGGIL' LIMIT 1";
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loketId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public QueueItem callNextForLoket(int loketId) {
        String selectSql = "SELECT a.antrian_id, a.nomor_antrian, p.poli_name, s.full_name AS patient_name, " +
                "a.status, a.created_at, a.called_at, a.finished_at " +
                "FROM antrian a " +
                "JOIN pasien s ON a.pasien_id = s.pasien_id " +
                "JOIN poli p ON a.poli_id = p.poli_id " +
                "WHERE a.status = 'MENUNGGU' " +
                "ORDER BY a.queue_date, a.queue_time " +
                "LIMIT 1";

        try (Connection conn = DatabaseManager.getInstance().openConnection()) {
            conn.setAutoCommit(false);

            Long antrianId = null;
            QueueItem item = null;

            try (PreparedStatement ps = conn.prepareStatement(selectSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    antrianId = rs.getLong("antrian_id");
                    item = new QueueItem(
                            antrianId,
                            rs.getString("nomor_antrian"),
                            rs.getString("poli_name"),
                            rs.getString("patient_name"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("called_at"),
                            rs.getTimestamp("finished_at")
                    );
                }
            }

            if (antrianId == null) {
                conn.rollback();
                return null;
            }

            String updateSql = "UPDATE antrian SET status = 'DIPANGGIL', loket_id = ?, called_at = ? " +
                    "WHERE antrian_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, loketId);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setLong(3, antrianId);
                ps.executeUpdate();
            }

            conn.commit();
            return item;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean markFinishedByNomor(String nomorAntrian) {
        String sql = "UPDATE antrian SET status = 'SELESAI', finished_at = ? " +
                "WHERE nomor_antrian = ? AND status <> 'BATAL'";
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, nomorAntrian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markCancelledByNomor(String nomorAntrian) {
        String sql = "UPDATE antrian SET status = 'BATAL', canceled_at = ? " +
                "WHERE nomor_antrian = ?";
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, nomorAntrian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean recallByNomor(String nomorAntrian) {
        String sql = "UPDATE antrian SET called_at = ? " +
                "WHERE nomor_antrian = ? AND status = 'DIPANGGIL'";
        try (Connection conn = DatabaseManager.getInstance().openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, nomorAntrian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
