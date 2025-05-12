import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerService {

    // Insert customer into the Customers table
    public static String insertCustomer(String firstName, String lastName, String email, String phone, String address) {
        String query = "INSERT INTO Customers (FirstName, LastName, Email, Phone, Address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);

            stmt.executeUpdate();
            return "Customer added successfully.";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    // Delete a customer from the Customers table by CustomerID
    public static String deleteCustomer(int customerId) {
        String query = "DELETE FROM Customers WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? "Customer deleted successfully." : "Customer not found.";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }
}
