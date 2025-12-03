package model;

import java.awt.Color;

public class Polyclinic extends HospitalService implements QueueService {

    public Polyclinic(String name, String description, Color color) {
        super(name, description, color);
    }

    @Override
    public String getQueuePrefix() {
        return getName().substring(0, 1).toUpperCase();
    }

    @Override
    public String generateQueueNumber(int lastNumber) {
        return getQueuePrefix() + String.format("%03d", lastNumber + 1);
    }

    @Override
    public boolean isServiceAvailable() {
        return true; 
    }
}
