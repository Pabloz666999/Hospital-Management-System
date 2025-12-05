package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private final ExecutorService executor;

    private DatabaseManager() {
        this.executor = Executors.newFixedThreadPool(4);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public Future<?> submitAsync(Runnable task) {
        return executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
