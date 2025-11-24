package model;

public class Admin extends User {
    private String username;
    private String password;

    public Admin(int id, String name, String username, String password) {
        super(id, name);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    @Override
    public String getRoleDescription() {
        return "Petugas Loket Rumah Sakit";
    }
}