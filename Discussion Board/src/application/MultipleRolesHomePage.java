package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the multiple roles user.
 */

public class MultipleRolesHomePage {
	private final DatabaseHelper databaseHelper;

    // Constructor to initialize databaseHelper
    public MultipleRolesHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, Multi Role User!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    // Create ComboBox for role selection
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.setPromptText("Select Role");
        roleComboBox.setMaxWidth(250);
        
        // If the user is admin then give access to all pages
        if(user.getRole().equals("admin")) {
        	roleComboBox.getItems().addAll("Student", "Instructor", "Staff", "Reviewer");
        }
        // Retrieve roles from the user object
        String userRoles = user.getRole();
        
        if (userRoles != null && !userRoles.isEmpty()) {
            String[] rolesArray = userRoles.split(",\\s*"); // Split roles by comma and space
            roleComboBox.getItems().addAll(rolesArray); // Add roles dynamically
        }
        
        else {
        	roleComboBox.setPromptText("No More Roles For You! Come Back 1 Year!");
        }
        
        Label errorLabel_role = new Label(); 		// Error label for role selection
        errorLabel_role.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
	    
	    continueButton.setOnAction(a -> {
	    	String role = roleComboBox.getValue(); // Get selected role
	    	if (role == null || role.isEmpty()) {
    	        errorLabel_role.setText("Please select a role.");
    	    } 
	    	else {
    	        errorLabel_role.setText(""); // Clear error message
    	        System.out.println(role);
    	    	
    	    	if(role.equals("admin")) {
    	    		new AdminHomePage(databaseHelper).show(primaryStage, user);
    	    	}
    	    	else if(role.equals("Student")) {
    	    		new StudentHomePage(databaseHelper, user).show(primaryStage, user);
    	    	}
    	    	else if(role.equals("Instructor")) {
    	    		new InstructorHomePage(databaseHelper).show(primaryStage, user);
    	    	}
    	    	else if(role.equals("Staff")) {
    	    		new StaffHomePage(databaseHelper).show(primaryStage, user);
    	    	}
    	    	else if(role.equals("Reviewer")) {
    	    		new ReviewerHomePage(databaseHelper, user).show(primaryStage, user);
    	    	}
    	    }
	    });
	    Button logOutButton = new Button("Log Out");
	    logOutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });
	    layout.getChildren().addAll(userLabel, roleComboBox, continueButton, errorLabel_role, logOutButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Multi Role User Page");
    }
}