import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDetailsPage {

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Account Management");

        // Title
        Text title = new Text("Manage Customer Accounts");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Input Fields
        TextField searchField = new TextField();
        searchField.setPromptText("Enter Customer Name or ID");

        // Search Button
        Button searchBtn = new Button("Search");
        Button insertBtn = new Button("Add Account");
        Button updateBtn = new Button("Update Account");
        
     // Set equal sizes for buttons
        searchBtn.setMaxWidth(Double.MAX_VALUE);
        insertBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setMaxWidth(Double.MAX_VALUE);

        // Output Area
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        searchBtn.setOnAction(e -> {
            String input = searchField.getText();
            if (!input.isEmpty()) {
                outputArea.setText(searchAccount(input));
            } else {
                outputArea.setText("Please enter a valid customer name or ID.");
            }
        });

        insertBtn.setOnAction(e -> openInsertAccountWindow(outputArea));
        updateBtn.setOnAction(e -> openUpdateAccountWindow(outputArea));

        // Layout
        VBox layout = new VBox(15, title, searchField, searchBtn, insertBtn, updateBtn, outputArea);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        // Scene and Stage
        Scene scene = new Scene(layout, 500, 600);
        window.setScene(scene);
        window.show();
    }

    private static String searchAccount(String input) {
        StringBuilder result = new StringBuilder();
        String query = "SELECT c.CustomerID, c.FirstName, c.LastName, c.Email, c.Phone, c.Address, " +
                       "a.Balance, a.AccountType, c.RegistrationDate " +
                       "FROM Customers c " +
                       "LEFT JOIN Accounts a ON c.CustomerID = a.CustomerID " +
                       "WHERE c.FirstName LIKE ? OR c.LastName LIKE ? OR c.CustomerID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + input + "%");
            stmt.setString(2, "%" + input + "%");
            stmt.setString(3, input);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.append("Customer ID: ").append(rs.getInt("CustomerID")).append("\n");
                result.append("Name: ").append(rs.getString("FirstName")).append(" ").append(rs.getString("LastName")).append("\n");
                result.append("Email: ").append(rs.getString("Email")).append("\n");
                result.append("Phone: ").append(rs.getString("Phone") != null ? rs.getString("Phone") : "N/A").append("\n");
                result.append("Address: ").append(rs.getString("Address") != null ? rs.getString("Address") : "N/A").append("\n");
                result.append("Balance: ").append(rs.getBigDecimal("Balance") != null ? rs.getBigDecimal("Balance") : "No Account").append("\n");
                result.append("Account Type: ").append(rs.getString("AccountType") != null ? rs.getString("AccountType") : "No Account").append("\n");
                result.append("Registration Date: ").append(rs.getTimestamp("RegistrationDate")).append("\n");
                result.append("-------------------------------\n");
            }

            if (result.length() == 0) {
                result.append("No customer found with the given name or ID.");
            }

        } catch (SQLException e) {
            result.append("Error: ").append(e.getMessage());
        }

        return result.toString();
    }

    private static void openInsertAccountWindow(TextArea outputArea) {
        Stage window = new Stage();
        window.setTitle("Add New Account");

        // Input Fields
        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Customer ID");

        ComboBox<String> accountTypeField = new ComboBox<>();
        accountTypeField.getItems().addAll("Savings", "Checking");
        accountTypeField.setPromptText("Account Type");

        TextField balanceField = new TextField();
        balanceField.setPromptText("Initial Balance");

        Button addButton = new Button("Add Account");
        addButton.setOnAction(e -> {
            try {
                int customerId = Integer.parseInt(customerIdField.getText().trim());
                String accountType = accountTypeField.getValue();
                double balance = Double.parseDouble(balanceField.getText().trim());

                if (addAccount(customerId, accountType, balance)) {
                    outputArea.setText("Account added successfully!\nCustomer ID: " + customerId + 
                                        "\nAccount Type: " + accountType + "\nBalance: " + balance);
                    window.close();
                }
            } catch (Exception ex) {
                showError("Error adding account: " + ex.getMessage());
            }
        });

        VBox layout = new VBox(10, customerIdField, accountTypeField, balanceField, addButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        Scene scene = new Scene(layout, 300, 300);
        window.setScene(scene);
        window.show();
    }

    private static boolean addAccount(int customerId, String accountType, double balance) {
        String query = "INSERT INTO Accounts (CustomerID, AccountType, Balance) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, accountType);
            stmt.setDouble(3, balance);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            showError("Error: " + e.getMessage());
            return false;
        }
    }

    private static void openUpdateAccountWindow(TextArea outputArea) {
        Stage window = new Stage();
        window.setTitle("Update Account");

        // Input Fields
        TextField accountIdField = new TextField();
        accountIdField.setPromptText("Account ID");

        ComboBox<String> accountTypeField = new ComboBox<>();
        accountTypeField.getItems().addAll("Savings", "Checking");
        accountTypeField.setPromptText("New Account Type");

        TextField balanceField = new TextField();
        balanceField.setPromptText("New Balance");

        DatePicker createdDateField = new DatePicker();
        createdDateField.setPromptText("New Created Date (optional)");

        Button updateButton = new Button("Update Account");
        updateButton.setOnAction(e -> {
            try {
                int accountId = Integer.parseInt(accountIdField.getText().trim());
                String accountType = accountTypeField.getValue();
                double balance = Double.parseDouble(balanceField.getText().trim());
                String createdDate = createdDateField.getValue() != null ? createdDateField.getValue().toString() : null;

                if (updateAccount(accountId, accountType, balance, createdDate)) {
                    outputArea.setText("Account updated successfully!\nAccount ID: " + accountId +
                                        "\nNew Account Type: " + accountType + "\nNew Balance: " + balance);
                    window.close();
                }
            } catch (Exception ex) {
                showError("Error updating account: " + ex.getMessage());
            }
        });

        VBox layout = new VBox(10, accountIdField, accountTypeField, balanceField, createdDateField, updateButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        Scene scene = new Scene(layout, 400, 300);
        window.setScene(scene);
        window.show();
    }

    private static boolean updateAccount(int accountId, String accountType, double balance, String createdDate) {
        String query = "UPDATE Accounts SET AccountType = ?, Balance = ?, CreatedDate = ? WHERE AccountID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountType);
            stmt.setDouble(2, balance);
            if (createdDate != null) {
                stmt.setTimestamp(3, java.sql.Timestamp.valueOf(createdDate + " 00:00:00"));
            } else {
                stmt.setNull(3, java.sql.Types.TIMESTAMP);
            }
            stmt.setInt(4, accountId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            showError("Error: " + e.getMessage());
            return false;
        }
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
