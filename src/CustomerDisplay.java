
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class CustomerDisplay extends Application {

    private TextField firstNameField;
    private TextField lastNameField;
    private Button searchButton;
    private Button updateCustomerButton;
    private Button newCustomerButton;
    
    private Button deleteCustomerButton;
    private TextArea resultArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Input fields for customer name
        firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        // Search button
        searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchCustomerByFullName());

        // New Customer button
        newCustomerButton = new Button("Add New Customer");
        newCustomerButton.setOnAction(e -> openNewCustomerWindow());

        // Update Customer button
        updateCustomerButton = new Button("Update Customer");
        updateCustomerButton.setOnAction(e -> openUpdateCustomerWindow());
        
        // Delete Customer button
        deleteCustomerButton = new Button("Delete Customer");
        deleteCustomerButton.setOnAction(e -> deleteCustomer());

        // Result area for displaying customer details
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPromptText("Customer details will be displayed here.");

        // Layout setup
        VBox layout = new VBox(10, firstNameField, lastNameField, searchButton, resultArea, newCustomerButton,updateCustomerButton, deleteCustomerButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 350);
        primaryStage.setTitle("Customer Search");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to search for a customer by full name
    private void searchCustomerByFullName() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            resultArea.setText("Please enter both first and last names.");
            return;
        }

        String query = "SELECT * FROM Customers WHERE FirstName = ? AND LastName = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);

            ResultSet rs = pstmt.executeQuery();
            resultArea.clear();

            if (rs.next()) {
                resultArea.appendText("CustomerID: " + rs.getInt("CustomerID") + "\n");
                resultArea.appendText("First Name: " + rs.getString("FirstName") + "\n");
                resultArea.appendText("Last Name: " + rs.getString("LastName") + "\n");
                resultArea.appendText("Email: " + rs.getString("Email") + "\n");
                resultArea.appendText("Phone: " + rs.getString("Phone") + "\n");
                resultArea.appendText("Address: " + rs.getString("Address") + "\n");
                resultArea.appendText("Date of Birth: " + rs.getDate("DateOfBirth") + "\n");
                resultArea.appendText("Registration Date: " + rs.getTimestamp("RegistrationDate") + "\n");
            } else {
                resultArea.setText("Customer not found.");
            }
        } catch (SQLException e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
    
 // Open a new window for updating a customer
    private void openUpdateCustomerWindow() {
        // Get customer ID from the result area (or prompt for it)
        String customerId = resultArea.getText().split("\n")[0].replace("CustomerID: ", "").trim();
        if (customerId.isEmpty()) {
            resultArea.setText("Please search for a customer first.");
            return;
        }

        // Fetch existing details of the customer
        String query = "SELECT * FROM Customers WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(customerId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create a new window for updating customer details
                Stage window = new Stage();
                window.setTitle("Update Customer");

                // Create input fields for customer details
                TextField firstNameField = new TextField(rs.getString("FirstName"));
                firstNameField.setPromptText("First Name");

                TextField lastNameField = new TextField(rs.getString("LastName"));
                lastNameField.setPromptText("Last Name");

                TextField emailField = new TextField(rs.getString("Email"));
                emailField.setPromptText("Email");

                TextField phoneField = new TextField(rs.getString("Phone"));
                phoneField.setPromptText("Phone");

                TextField addressField = new TextField(rs.getString("Address"));
                addressField.setPromptText("Address");

                DatePicker dateOfBirthPicker = new DatePicker(rs.getDate("DateOfBirth").toLocalDate());
                dateOfBirthPicker.setPromptText("Date of Birth");

                Button updateButton = new Button("Update Customer");

                updateButton.setOnAction(e -> {
                    String firstName = firstNameField.getText().trim();
                    String lastName = lastNameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String address = addressField.getText().trim();
                    Date dateOfBirth = Date.valueOf(dateOfBirthPicker.getValue());

                    if (updateCustomer(Integer.parseInt(customerId), firstName, lastName, email, phone, address, dateOfBirth)) {
                        resultArea.setText("Customer updated successfully.");
                        window.close();
                    } else {
                        resultArea.setText("Error updating customer.");
                    }
                });

                VBox layout = new VBox(10, firstNameField, lastNameField, emailField, phoneField, addressField, dateOfBirthPicker, updateButton);
                layout.setPadding(new Insets(10));
                Scene scene = new Scene(layout, 300, 350);
                window.setScene(scene);
                window.show();
            } else {
                resultArea.setText("Customer not found.");
            }
        } catch (SQLException e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    
    
 // Method to update customer details in the database
    private boolean updateCustomer(int customerId, String firstName, String lastName, String email, String phone, String address, Date dateOfBirth) {
        String query = "UPDATE Customers SET FirstName = ?, LastName = ?, Email = ?, Phone = ?, Address = ?, DateOfBirth = ? WHERE CustomerID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setDate(6, dateOfBirth);
            stmt.setInt(7, customerId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }


    // Open a new window for adding a new customer
    private void openNewCustomerWindow() {
        Stage window = new Stage();
        window.setTitle("Add New Customer");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter Last Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter Phone");

        TextField addressField = new TextField();
        addressField.setPromptText("Enter Address");

        DatePicker dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setPromptText("Enter Date of Birth");

        Button addButton = new Button("Add Customer");

        addButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            Date dateOfBirth = Date.valueOf(dateOfBirthPicker.getValue());

            if (addCustomer(firstName, lastName, email, phone, address, dateOfBirth)) {
                resultArea.setText("Customer added successfully.");
                window.close();
            } else {
                resultArea.setText("Error adding customer.");
            }
        });

        VBox layout = new VBox(10, firstNameField, lastNameField, emailField, phoneField, addressField, dateOfBirthPicker, addButton);
        layout.setPadding(new Insets(10));
        Scene scene = new Scene(layout, 300, 350);
        window.setScene(scene);
        window.show();
    }

    // Method to add a new customer to the database
    private boolean addCustomer(String firstName, String lastName, String email, String phone, String address, Date dateOfBirth) {
        String query = "INSERT INTO Customers (FirstName, LastName, Email, Phone, Address, DateOfBirth) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setDate(6, dateOfBirth);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
            return false;
        }
    }

    // Method to delete a customer from the database
    private void deleteCustomer() {
        String customerId = resultArea.getText().split("\n")[0].replace("CustomerID: ", "").trim(); // Get the customer ID from the displayed result

        if (customerId.isEmpty()) {
            resultArea.setText("Please search for a customer first.");
            return;
        }

        String query = "DELETE FROM Customers WHERE CustomerID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Integer.parseInt(customerId));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                resultArea.setText("Customer deleted successfully.");
            } else {
                resultArea.setText("Error: Customer not found.");
            }
        } catch (SQLException e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
}