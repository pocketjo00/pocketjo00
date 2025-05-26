package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * This page displays a simple welcome message for the Student user.
 */
public class StudentHomePage {
	private final DatabaseHelper databaseHelper;
	private final User currentUser;

    // Constructor to initialize databaseHelper
    public StudentHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
		this.currentUser = currentUser;
    }
    
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, Student!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button that takes you to the Trusted Reviewers List
	    Button trustedReviewersListButton = new Button("Trusted Reviewers List");
	    trustedReviewersListButton.setOnAction(a -> new TrustedReviewersListPage(databaseHelper, currentUser).show(primaryStage, currentUser));

	    // Button to Request Reviewer Role
	    Button requestReviewerRoleButton = new Button("Request Reviewer Role");
        requestReviewerRoleButton.setOnAction(a -> {
               try {
            	   databaseHelper.requestReviewerRole(user.getUserName());
            	   
            	   Stage popupStage = new Stage();
                   VBox popupLayout = new VBox(10);
                   popupLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
                   
                   Label requestReviewerLabel = new Label("Requested Reviewer Role");
                   requestReviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                   
                   Button closeButton = new Button("Close");
                   closeButton.setOnAction(event -> popupStage.close());
                   
                   popupLayout.getChildren().addAll(requestReviewerLabel, closeButton);
                   Scene popupScene = new Scene(popupLayout, 400, 300);
                   popupStage.setTitle("Request Reviewer Role");
                   popupStage.setScene(popupScene);
                   popupStage.show();
                   
			} catch (SQLException e) {
				e.printStackTrace();
			}
               
        });
	    
	    //Button to Log Out and go back to Setup and Login Page
	    Button logOutButton = new Button("Log Out");
	    logOutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });
	    
	    layout.getChildren().add(userLabel);
	    
	    HBox buttonBox = new HBox(10);
	    buttonBox.setStyle("-fx-alignment: center;");
	    
	    if (!user.getRole().contains("Reviewer")) {
	        buttonBox.getChildren().add(requestReviewerRoleButton);
	    }
	    
	    buttonBox.getChildren().add(trustedReviewersListButton);
	    layout.getChildren().addAll(buttonBox, logOutButton);
	    
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Student Page");
    	
    }
}