package model;

import java.awt.Color;

public class Polyclinic extends HospitalService implements QueueService {

    // Prefix khusus dari database (boleh null)
    private String customPrefix;

    public Polyclinic(String name, String description, Color color) {
        super(name, description, color);
    }

    public Polyclinic(String name, String description, Color color, String customPrefix) {
        super(name, description, color);
        this.customPrefix = customPrefix;
    }

    // Konstruktor praktis tanpa warna (gunakan warna default)
    public Polyclinic(String name, String description, String customPrefix) {
        this(name, description, Color.GRAY, customPrefix);
    }

    public Polyclinic(String name, String description) {
        this(name, description, Color.GRAY, null);
    }

    @Override
    public String getQueuePrefix() {
        if (customPrefix != null && !customPrefix.isEmpty()) {
            return customPrefix.toUpperCase();
        }
        return getName().substring(0, 1).toUpperCase();
    }

    @Override
    public String generateQueueNumber(int lastNumber) {
        // Gunakan nomor terakhir dari database, lalu tambah 1
        return getQueuePrefix() + String.format("%03d", lastNumber + 1);
    }

    @Override
    public boolean isServiceAvailable() {
        // Untuk saat ini semua poli dianggap tersedia
        return true; 
    }
}
