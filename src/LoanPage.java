import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoanPage {

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Apply for Loan");

        // Title
        Text title = new Text("Loan Application");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Input Fields
        TextField accountIdField = new TextField();
        accountIdField.setPromptText("Enter Account ID");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter Loan Amount");

        TextField durationField = new TextField();
        durationField.setPromptText("Enter Loan Duration (Months)");

        // Apply Button
        Button applyBtn = new Button("Apply");

        // ListView to show loan review status
        ListView<String> loanReviewList = new ListView<>();
        loanReviewList.setPrefHeight(150);
        loanReviewList.setEditable(false);

        // Labels to display additional information after loan application
        Label dateLabel = new Label();
        Label accountLabel = new Label();
        Label nameLabel = new Label();
        Label transactionLabel = new Label();
        Label amountLabel = new Label();
        Label balanceLabel = new Label();

        applyBtn.setOnAction(e -> {
            try {
                int accountId = Integer.parseInt(accountIdField.getText());
                double loanAmount = Double.parseDouble(amountField.getText());
                int duration = Integer.parseInt(durationField.getText());

                // Assume these values are fetched from a database or input
                String firstName = "John"; // Example customer name
                String lastName = "Doe";  // Example customer name
                double remainingBalance = 1000.00; // Example remaining balance (you could fetch this from DB)
                
                // Get current date and time
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                
                // Set labels with dynamic information
                dateLabel.setText("Date: " + dateTime);
                accountLabel.setText("Account ID: " + accountId);
                nameLabel.setText("Customer: " + firstName + " " + lastName);
                transactionLabel.setText("Transaction: Loan Application");
                amountLabel.setText("Loan Amount: $" + loanAmount);
                balanceLabel.setText("Remaining Balance: $" + remainingBalance);

                String message = applyForLoan(accountId, loanAmount, duration);
                
                // Add loan details and status to ListView
                loanReviewList.getItems().clear(); // Clear previous entries
                loanReviewList.getItems().add("Your loan application is under review.");
                loanReviewList.getItems().add("Loan Amount: $" + loanAmount);
                loanReviewList.getItems().add("Duration: " + duration + " months");
                loanReviewList.getItems().add("Status: Pending");

            } catch (NumberFormatException ex) {
                loanReviewList.getItems().clear();
                loanReviewList.getItems().add("Invalid input. Please enter valid values.");
            }
        });

        // Layout
        VBox layout = new VBox(15, title, accountIdField, amountField, durationField, applyBtn, 
                               loanReviewList, dateLabel, accountLabel, nameLabel, transactionLabel, 
                               amountLabel, balanceLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        // Scene and Stage
        Scene scene = new Scene(layout, 400, 500);
        window.setScene(scene);
        window.show();
    }
    private static String applyForLoan(int accountId, double loanAmount, int duration) {
        // SQL query to insert a new loan application into the Loans table
        String query = "INSERT INTO Loans (AccountID, LoanAmount, DurationMonths, Status) VALUES (?, ?, ?, 'Pending')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters for the query
            stmt.setInt(1, accountId);          // Set AccountID
            stmt.setDouble(2, loanAmount);      // Set LoanAmount
            stmt.setInt(3, duration);           // Set Duration (in months)

            // Execute the query to insert the loan application
            stmt.executeUpdate();

            // Return success message
            return "Loan application submitted successfully. Status: Pending";
            
        } catch (SQLException e) {
            // Handle errors and return error message
            return "Error: " + e.getMessage();
        }
    }

}
