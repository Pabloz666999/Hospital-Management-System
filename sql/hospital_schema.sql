-- Schema basis untuk sistem antrian "Ruang Sehat"
-- Sesuai konsep ERD: Admin, Pasien, Poli, Antrian (+ Loket)

-- Buat database (opsional, sesuaikan dengan konfigurasi aplikasi)
CREATE DATABASE IF NOT EXISTS hospital_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE hospital_db;

-- =========================
--  Tabel Admin (Petugas)
-- =========================
CREATE TABLE IF NOT EXISTS admin (
    admin_id   INT AUTO_INCREMENT PRIMARY KEY,
    full_name  VARCHAR(100) NOT NULL,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL, -- bisa diganti hash password
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
--  Tabel Pasien
-- =========================
CREATE TABLE IF NOT EXISTS pasien (
    pasien_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    phone_num   VARCHAR(20),
    national_id VARCHAR(30),        -- NIK / nomor identitas
    age         INT,
    gender      VARCHAR(10),        -- contoh: 'L', 'P'
    address     VARCHAR(255),
    birth_date  DATE,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_pasien_national UNIQUE (national_id)
);

-- =========================
--  Tabel Poli / Layanan
-- =========================
CREATE TABLE IF NOT EXISTS poli (
    poli_id      INT AUTO_INCREMENT PRIMARY KEY,
    poli_name    VARCHAR(100) NOT NULL,
    description  VARCHAR(255),
    queue_prefix VARCHAR(5)   -- contoh: 'A', 'B', 'C'
);

-- =========================
--  Tabel Loket
-- =========================
CREATE TABLE IF NOT EXISTS loket (
    loket_id   INT AUTO_INCREMENT PRIMARY KEY,
    loket_name VARCHAR(50) NOT NULL,   -- contoh: 'Loket 1', 'Loket 2'
    poli_id    INT NULL,
    is_active  TINYINT(1) NOT NULL DEFAULT 1,

    CONSTRAINT fk_loket_poli
        FOREIGN KEY (poli_id) REFERENCES poli (poli_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- =========================
--  Tabel Antrian
-- =========================
CREATE TABLE IF NOT EXISTS antrian (
    antrian_id  BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Nomor antrian yang tampil di layar, contoh: 'A045'
    nomor_antrian VARCHAR(10) NOT NULL,

    queue_date  DATE NOT NULL,  -- tanggal daftar
    queue_time  TIME NOT NULL,  -- jam daftar

    status      ENUM('MENUNGGU','DIPANGGIL','SELESAI','BATAL')
                NOT NULL DEFAULT 'MENUNGGU',

    pasien_id   BIGINT NOT NULL,
    poli_id     INT    NOT NULL,
    loket_id    INT    NULL,    -- loket yang melayani
    admin_id    INT    NULL,    -- petugas yang mendaftarkan

    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    called_at   DATETIME NULL,
    finished_at DATETIME NULL,
    canceled_at DATETIME NULL,

    CONSTRAINT fk_antrian_pasien
        FOREIGN KEY (pasien_id) REFERENCES pasien (pasien_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_antrian_poli
        FOREIGN KEY (poli_id) REFERENCES poli (poli_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_antrian_loket
        FOREIGN KEY (loket_id) REFERENCES loket (loket_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,

    CONSTRAINT fk_antrian_admin
        FOREIGN KEY (admin_id) REFERENCES admin (admin_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,

    INDEX idx_antrian_status (status),
    INDEX idx_antrian_tanggal (queue_date, queue_time),
    INDEX idx_antrian_nomor (nomor_antrian)
);

-- =========================
--  Data awal (seed) opsional
-- =========================

-- Admin default (untuk login awal, bisa diganti).
INSERT INTO admin (full_name, username, password)
VALUES ('Administrator', 'admin', 'admin123')
ON DUPLICATE KEY UPDATE username = username;

-- Beberapa poli contoh
INSERT INTO poli (poli_name, description, queue_prefix) VALUES
    ('Poli Umum',      'Pelayanan umum',       'A'),
    ('Poli Anak',      'Pelayanan anak',       'B'),
    ('Poli Jantung',   'Kardiologi',           'C'),
    ('IGD',            'Instalasi Gawat Darurat', 'D'),
    ('Laboratorium',   'Pemeriksaan laboratorium', 'E'),
    ('Radiologi',      'Pemeriksaan radiologi',    'F'),
    ('Farmasi',        'Pengambilan obat',         'G')
ON DUPLICATE KEY UPDATE poli_name = poli_name;

-- Loket contoh
INSERT INTO loket (loket_name, poli_id, is_active) VALUES
    ('Loket 1', NULL, 1),
    ('Loket 2', NULL, 1),
    ('Loket 3', NULL, 1),
    ('Loket 4', NULL, 1),
    ('Loket 5', NULL, 1);

