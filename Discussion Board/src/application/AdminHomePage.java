package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.*;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin along with role management.
 */
//JOAGAV - making changes to test
public class AdminHomePage {

    private final DatabaseHelper databaseHelper;

    public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Welcome Label
        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Error Label
        Label errorLabel = new Label();       // For displaying admin operation errors.
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        // Registered Users Label
        Label registeredUsersLabel = new Label("Registered Users:");
        registeredUsersLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        // ListView to display registered users
        ListView<String> userListView = new ListView<>();

        try {
            List<String> users = databaseHelper.getAllUsersWithRoles();
            userListView.getItems().addAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
            userListView.getItems().add("Error loading users");
        }

        // Role selection section
        Label roleLabel = new Label("Select Role:");
        roleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        // Create CheckBoxes
        CheckBox studentCheckBox = new CheckBox("Student");
        CheckBox instructorCheckBox = new CheckBox("Instructor");
        CheckBox staffCheckBox = new CheckBox("Staff");
        CheckBox reviewerCheckBox = new CheckBox("Reviewer");
        
        // Apply uniform styling
        String checkBoxStyle = "-fx-font-size: 14px; -fx-padding: 5;";
        studentCheckBox.setStyle(checkBoxStyle);
        instructorCheckBox.setStyle(checkBoxStyle);
        staffCheckBox.setStyle(checkBoxStyle);
        reviewerCheckBox.setStyle(checkBoxStyle);
        
        HBox checkBoxContainer = new HBox(15); // 15 pixels spacing between checkboxes
        checkBoxContainer.getChildren().addAll(studentCheckBox, instructorCheckBox, staffCheckBox, reviewerCheckBox);
        checkBoxContainer.setStyle("-fx-alignment: center; -fx-padding: 10;");
        
        Button updateButton = new Button("Update Role");

        // Event listener for updating user role
        updateButton.setOnAction(e -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            
            if (selectedUser != null && selectedUser.contains(" (")) {
                String userName = selectedUser.substring(0, selectedUser.indexOf(" ("));
                String role = selectedUser.substring((selectedUser.indexOf("(")+1), selectedUser.indexOf(")"));
                
                // Prevent the admin from updating its own roles
                if (role.equals("admin")) {
                	errorLabel.setText("Admin does not need to replace its role.");
                    System.out.println("Admin does not need to replace its role.");
                    return;
                }
                
                // Get selected roles
                List<String> selectedRoles = new ArrayList<>();
                if (studentCheckBox.isSelected()){
                	selectedRoles.add("Student");
                	}
                if (instructorCheckBox.isSelected()) {
                	selectedRoles.add("Instructor");
                }
                if (staffCheckBox.isSelected()) {
                	selectedRoles.add("Staff");
                }
                if (reviewerCheckBox.isSelected()) {
                	selectedRoles.add("Reviewer");
                }
                if (!selectedRoles.isEmpty()) {
                    String newRoles = String.join(", ", selectedRoles);
                    System.out.println(newRoles);
                    try {
                        databaseHelper.updateUserRoles(userName, newRoles);
                        userListView.getItems().clear();
                        userListView.getItems().addAll(databaseHelper.getAllUsersWithRoles());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                	errorLabel.setText("No roles selected.");
                    System.out.println("No roles selected.");
                }
                    }
            else {
            	errorLabel.setText("No user selected.");
                System.out.println("No user selected.");
            }
            });
        
        // Create a Delete Button
        Button deleteButton = new Button("Delete User");
        //JOANNA TO CHANGE AND ADD A POP UP MESSAGE ASKING IF SURE
        // Event listener for deleting user
        
        deleteButton.setOnAction(e -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null && selectedUser.contains(" (")) {
            	String userName = selectedUser.substring(0, selectedUser.indexOf(" ("));
                String role = selectedUser.substring((selectedUser.indexOf("(")+1), selectedUser.indexOf(")"));
                System.out.println(role);
                
                // Prevent the admin from deleting itself
                if (role.equals("admin")) {
                	errorLabel.setText("Admin cannot delete itself.");
                    System.out.println("Admin cannot delete itself.");
                    return;
                }
                
                //new popup 
                Stage popUpStage = new Stage();
                popUpStage.setTitle("Delete Confirmation");
                
                VBox popUpLayout = new VBox(10);
                popUpLayout.setAlignment(Pos.CENTER);

                Label delMessage = new Label("Are you sure you want to delete this user?\nUser: "+ userName);
                Button confirmButton = new Button("Confirm");
                Button cancelButton = new Button("Cancel)");
               
                confirmButton.setOnAction(en -> {
		                try {
		                    databaseHelper.deleteUser(userName);
		                    userListView.getItems().clear();
		                    userListView.getItems().addAll(databaseHelper.getAllUsersWithRoles());
		                } catch (SQLException ex) {
		                    ex.printStackTrace();
		                }
		                
		             popUpStage.close();
                });
                
                cancelButton.setOnAction(en -> 
                	popUpStage.close()
                	);
                
                HBox buttonBox = new HBox(10, confirmButton, cancelButton);
                buttonBox.setAlignment(Pos.CENTER);
                
                popUpLayout.getChildren().addAll(delMessage, buttonBox);
                
                Scene popUpScene = new Scene(popUpLayout, 300, 150);
                popUpStage.setScene(popUpScene);
                
                popUpStage.initModality(Modality.APPLICATION_MODAL);
                
                popUpStage.showAndWait();
                
	            
                
            } else {
	            	errorLabel.setText("No user selected.");
	                System.out.println("No user selected.");
            }
        });
        
        
        // Button to take you to pending reviewer requests
        Button pendingReviewerRequestsButton = new Button("Pending Reviewer Requests");
        pendingReviewerRequestsButton.setOnAction(e -> new PendingReviewerRequestsPage(databaseHelper).show(primaryStage, user));
        
        // Button to access group chat
        Button groupChatButton = new Button ("Group Chat");
	    groupChatButton.setOnAction(e -> new StaffInstructorGroupChatPage(databaseHelper, user).show(primaryStage));
        
        HBox buttonBox = new HBox(10); // 10 pixels spacing between update and delete button
        buttonBox.getChildren().addAll(updateButton, deleteButton, pendingReviewerRequestsButton, groupChatButton);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 10;");
        
        //Button to Log Out and go back to Setup and Login Page
	    Button logOutButton = new Button("Log Out");
	    logOutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });
	    
	    layout.getChildren().addAll(adminLabel, registeredUsersLabel, userListView, roleLabel,
	    		checkBoxContainer, buttonBox, errorLabel, logOutButton);

        Scene adminScene = new Scene(layout, 800, 500);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
}
