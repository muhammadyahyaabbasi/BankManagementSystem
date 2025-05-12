import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public abstract class EmployeesPage extends Application {

    private static Connection conn;
	private TextArea employeesInfo;
    private TextField searchField;

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Employees Details");

        // Title
        Label titleLabel = new Label("Employee Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Output area to display employee information using a TextArea for scrollability
        TextArea employeesInfo = new TextArea();
        employeesInfo.setEditable(false);  // Prevents editing
        employeesInfo.setWrapText(true);   // Ensures text wraps nicely
        employeesInfo.setStyle("-fx-font-size: 14px; -fx-font-family: Arial;");
        employeesInfo.setVisible(false); // Hide initially

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Employee Name or ID");

        // Search button
        Button searchButton = new Button("Search Employee");
        searchButton.setOnAction(e -> searchEmployee(searchField.getText(), employeesInfo));

        // Add Employee button
        Button addEmployeeButton = new Button("Add Employee");
        addEmployeeButton.setOnAction(e -> openAddEmployeeWindow());

        // Delete Employee button
        Button deleteEmployeeButton = new Button("Delete Employee");
        deleteEmployeeButton.setOnAction(e -> deleteEmployee(searchField.getText(), employeesInfo));

        // Update Employee button
        Button updateEmployeeButton = new Button("Update Employee");
        updateEmployeeButton.setOnAction(e -> openUpdateEmployeeWindow(searchField.getText(), employeesInfo));

        // Layout
        VBox layout = new VBox(20, titleLabel, searchField, searchButton, addEmployeeButton, deleteEmployeeButton, updateEmployeeButton, employeesInfo);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Scene and Stage
        Scene scene = new Scene(layout, 500, 500);
        window.setScene(scene);
        window.show();
    }

    private static String getEmployeesDetails() {
        StringBuilder employeesInfo = new StringBuilder();
        String query = "SELECT EmployeeID, FirstName, LastName, Email, Phone, Role, HireDate, Salary FROM Employees";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // SimpleDateFormat to format HireDate
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while (rs.next()) {
                int employeeId = rs.getInt("EmployeeID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                String role = rs.getString("Role");
                String hireDate = dateFormat.format(rs.getDate("HireDate")); // Format hire date
                double salary = rs.getDouble("Salary");

                employeesInfo.append("Employee ID: ").append(employeeId).append("\n")
                        .append("Name: ").append(firstName).append(" ").append(lastName).append("\n")
                        .append("Email: ").append(email).append("\n")
                        .append("Phone: ").append(phone != null ? phone : "N/A").append("\n")
                        .append("Role: ").append(role).append("\n")
                        .append("Hire Date: ").append(hireDate).append("\n") // Show formatted HireDate
                        .append("Salary: $").append(salary).append("\n")
                        .append("------------------------------------------------\n");
            }

            if (employeesInfo.length() == 0) {
                employeesInfo.append("No employee details available.");
            }

        } catch (SQLException e) {
            employeesInfo.append("Error: ").append(e.getMessage());
        }

        return employeesInfo.toString();
    }

    private static void searchEmployee(String query, TextArea employeesInfo) {
        if (query == null || query.trim().isEmpty()) {
            employeesInfo.setText("Please enter a name or ID to search.");
            employeesInfo.setVisible(false);
            return;
        }

        String searchQuery = "SELECT EmployeeID, FirstName, LastName, Email, Phone, Role, HireDate, Salary FROM Employees WHERE FirstName LIKE ? OR LastName LIKE ? OR EmployeeID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, query);

            ResultSet rs = stmt.executeQuery();
            StringBuilder result = new StringBuilder();

            // SimpleDateFormat to format HireDate
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while (rs.next()) {
                int employeeId = rs.getInt("EmployeeID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                String role = rs.getString("Role");
                String hireDate = dateFormat.format(rs.getDate("HireDate")); // Format hire date
                double salary = rs.getDouble("Salary");

                result.append("Employee ID: ").append(employeeId).append("\n")
                        .append("Name: ").append(firstName).append(" ").append(lastName).append("\n")
                        .append("Email: ").append(email).append("\n")
                        .append("Phone: ").append(phone != null ? phone : "N/A").append("\n")
                        .append("Role: ").append(role).append("\n")
                        .append("Hire Date: ").append(hireDate).append("\n") // Show formatted HireDate
                        .append("Salary: $").append(salary).append("\n")
                        .append("------------------------------------------------\n");
            }

            if (result.length() == 0) {
                result.append("No employee found.");
            }

            employeesInfo.setText(result.toString());
            employeesInfo.setVisible(true); // Show the result area

        } catch (SQLException e) {
            employeesInfo.setText("Error: " + e.getMessage());
            employeesInfo.setVisible(true); // Show the result area
        }
    }

    private static void openAddEmployeeWindow() {
        Stage window = new Stage();
        window.setTitle("Add Employee");

        // Input fields for employee data
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        TextField roleField = new TextField();
        roleField.setPromptText("Role");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");

        DatePicker hireDatePicker = new DatePicker();  // Adding hire date picker
        hireDatePicker.setPromptText("Hire Date");

        Button addButton = new Button("Add Employee");
        addButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String role = roleField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim());
            String hireDate = hireDatePicker.getValue() != null ? hireDatePicker.getValue().toString() : null;

            if (addEmployee(firstName, lastName, email, phone, role, salary, hireDate)) {
                window.close();
            }
        });

        VBox layout = new VBox(10, firstNameField, lastNameField, emailField, phoneField, roleField, salaryField, hireDatePicker, addButton);
        layout.setPadding(new javafx.geometry.Insets(10));
        Scene scene = new Scene(layout, 300, 400);
        window.setScene(scene);
        window.show();
    }

    private static boolean addEmployee(String firstName, String lastName, String email, String phone, String role, double salary, String hireDate) {
        String query = "INSERT INTO Employees (FirstName, LastName, Email, Phone, Role, Salary, HireDate) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, role);
            stmt.setDouble(6, salary);
            stmt.setString(7, hireDate);  // Setting the hire date

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    private static void deleteEmployee(String query, TextArea employeesInfo) {
        if (query == null || query.trim().isEmpty()) {
            employeesInfo.setText("Please search for an employee first.");
            employeesInfo.setVisible(false);
            return;
        }

        // Step 1: First, update the branches table to set the ManagerID to NULL or another valid EmployeeID
        String updateBranchesQuery = "UPDATE branches SET ManagerID = NULL WHERE ManagerID = ?";

        // Step 2: Then delete the employee from the employees table
        String deleteEmployeeQuery = "DELETE FROM Employees WHERE EmployeeID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false); // Disable auto-commit to handle the transaction

            try (PreparedStatement updateStmt = conn.prepareStatement(updateBranchesQuery)) {
                updateStmt.setString(1, query); // Set the employee ID or name to update related rows
                updateStmt.executeUpdate();  // Update branches table
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteEmployeeQuery)) {
                deleteStmt.setString(1, query); // Set the employee ID or name to delete
                int rowsAffected = deleteStmt.executeUpdate();  // Delete employee from employees table

                if (rowsAffected > 0) {
                    employeesInfo.setText("Employee deleted successfully.");
                    employeesInfo.setVisible(true);
                } else {
                    employeesInfo.setText("Error: Employee not found.");
                    employeesInfo.setVisible(true);
                }
            }

            // Commit the transaction
            conn.commit();

        } catch (SQLException e) {
            try {
                // Rollback if there's any error
                conn.rollback();
            } catch (SQLException rollbackEx) {
                employeesInfo.setText("Error: " + rollbackEx.getMessage());
                employeesInfo.setVisible(true);
            }
            employeesInfo.setText("Error: " + e.getMessage());
            employeesInfo.setVisible(true);
        }
    }

    private static void openUpdateEmployeeWindow(String employeeId, TextArea employeesInfo) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            employeesInfo.setText("Please search for an employee first.");
            employeesInfo.setVisible(true);
            return;
        }

        // Query to fetch current employee details based on EmployeeID
        String query = "SELECT FirstName, LastName, Email, Phone, Role, Salary, HireDate FROM Employees WHERE EmployeeID = ?";

        // Create a new window for updating employee details
        Stage window = new Stage();
        window.setTitle("Update Employee");

        // Input fields for employee data
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField roleField = new TextField();
        TextField salaryField = new TextField();
        DatePicker hireDatePicker = new DatePicker();  // Hire date picker

        // Fetch current employee details and pre-fill the fields
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                firstNameField.setText(rs.getString("FirstName"));
                lastNameField.setText(rs.getString("LastName"));
                emailField.setText(rs.getString("Email"));
                phoneField.setText(rs.getString("Phone"));
                roleField.setText(rs.getString("Role"));
                salaryField.setText(String.valueOf(rs.getDouble("Salary")));
                hireDatePicker.setValue(rs.getDate("HireDate").toLocalDate());
            }

        } catch (SQLException e) {
            employeesInfo.setText("Error: " + e.getMessage());
            employeesInfo.setVisible(true);
        }

        Button updateButton = new Button("Update Employee");
        updateButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String role = roleField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim());
            String hireDate = hireDatePicker.getValue() != null ? hireDatePicker.getValue().toString() : null;

            if (updateEmployee(employeeId, firstName, lastName, email, phone, role, salary, hireDate)) {
                employeesInfo.setText("Employee details updated successfully.");
                employeesInfo.setVisible(true);
                window.close();
            }
        });

        // Layout
        VBox layout = new VBox(10, firstNameField, lastNameField, emailField, phoneField, roleField, salaryField, hireDatePicker, updateButton);
        layout.setPadding(new javafx.geometry.Insets(10));
        Scene scene = new Scene(layout, 300, 400);
        window.setScene(scene);
        window.show();
    }

    private static boolean updateEmployee(String employeeId, String firstName, String lastName, String email, String phone, String role, double salary, String hireDate) {
        String query = "UPDATE Employees SET FirstName = ?, LastName = ?, Email = ?, Phone = ?, Role = ?, Salary = ?, HireDate = ? WHERE EmployeeID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, role);
            stmt.setDouble(6, salary);
            stmt.setString(7, hireDate);
            stmt.setString(8, employeeId); // Update specific employee by ID

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
