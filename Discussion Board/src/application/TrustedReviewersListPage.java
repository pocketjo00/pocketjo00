package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.*;
/**
 * This page displays a simple welcome message for the Student user.
 */
public class TrustedReviewersListPage {
	private final DatabaseHelper databaseHelper;
	private final User currentUser;

    // Constructor to initialize databaseHelper
    public TrustedReviewersListPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
		this.currentUser = currentUser;
    }
    
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Trusted Reviewers");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		ListView<String> trustedAnswerersList = new ListView<>();
		try {
			List<String> trustedAnswerers = databaseHelper.getTrustedAnswerers(currentUser.getUserName());
			trustedAnswerersList.getItems().setAll(trustedAnswerers);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		HBox buttonBox = new HBox(10);
	    buttonBox.setStyle("-fx-alignment: center;");
		
		// Back Button
		Button backButton = new Button("Back");
		backButton.setOnAction(a -> new StudentHomePage(databaseHelper, currentUser).show(primaryStage, currentUser));
	    
	    //Button to Log Out and go back to Setup and Login Page
	    Button logOutButton = new Button("Log Out");
	    logOutButton.setOnAction(a -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));
	    
	    buttonBox.getChildren().addAll(backButton, logOutButton);
	    
	    layout.getChildren().addAll(userLabel, trustedAnswerersList, buttonBox);
	    
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Trusted Reviewers List Page");
    	
    }
}