package model; 

import java.awt.Color;

// Inheritance: EmergencyService mewarisi dari HospitalService
// dan juga mengimplementasikan QueueService untuk logika nomor antrian.
public class EmergencyService extends HospitalService implements QueueService {

    public EmergencyService(String name, String description, Color color) {
        super(name, description, color);
    }

    // Konstruktor praktis tanpa warna (gunakan warna merah default)
    public EmergencyService(String name, String description) {
        this(name, description, Color.RED);
    }

    // Polymorphism: Implementasi kustom untuk Gawat Darurat (selalu 'G')
    @Override
    public String getQueuePrefix() {
        return "G"; 
    }

    @Override
    public String generateQueueNumber(int lastNumber) {
        return getQueuePrefix() + String.format("%03d", lastNumber + 1);
    }

    @Override
    public boolean isServiceAvailable() {
        // IGD diasumsikan selalu tersedia
        return true;
    }
}
