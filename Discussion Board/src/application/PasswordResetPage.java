package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * PasswordResetPage class allows the user to set a new password for their account.
 * Users must provide a valid password and type it in again to confirm it.
 */
@SuppressWarnings("unused")
public class PasswordResetPage {
	
    private final DatabaseHelper databaseHelper;
    private final String userName;
    // DatabaseHelper to handle database operations.
    public PasswordResetPage(DatabaseHelper databaseHelper, String userName) {
        this.databaseHelper = databaseHelper;
        this.userName = userName;
    }

    /**
     * Displays the Password Reset page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for password, confirm password
      
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(250);
        
        // The label lets the user know we are resetting the password
        Label passwordResetLabel = new Label("Reset Password");
	    passwordResetLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Label to display error messages for password-related errors.
        Label errorLabel_password = new Label();
        errorLabel_password.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    
        // Confirm the new password
        Button updatePasswordButton = new Button("Confirm New Password");
        
        updatePasswordButton.setOnAction(a -> {
        	// Retrieve user input
        	String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            // Empty password, display an error message
            if (password.isEmpty() || confirmPassword.isEmpty()) {
            	errorLabel_password.setText("Password Field is Empty!");
            	return;
            }
            
            // Passwords don't match, display an error message
            if (!password.equals(confirmPassword)) {
                errorLabel_password.setText("Passwords do not match.");
                return;
            }
             	      			
        	//Validate the password
        	PasswordEvaluator.evaluatePassword(password);
        	String passwordError = PasswordEvaluator.passwordErrorMessage;
        			
        	if (PasswordEvaluator.passwordErrorMessage != "") {
        		errorLabel_password.setText(passwordError);
        		System.out.println(passwordError);
        	}
        	else {
        		// If the password is valid remove errorLabel.
        		errorLabel_password.setText("");
        		System.out.println("Success! The Password is valid.");
        		// User's password is updated in the database
    		    databaseHelper.updateUserPassword(userName, password);        
            	// Navigate to the UserLoginPage after password is updated
            	new UserLoginPage(databaseHelper).show(primaryStage);
        	}
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(passwordResetLabel, passwordField, confirmPasswordField, 
        		updatePasswordButton, errorLabel_password);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Reset Password");
        primaryStage.show();
    }
}
