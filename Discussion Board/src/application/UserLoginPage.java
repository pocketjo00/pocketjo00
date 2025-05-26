package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
        	// Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            
            try {
            	// If the user is logging in with a one time password, take them to the PasswordResetPage
            	if(databaseHelper.validateOneTimePassword(userName, password)) {
    				new PasswordResetPage(databaseHelper, userName).show(primaryStage);
    				return;
    			}
            	
            	User user=new User(userName, password, "");
            	// Retrieve the user's role from the database using userName
            	String role = databaseHelper.getUserRole(userName);
            	
            	if(role!=null) {
            		user.setRole(role);
            		if(databaseHelper.login(user)) {
            			new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            		}
            		else {
            			// Display an error if the login fails
                        errorLabel.setText("Incorrect Password!");
            		}
            	}
            	else {
            		// Display an error if the account does not exist
                    errorLabel.setText("User account doesn't exist!");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            } 
        });
        
        // backButton takes the user back the SetupLoginSelectionPage
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        // forgotPasswordButton takes the User to the ForgotPasswordPage where they can reset their password
        Button forgotPasswordButton = new Button("Forgot Password?");
        forgotPasswordButton.setOnAction(a -> {
        	new ForgotPasswordPage(databaseHelper).show(primaryStage);
        });
        
        HBox buttonSpacer = new HBox(15); // 15 pixels spacing between buttons
        buttonSpacer.getChildren().addAll(backButton, loginButton);
        buttonSpacer.setStyle("-fx-alignment: center; -fx-padding: 0;");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, buttonSpacer, forgotPasswordButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
