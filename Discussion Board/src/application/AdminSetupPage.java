package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
//joanna test commit
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter name");
        nameField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setMaxWidth(250);

        //TextField passwordField = new TextField();
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        // Label to display error messages for invalid input or registration issues
        Label errorLabel_username = new Label();       // For displaying username-related errors.
        errorLabel_username.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        Label errorLabel_password = new Label();       // For displaying password-related errors.
        errorLabel_password.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Input Validity Flag
        	int Validity_Flag = 0;
        	
        	// Retrieve user input
            String userName = userNameField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            try {
            	//Validate the username
        		String errMessage = UserNameRecognizer.checkForValidUserName(userName);
     			
     			// If the returned String is not empty, it is an error message
    			if (errMessage != "") {
    				// Display the error message
    				errorLabel_username.setText(errMessage);
    				System.out.println(errMessage);
    				// Fetch the index where the processing of the input stopped
    				if (UserNameRecognizer.userNameRecognizerIndexofError <= -1) return;	// Should never happen
    				// Display the input line so the user can see what was entered		
    				System.out.println(userName);
    				// Display the line up to the error and the display an up arrow
    				System.out.println(userName.substring(0,UserNameRecognizer.userNameRecognizerIndexofError) + "\u21EB");
    			}
    			else {
    				// If username is valid then increment the Validity Flag and remove errorLabel.
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
    			// Check if the input in both fields are valid to create the admin account.
    			if(Validity_Flag == 2) {
    				// Create a new User object with admin role and register in the database
                	User user=new User(userName, name, email, password, "admin");
                    databaseHelper.register(user);
                    System.out.println("Administrator setup completed.");
                    
                    // Navigate to the Login Page
                    new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        		}
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameField, nameField, emailField, passwordField, setupButton, errorLabel_username, errorLabel_password);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
