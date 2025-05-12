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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DepositPage {

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Deposit Funds");

        // Title
        Text title = new Text("Deposit Funds");

        // Input fields
        TextField accountIdField = new TextField();
        accountIdField.setPromptText("Enter Account ID");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter Amount to Deposit");

        // Deposit button
        Button depositBtn = new Button("Deposit");
        depositBtn.setOnAction(e -> {
            int accountId = Integer.parseInt(accountIdField.getText());
            double amount = Double.parseDouble(amountField.getText());
            processDeposit(accountId, amount, window);
        });

        VBox layout = new VBox(20, title, accountIdField, amountField, depositBtn);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300);
        window.setScene(scene);
        window.show();
    }

    private static void processDeposit(int accountId, double amount, Stage window) {
        String updateBalanceQuery = "UPDATE Accounts SET Balance = Balance + ? WHERE AccountID = ?";
        String fetchDetailsQuery = """
            SELECT Balance, FirstName, LastName 
            FROM Accounts 
            JOIN Customers ON Accounts.CustomerID = Customers.CustomerID 
            WHERE AccountID = ?
        """;
        String insertTransactionQuery = """
            INSERT INTO Transactions (AccountID, TransactionType, Amount, TransactionDate) 
            VALUES (?, 'Deposit', ?, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
             PreparedStatement fetchDetailsStmt = conn.prepareStatement(fetchDetailsQuery);
             PreparedStatement insertTransactionStmt = conn.prepareStatement(insertTransactionQuery)) {

            // Fetch account and customer details
            fetchDetailsStmt.setInt(1, accountId);
            ResultSet rs = fetchDetailsStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("Balance");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");

                // Update account balance
                updateBalanceStmt.setDouble(1, amount);
                updateBalanceStmt.setInt(2, accountId);
                updateBalanceStmt.executeUpdate();

                // Insert transaction record
                insertTransactionStmt.setInt(1, accountId);
                insertTransactionStmt.setDouble(2, amount);
                insertTransactionStmt.executeUpdate();

                // Calculate new balance and show receipt
                double newBalance = currentBalance + amount;
                showReceipt(accountId, firstName, lastName, amount, newBalance);
                window.close();
            } else {
                showError("Account not found.");
            }
        } catch (SQLException e) {
            showError("Error: " + e.getMessage());
        }
    }

    private static void showReceipt(int accountId, String firstName, String lastName, double amount, double remainingBalance) {
        Stage receiptWindow = new Stage();
        receiptWindow.setTitle("Transaction Receipt");

        // Generate receipt content
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTime = LocalDateTime.now().format(formatter);

        Text receiptTitle = new Text("Transaction Receipt");
        receiptTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label dateLabel = new Label("Date: " + dateTime);
        Label accountLabel = new Label("Account ID: " + accountId);
        Label nameLabel = new Label("Customer: " + firstName + " " + lastName);
        Label transactionLabel = new Label("Transaction: Deposit");
        Label amountLabel = new Label("Amount Deposited: $" + amount);
        Label balanceLabel = new Label("Updated Balance: $" + remainingBalance);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> receiptWindow.close());

        VBox layout = new VBox(10, receiptTitle, dateLabel, accountLabel, nameLabel, transactionLabel, amountLabel, balanceLabel, closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(layout, 350, 300);
        receiptWindow.setScene(scene);
        receiptWindow.show();
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
