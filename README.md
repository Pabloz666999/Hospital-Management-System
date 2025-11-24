# 🧠 THE PATRIOTS NETWORK

### Multithreading + JDBC Simulation (Metal Gear Solid Themed)

Proyek ini adalah simulasi jaringan **AI The Patriots** dari _Metal Gear Solid_,
dibuat menggunakan **Java Thread** dan **JDBC (MySQL)**.  
Setiap AI (GW, TJ, AL, TR, JD) berjalan sebagai **thread terpisah** yang membaca data
dari database MySQL secara paralel.

---

## 🚀 Tujuan Proyek

- Mempraktikkan **multithreading** di Java dengan class `Thread`.
- Menggunakan **JDBC** untuk koneksi ke database MySQL.
- Menampilkan **eksekusi paralel** antar-thread dengan _timestamp log_.
- Membuat simulasi bertema Metal Gear Solid untuk pembelajaran yang seru ⚡

---

## 💾 Database Setup

Gunakan MySQL (misalnya lewat XAMPP), lalu jalankan perintah SQL berikut:

````sql
CREATE DATABASE patriots_db;
USE patriots_db;

CREATE TABLE ai_nodes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20),
    region VARCHAR(30),
    status VARCHAR(20)
);

INSERT INTO ai_nodes (name, region, status) VALUES
('GW', 'North America', 'Active'),
('TJ', 'South America', 'Active'),
('AL', 'Europe', 'Standby'),
('TR', 'Asia', 'Active'),
('JD', 'Orbit', 'Controlling');

Pastikan MySQL service sudah berjalan di localhost:3306.

⚙️ Konfigurasi Database

Buka file DBUtil.java dan pastikan konfigurasi sesuai:
```java
private static final String URL = "jdbc:mysql://localhost:3306/patriots_db";
private static final String USER = "root";
private static final String PASS = ""; // ubah jika kamu pakai password
````

---

🧱 Menjalankan Program
1️⃣ Tambahkan Library JDBC

Pastikan mysql-connector-j-8.x.x.jar sudah ada di folder lib/.

2️⃣ Kompilasi & Jalankan

Jika lewat terminal:

```bash
javac -cp ".;lib/mysql-connector-j-8.4.0.jar" src/*.java
java  -cp ".;lib/mysql-connector-j-8.4.0.jar;src" Main
```

Gunakan : alih-alih ; di macOS/Linux.

Atau jika lewat VS Code:

Tambahkan lib ke referenced libraries di settings.json:

```json
"java.project.referencedLibraries": [
    "lib/**/*.jar"
]
```

## Jalankan langsung Main.java.

🧩 Contoh Output

```yaml
=== THE PATRIOTS NETWORK ===
Central Core: All AI nodes are operational...

[2025-11-05 11:31:43] AI JD reading node: [GW] (North America) - Active
[2025-11-05 11:31:43] AI AL reading node: [GW] (North America) - Active
[2025-11-05 11:31:43] AI TR reading node: [GW] (North America) - Active
[2025-11-05 11:31:43] AI TJ reading node: [GW] (North America) - Active
[2025-11-05 11:31:43] AI GW reading node: [GW] (North America) - Active
[2025-11-05 11:31:44] AI JD reading node: [TJ] (South America) - Active
[2025-11-05 11:31:44] AI TJ reading node: [TR] (Asia) - Active
...
[2025-11-05 11:31:48] AI GW finished scanning database.
[2025-11-05 11:31:48] AI TJ finished scanning database.
[2025-11-05 11:31:48] AI JD finished scanning database.
[2025-11-05 11:31:48] AI TR finished scanning database.
[2025-11-05 11:31:48] AI AL finished scanning database.
```

## ⚡ Urutan output bisa berbeda tiap eksekusi — itulah bukti bahwa program ini berjalan secara multithreaded.

🧠 Konsep Utama
|Konsep| Penjelasan|
|Thread| Representasi AI node (GW, TJ, AL, TR, JD)|
|JDBC| Menghubungkan Java ke MySQL|
|AINode| Model data untuk tiap AI|
|AINodeDAO| Mengambil data dari database|
|toString()| Format deskripsi AI yang dibaca|
|LocalDateTime| Menampilkan timestamp eksekusi tiap thread|
