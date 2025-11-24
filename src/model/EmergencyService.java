package model; 

import java.awt.Color;
// Inheritance: EmergencyService mewarisi dari HospitalService
public class EmergencyService extends HospitalService {

    public EmergencyService(String name, String description, Color color) {
        super(name, description, color);
    }

    // Polymorphism: Implementasi kustom untuk Gawat Darurat (selalu 'G')
    @Override
    public String getQueuePrefix() {
        return "G"; 
    }
}