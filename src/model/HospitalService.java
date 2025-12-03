package model;

import java.awt.Color;

public abstract class HospitalService {
    private String name;
    private String description;
    private Color color;

    public HospitalService(String name, String description, Color color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    public abstract String getQueuePrefix();

    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }
}