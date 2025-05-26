package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class ReviewerListPage {
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    public ReviewerListPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
    
    public void show(Stage primaryStage) {
        VBox mainLayout = new VBox(10);
        mainLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("Reviewer Search");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12px;");
        
        // Search field for reviewers
        TextField searchField = new TextField();
        searchField.setPromptText("Search reviewers by username...");
        
        // List to display reviewers
        ListView<String> reviewerListView = new ListView<>();
        
        // Button to add selected reviewer as trusted
        Button addTrustedButton = new Button("Add Trusted Reviewer");
        addTrustedButton.setOnAction(e -> {
            String selectedReviewer = reviewerListView.getSelectionModel().getSelectedItem();
            if (selectedReviewer == null) {
                statusLabel.setText("Please select a reviewer first");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                return;
            }
            
            // Extract username from the list item (format is "username (Reviewer)")
            String reviewerUsername = selectedReviewer.split(" ")[0];
            
            try {
                // Check if already trusted
                if (databaseHelper.isTrustedAnswerer(currentUser.getUserName(), reviewerUsername)) {
                    statusLabel.setText("This reviewer is already in your trusted list");
                    statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                    return;
                }
                
                // Add to trusted list
                databaseHelper.addTrustedAnswerer(currentUser.getUserName(), reviewerUsername);
                statusLabel.setText("Reviewer added to your trusted list!");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
            } catch (SQLException ex) {
                statusLabel.setText("Error adding trusted reviewer");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                ex.printStackTrace();
            }
        });
        
        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new QuestionListPage(databaseHelper, currentUser).show(primaryStage));
        
        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                List<String> allReviewers = databaseHelper.getAllUsersWithRoles();
                List<String> filteredReviewers = allReviewers.stream()
                    .filter(user -> user.contains("Reviewer") && 
                           user.toLowerCase().contains(newValue.toLowerCase()))
                    .toList();
                reviewerListView.getItems().setAll(filteredReviewers);
            } catch (SQLException ex) {
                statusLabel.setText("Error loading reviewers");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                ex.printStackTrace();
            }
        });
        
        // Initial load of all reviewers
        try {
            List<String> allReviewers = databaseHelper.getAllUsersWithRoles().stream()
                .filter(user -> user.contains("Reviewer"))
                .toList();
            reviewerListView.getItems().setAll(allReviewers);
        } catch (SQLException ex) {
            statusLabel.setText("Error loading reviewers");
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            ex.printStackTrace();
        }
        
        HBox buttonBox = new HBox(10, addTrustedButton, backButton);
        buttonBox.setStyle("-fx-alignment: center;");
        
        mainLayout.getChildren().addAll(titleLabel, searchField, reviewerListView, buttonBox, statusLabel);
        
        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviewer Search");
        primaryStage.show();
    }
}