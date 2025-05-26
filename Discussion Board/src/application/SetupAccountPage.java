package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, name, email, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter name");
        nameField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel_username = new Label();	// For displaying username-related errors.
        errorLabel_username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        Label errorLabel_password = new Label();	// For displaying password-related errors.
        errorLabel_password.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        Label errorLabel_code = new Label();		// For displaying invite-code-related errors.
        errorLabel_code.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Button to setup account 
        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Input Validity Flag
        	int Validity_Flag = 0;
        	// Retrieve user input
            String userName = userNameField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		// Validate the userName
            		String errMessage = UserNameRecognizer.checkForValidUserName(userName);
         			
         			// If the returned String is not empty, it is an error message
        			if (errMessage != "") {
        				// Display the error message
        				errorLabel_username.setText(errMessage);
        				System.out.println(errMessage);
        			}
        			else {
        				// If userName is valid then increment the Validity Flag and remove errorLabel.
        				errorLabel_username.setText("");
        				Validity_Flag++;
        				System.out.println("Success! The UserName is valid.");
        			}
        			
        			//Validate the password
        			PasswordEvaluator.evaluatePassword(password);
        			String passwordError = PasswordEvaluator.passwordErrorMessage;
        			if(PasswordEvaluator.passwordErrorMessage != "") {
        				errorLabel_password.setText(passwordError);
        				System.out.println(passwordError);
        			}
        			else {
        				// If the password is valid then increment the Validity Flag and remove errorLabel.
        				errorLabel_password.setText("");
        				Validity_Flag++;
        				System.out.println("Success! The Password is valid.");
        			}
            		// Validate the invitation code
            		if(databaseHelper.validateInvitationCode(code)) {
            			// Increment Validity Flag if the code is valid and remove errorLabel.
            			errorLabel_code.setText("");
            			Validity_Flag++;
            		}
            		else {
            			// Validate whether the code has been timed out
                		if (databaseHelper.isTimedOut(code)) {
                			errorLabel_code.setText("Invitation code timed out or does not exist");
                		}
                		else {
                			errorLabel_code.setText("Please enter a valid invitation code");
                		}
            		}
            		
            		
            		
            		// Check if the input in all 3 fields are valid to create the new user.
            		if(Validity_Flag == 3) {
            			// Create a new user and register them in the database
		            	User user=new User(userName, name, email, password, "user");
		                databaseHelper.register(user);
		                
		             // Navigate to the Welcome Login Page
		                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            		}
            	}
            	else {
            		errorLabel_username.setText("This userName is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // backButton takes the user to the SetupLoginSelectionPage
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
        	SetupLoginSelectionPage setupLoginSelectionPage = new SetupLoginSelectionPage(databaseHelper);
        	setupLoginSelectionPage.show(primaryStage);
        });
        
        HBox buttonSpacer = new HBox(15); // 15 pixels spacing between buttons
        buttonSpacer.getChildren().addAll(backButton, setupButton);
        buttonSpacer.setStyle("-fx-alignment: center; -fx-padding: 0;");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, nameField, emailField, passwordField, inviteCodeField, buttonSpacer,
        		errorLabel_username, errorLabel_password, errorLabel_code);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
