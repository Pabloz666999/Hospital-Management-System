package model;

public class Patient extends User {
    private String username; 
    private String password;
    private String phoneNumber;
    private String identificationNumber;
    
    public Patient(int id, String name, String username, String password, String phoneNumber, String identificationNumber) {
        super(id, name);
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.identificationNumber = identificationNumber;
    }

    public String getUsername() {
        return username;
    }
    
    // Metode untuk otentikasi
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    public String getPhoneNumber() { return phoneNumber; }
    public String getIdentificationNumber() { return identificationNumber; }

    @Override
    public String getRoleDescription() {
        return "Pasien Aktif";
    }
}