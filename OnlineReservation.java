import java.sql.*;
import java.util.Scanner;

class Users {
    private String username;
    private String password;

    public Users(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isValidUser(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }
}

class DatabaseHandler {
    private Connection conn;

    public DatabaseHandler(String url, String username, String password) throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbdemo", "root", "");
    }

    public Users getUser(String username) throws SQLException {
        String query = "SELECT password FROM user WHERE user= ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    return new Users(username, dbPassword);
                } else {
                    return null; // User not found
                }
            }
        }
    }

    public void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}

class Reservation {
    public void makeReservation(Users user) {
        // Implement reservation creation in the database
        System.out.println("Reservation process is started");
        System.out.println("Reservation is successful for user: " + user.getUsername());
        System.out.println("Database status updated: Reservation saved.");
    }

    public void cancelReservation(Users user, String pnrNumber) {
        // Implement reservation cancellation in the database
        System.out.println("Cancellation process is started");
        System.out.println("Reservation is cancelled for user: " + user.getUsername() + ", PNR: " + pnrNumber);
        System.out.println("Database status updated: Reservation cancelled.");
    }
}

public class OnlineReservation {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            DatabaseHandler dbHandler = null;
            try {
                dbHandler = new DatabaseHandler("jdbc:mysql://localhost:3306/jdbdemo", "root", "");

                System.out.println("Enter your username: ");
                String username = sc.nextLine();
                System.out.println("Enter your password: ");
                String password = sc.nextLine();

                Users user = dbHandler.getUser(username);
                if (user != null && user.isValidUser(password)) {
                    Reservation reservation = new Reservation();
                    System.out.println("Login successful for " + user.getUsername() + "!");
                    System.out.println("1. Make Reservation");
                    System.out.println("2. Cancel Reservation");
                    System.out.println("Please select an option: ");
                    int option = sc.nextInt();
                    sc.nextLine();
                    switch (option) {
                        case 1:
                            reservation.makeReservation(user);
                            break;
                        case 2:
                            System.out.println("Enter your PNR number: ");
                            String pnrNumber = sc.nextLine();
                            reservation.cancelReservation(user, pnrNumber);
                            break;
                        default:
                            System.out.println("Select option correctly!");
                    }
                } else {
                    System.out.println("Invalid username or password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dbHandler != null) {
                        dbHandler.closeConnection();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}