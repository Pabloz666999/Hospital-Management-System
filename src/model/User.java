package model;

public class User {
    private int id;
    private String name;
    
    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    // Metode Polimorfis: Deskripsi peran
    public String getRoleDescription() {
        return "Pengguna Dasar";
    }
}