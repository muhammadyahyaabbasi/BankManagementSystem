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

public class TransactionHistoryPage {

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Transaction History");

        // Title
        Text title = new Text("Transaction History");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Input Field
        TextField accountIdField = new TextField();
        accountIdField.setPromptText("Enter Account ID");

        // Search Button
        Button searchBtn = new Button("Search");

        // Output Area
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        searchBtn.setOnAction(e -> {
            try {
                int accountId = Integer.parseInt(accountIdField.getText());
                outputArea.setText(getTransactionHistory(accountId));
            } catch (NumberFormatException ex) {
                outputArea.setText("Invalid Account ID. Please enter a valid number.");
            }
        });

        // Layout
        VBox layout = new VBox(15, title, accountIdField, searchBtn, outputArea);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        // Scene and Stage
        Scene scene = new Scene(layout, 500, 400);
        window.setScene(scene);
        window.show();
    }

    private static String getTransactionHistory(int accountId) {
        StringBuilder result = new StringBuilder();
        String query = """
            SELECT t.TransactionID, t.TransactionDate, t.TransactionType, t.Amount, 
                   a.AccountID, CONCAT(c.FirstName, ' ', c.LastName) AS CustomerName
            FROM Transactions t
            JOIN Accounts a ON t.AccountID = a.AccountID
            JOIN Customers c ON a.CustomerID = c.CustomerID
            WHERE t.AccountID = ?
            ORDER BY t.TransactionDate ASC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the account ID parameter
            stmt.setInt(1, accountId);

            // Execute the query and process the results
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    result.append("No transactions found for this account.");
                    return result.toString();
                }

                // Build the result string
                while (rs.next()) {
                    result.append("Transaction ID: ").append(rs.getInt("TransactionID")).append("\n")
                          .append("Account ID: ").append(rs.getInt("AccountID")).append("\n")
                          .append("Customer Name: ").append(rs.getString("CustomerName")).append("\n")
                          .append("Date: ").append(rs.getTimestamp("TransactionDate")).append("\n")
                          .append("Transaction Type: ").append(rs.getString("TransactionType")).append("\n")
                          .append("Amount: ").append(rs.getBigDecimal("Amount")).append("\n")
                          .append("------------------------------------------------\n");
                }
            }

        } catch (SQLException e) {
            result.append("Error retrieving transaction history: ").append(e.getMessage());
        }

        return result.toString();
    }


}
