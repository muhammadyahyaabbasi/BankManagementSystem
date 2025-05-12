import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class BankManagementSystem extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Title Text
        Text title = new Text("BANK MANAGEMENT SYSTEM");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Buttons for navigation
        Button accountDetailsBtn = new Button("Account Details");
        Button withdrawBtn = new Button("Withdraw Funds");
        Button depositBtn = new Button("Deposit Funds");
        Button loanBtn = new Button("Loan Management");
        Button transactionHistoryBtn = new Button("Transaction History");
        Button addCustomerBtn = new Button("Customer Details");
        Button branchBtn = new Button("Branch Details");
        Button employeesBtn = new Button("Employees Details");

        // Event handlers for buttons
        accountDetailsBtn.setOnAction(e -> AccountDetailsPage.display());
        withdrawBtn.setOnAction(e -> WithdrawPage.display());
        depositBtn.setOnAction(e -> DepositPage.display());
        loanBtn.setOnAction(e -> LoanPage.display());
        transactionHistoryBtn.setOnAction(e -> TransactionHistoryPage.display());
        addCustomerBtn.setOnAction(e -> new CustomerDisplay().start(new Stage())); 
        branchBtn.setOnAction(e -> BranchPage.display());
        employeesBtn.setOnAction(e -> EmployeesPage.display());

     // Set equal size for all buttons
        double buttonWidth = 150; // Set desired width
        double buttonHeight = 40; // Set desired height

        accountDetailsBtn.setPrefSize(buttonWidth, buttonHeight);
        withdrawBtn.setPrefSize(buttonWidth, buttonHeight);
        depositBtn.setPrefSize(buttonWidth, buttonHeight);
        loanBtn.setPrefSize(buttonWidth, buttonHeight);
        transactionHistoryBtn.setPrefSize(buttonWidth, buttonHeight);
        addCustomerBtn.setPrefSize(buttonWidth, buttonHeight);
        branchBtn.setPrefSize(buttonWidth, buttonHeight);
        employeesBtn.setPrefSize(buttonWidth, buttonHeight);

        // Left Column (4 buttons)
        VBox leftColumn = new VBox(20, accountDetailsBtn, withdrawBtn, depositBtn, loanBtn);
        leftColumn.setStyle("-fx-alignment: center-left;");

        // Right Column (4 buttons)
        VBox rightColumn = new VBox(20, transactionHistoryBtn, addCustomerBtn, branchBtn, employeesBtn);
        rightColumn.setStyle("-fx-alignment: center-right;");

        // Combine both columns into an HBox
        HBox buttonLayout = new HBox(50, leftColumn, rightColumn);
        buttonLayout.setStyle("-fx-alignment: center;");

        // Main Layout
        VBox layout = new VBox(20, title, buttonLayout);
        layout.setStyle("-fx-padding: 50px; -fx-alignment: center; -fx-background-color: #f0f8ff;");


        // Scene and Stage
        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setTitle("Bank Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
