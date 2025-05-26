package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The ForgotPasswordPageConfirmation class displays a screen explaining that the password
 * has been reset and the admin will provide them with a one time password.
 */
public class ForgotPasswordPageConfirmation {
	
	private final DatabaseHelper databaseHelper;

    public ForgotPasswordPageConfirmation(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage) {
	    //Gives the user the next steps to proceed with the password reset process
    	Label forgotPasswordLabel = new Label("Password has been reset. Admin will provide you with a one time password.");
	    forgotPasswordLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    //Button to close the page and take you back to the login page
	    Button okayButton = new Button("Okay");
	    
	    okayButton.setOnAction(a -> {
        	UserLoginPage userLoginPage = new UserLoginPage(databaseHelper);
        	userLoginPage.show(primaryStage);
        });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    HBox buttonSpacer = new HBox(15); // 15 pixels spacing between the buttons
        buttonSpacer.getChildren().addAll(okayButton, quitButton);
        buttonSpacer.setStyle("-fx-alignment: center; -fx-padding: 0;");
	    
	    VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    layout.getChildren().addAll(forgotPasswordLabel, buttonSpacer);

	    // Set the scene to primary stage
	    primaryStage.setScene(new Scene(layout, 800, 400));
	    primaryStage.setTitle("Password Reset Confirmation");
    }
}