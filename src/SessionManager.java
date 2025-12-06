import model.Admin;

public class SessionManager {

    private static boolean adminLoggedIn = false;
    private static String adminUsername = null;
    private static Admin currentAdmin = null;

    private SessionManager() {
    }

    public static void setAdminLoggedIn(String username) {
        adminLoggedIn = true;
        adminUsername = username;
        currentAdmin = null;
    }

    public static void setAdminLoggedIn(Admin admin) {
        if (admin != null) {
            adminLoggedIn = true;
            adminUsername = admin.getUsername();
            currentAdmin = admin;
        }
    }

    public static boolean isAdminLoggedIn() {
        return adminLoggedIn;
    }

    public static void logoutAdmin() {
        adminLoggedIn = false;
        adminUsername = null;
        currentAdmin = null;
    }

    public static String getAdminUsername() {
        return adminUsername;
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }
}
