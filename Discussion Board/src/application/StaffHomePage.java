package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class StaffHomePage {
	private final DatabaseHelper databaseHelper;

    // Constructor to initialize databaseHelper
    public StaffHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, Staff!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to access pending reviewer requests 
	    Button pendingReviewerRequestsButton = new Button("Pending Reviewer Requests");
        pendingReviewerRequestsButton.setOnAction(e -> new PendingReviewerRequestsPage(databaseHelper).show(primaryStage, user));
        
        // Button to access group chat
        Button groupChatButton = new Button ("Group Chat");
	    groupChatButton.setOnAction(e -> new StaffInstructorGroupChatPage(databaseHelper, user).show(primaryStage));

		Button viewAllMessagesButton = new Button("View All Messages");
		viewAllMessagesButton.setOnAction(e -> new StaffMessagePage(databaseHelper).show(primaryStage, user));

		HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: center;");
        buttonBox.getChildren().addAll(pendingReviewerRequestsButton, groupChatButton, viewAllMessagesButton);
        
	    //Button to Log Out and go back to Setup and Login Page
	    Button logOutButton = new Button("Log Out");
	    logOutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });
	    
	    layout.getChildren().addAll(userLabel, buttonBox, logOutButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Staff Page");
    	
    }
}