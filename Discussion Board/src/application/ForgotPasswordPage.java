package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The ForgotPasswordPage class displays a screen for users to 
 * verify their credentials then they can reset their password.
 */
public class ForgotPasswordPage {
	
	private final DatabaseHelper databaseHelper;

    public ForgotPasswordPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage) {
	    // Input for userName, name, email
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter name");
        nameField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setMaxWidth(250);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	    
        //Button that generates a temporary one time password for user
	    Button resetPasswordButton = new Button("Reset Password");
	    
	    resetPasswordButton.setOnAction(a -> {
	        String userName = userNameField.getText();
	        String name = nameField.getText();
	        String email = emailField.getText();
	        
	        // If userName, name, email are valid generate the the one time password
	        if(!databaseHelper.verifyUserCredentials(userName, name, email)) {
	            errorLabel.setText("Invalid User Credentenials");
	            return;
	        }
	        
	        databaseHelper.generateOneTimePassword(userName);
	    	
	        //Takes the User to a Confirmation page explaining the next steps
        	new ForgotPasswordPageConfirmation(databaseHelper).show(primaryStage);
        });

        
	    //Button to go to the previous page
	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> {
        	new UserLoginPage(databaseHelper).show(primaryStage);
        });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
        HBox buttonSpacer = new HBox(15); // 15 pixels spacing between the buttons
        buttonSpacer.getChildren().addAll(backButton, quitButton);
        buttonSpacer.setStyle("-fx-alignment: center; -fx-padding: 0;");
	    
        VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    layout.getChildren().addAll(userNameField, nameField, emailField, resetPasswordButton, buttonSpacer, errorLabel);
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(new Scene(layout, 800, 400));
	    primaryStage.setTitle("Forgot Password");
    }
}