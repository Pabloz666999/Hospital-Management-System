package model;

import java.awt.Color;

public class Polyclinic extends HospitalService {
    
    public Polyclinic(String name, String description, Color color) {
        super(name, description, color);
    }

    // Polymorphism: Implementasi kustom untuk Poli (misal: Jantung -> J)
    @Override
    public String getQueuePrefix() {
        return getName().substring(0, 1).toUpperCase();
    }
}