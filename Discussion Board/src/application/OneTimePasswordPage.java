package application;


import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * OneTimePasswordPage class represents the page where an admin can generate a one time password.
 * The one time password is displayed upon clicking a button.
 */

public class OneTimePasswordPage {

	/**
     * Displays the OneTimePassword Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("One Time Password");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to generate the one time password
	    Button showOneTimePasswordButton = new Button("Generate One Time Password");
	    
	    // Label to display the generated one time password
	    Label oneTimePasswordLabel = new Label(""); ;
        oneTimePasswordLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        showOneTimePasswordButton.setOnAction(a -> {
        	// Generate the one time password using the databaseHelper and set it to the label
            String oneTimePassword = databaseHelper.retrieveOneTimePassword();
            oneTimePasswordLabel.setText(oneTimePassword);
        });
        
        // Button to Log Out and go back to Setup and Login Page
	    Button logOutButton = new Button("Log Out");
	    logOutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });

        layout.getChildren().addAll(userLabel, showOneTimePasswordButton, oneTimePasswordLabel, logOutButton);
	    Scene oneTimePasswordScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(oneTimePasswordScene);
	    primaryStage.setTitle("One Time Password Page");
    	
    }
}