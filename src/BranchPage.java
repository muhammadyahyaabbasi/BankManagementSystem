import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BranchPage {

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Branch Details");

        // Title
        Label titleLabel = new Label("Branch Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Output area to display branch information using a TextArea for scrollability
        TextArea branchInfo = new TextArea();
        branchInfo.setEditable(false);  // Prevents editing
        branchInfo.setWrapText(true);   // Ensures text wraps nicely
        branchInfo.setStyle("-fx-font-size: 14px; -fx-font-family: Arial;");

        // Populate the TextArea with branch details
        String branchDetails = getBranchDetails();
        branchInfo.setText(branchDetails);

        // Wrap the TextArea inside a ScrollPane to make it scrollable
        ScrollPane scrollPane = new ScrollPane(branchInfo);
        scrollPane.setFitToWidth(true); // Ensures the content fits within the width of the ScrollPane
        scrollPane.setFitToHeight(true); // Allows the height of the content to be adjusted as needed

        // Layout
        VBox layout = new VBox(20, titleLabel, scrollPane);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Scene and Stage
        Scene scene = new Scene(layout, 500, 400);
        window.setScene(scene);
        window.show();
    }

    private static String getBranchDetails() {
        StringBuilder branchInfo = new StringBuilder();
        String query = "SELECT BranchName, Address, Phone, ManagerID FROM Branches";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String branchName = rs.getString("BranchName");
                String address = rs.getString("Address");
                String phone = rs.getString("Phone");
                int managerId = rs.getInt("ManagerID");

                branchInfo.append("Branch Name: ").append(branchName).append("\n")
                          .append("Address: ").append(address).append("\n")
                          .append("Phone: ").append(phone != null ? phone : "N/A").append("\n")
                          .append("Manager ID: ").append(managerId).append("\n")
                          .append("------------------------------------------------\n");
            }

            if (branchInfo.length() == 0) {
                branchInfo.append("No branch details available.");
            }

        } catch (SQLException e) {
            branchInfo.append("Error: ").append(e.getMessage());
        }

        return branchInfo.toString();
    }
}
