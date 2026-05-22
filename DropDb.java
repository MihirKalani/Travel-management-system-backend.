import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DropDb {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", "root", "mihir@2004");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP DATABASE IF EXISTS travel_management_system");
            stmt.executeUpdate("CREATE DATABASE travel_management_system");
            System.out.println("Database reset successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
