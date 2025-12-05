package model;

public class Patient extends User {
    private String phoneNumber;
    private String identificationNumber;
    private Polyclinic targetPolyclinic;

    public Patient(int id, String name, String phoneNumber, String identificationNumber, Polyclinic targetPolyclinic) {
        super(id, name);
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.identificationNumber = identificationNumber;
        this.targetPolyclinic = targetPolyclinic;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getidentificationNumber() {
        return identificationNumber;
    }

    public Polyclinic getTargetPolyclinic() {
        return targetPolyclinic;
    }
}

