package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class PendingReviewerRequestsPage {

    private final DatabaseHelper databaseHelper;

    public PendingReviewerRequestsPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 10;");
        
        Label titleLabel = new Label("Pending Reviewer Requests");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Table holds reviewer requests
        ListView<String> requestsListView = new ListView<>();
        try {
            List<String> requests = databaseHelper.getPendingReviewerRequests();
            requestsListView.getItems().addAll(requests);
        } catch (SQLException ex) {
            ex.printStackTrace();
            requestsListView.getItems().add("Error loading requests");
        }
        
        // Buttons to approve or decline a request
        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 0;");
        Button approveButton = new Button("Approve Request");
        Button declineButton = new Button("Decline Request");
        
        // Approve Reviewer Role Request
        approveButton.setOnAction(e -> {
            String selectedRequest = requestsListView.getSelectionModel().getSelectedItem();
            
            if (selectedRequest == null) {
            	errorLabel.setText("No user selected.");
             	System.out.println("No user selected.");
             	return;
            }
            
            if (selectedRequest != null && selectedRequest.contains("Pending Reviewer Request From: ")) {
            	String userName = selectedRequest.substring(selectedRequest.indexOf(":") + 2);
            	try {
            		int requestId = databaseHelper.getPendingReviewerRequestIdForUser(userName);
            		if (requestId != -1) {
            			databaseHelper.updateReviewerRequestStatus(requestId, "accepted");
            			String currentRoles = databaseHelper.getUserRole(userName);
            			
            			if (currentRoles == null || currentRoles.isEmpty()) {
            				currentRoles = "Reviewer";
            			} else if (!currentRoles.contains("Reviewer")) {
            				currentRoles = currentRoles + ", Reviewer";
            			}
            			databaseHelper.updateUserRoles(userName, currentRoles);
                    
            			requestsListView.getItems().remove(selectedRequest);
            		}
            	} catch (SQLException ex) {
            		ex.printStackTrace();
            	}
            }
        });
        
        // Decline Reviewer Role Request
        declineButton.setOnAction(e -> {
        	String selectedRequest = requestsListView.getSelectionModel().getSelectedItem();
        	
        	 if (selectedRequest == null) {
             	errorLabel.setText("No user selected.");
              	System.out.println("No user selected.");
              	return;
             }
        	
            if (selectedRequest != null && selectedRequest.contains("Pending Reviewer Request From: ")) {
            	String userName = selectedRequest.substring(selectedRequest.indexOf(":") + 2);
            	try {
            		int requestId = databaseHelper.getPendingReviewerRequestIdForUser(userName);
            		if (requestId != -1) {
            			databaseHelper.updateReviewerRequestStatus(requestId, "declined");
            			requestsListView.getItems().remove(selectedRequest);
            		}
            	} catch (SQLException ex) {
            		ex.printStackTrace();
            	}
            }
        });
        
        buttonBox.getChildren().addAll(approveButton, declineButton);
        Button backButton = new Button();
        
        // Back button to return to the Admin/Instructor/Staff Home Page
        if (user.getRole().contains("admin")) {	
        	backButton = new Button("Back to Admin Home");
        	backButton.setOnAction(e -> new AdminHomePage(databaseHelper).show(primaryStage, user));
        } else if (user.getRole().contains("Instructor")) {
        	backButton = new Button("Back to Instructor Home");
        	backButton.setOnAction(e -> new InstructorHomePage(databaseHelper).show(primaryStage, user));
        } else if (user.getRole().contains("Staff")) {
        	backButton = new Button("Back to Staff Home");
        	backButton.setOnAction(e -> new StaffHomePage(databaseHelper).show(primaryStage, user));
        } else {
        	System.out.println("Invalid User Role");
        }
            	
        layout.getChildren().addAll(titleLabel, requestsListView, buttonBox, errorLabel, backButton);
        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pending Reviewer Requests");
    }
}