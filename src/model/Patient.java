package model;

public class Patient {
    private int id;
    private String name;
    private String phoneNumber;
    private String identificationNumber;
    private Polyclinic targetPolyclinic;

    public Patient(int id, String name, String phoneNumber,
                   String identificationNumber, Polyclinic targetPolyclinic) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.identificationNumber = identificationNumber;
        this.targetPolyclinic = targetPolyclinic;
    }

    public Polyclinic getTargetPolyclinic() {
        return targetPolyclinic;
    }
}
