package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage, User user) {
	    Label welcomeLabel = new Label("Welcome User!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Messages notif
	    Label unreadMessagesLabel = new Label();
	    try {
	    	int unreadMessages = databaseHelper.countUnreadMessages(user.getUserName());
	    	unreadMessagesLabel.setText(unreadMessages + " Unread Messages");
	    	
	    } catch (SQLException ex) {
	    	ex.printStackTrace();
	    	unreadMessagesLabel.setText("Messages error");
	    }
	    
	    Label no_role_errorLabel = new Label();       // For displaying no role error.
        no_role_errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
	    continueButton.setOnAction(a -> {
	    	String role = user.getRole();
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
	    	else if(role.equals("user")) {
	    		no_role_errorLabel.setText("You don't have an assigned role yet, request the admin to assign you a role.");
	    		return;
	    	}
	    	else{
	    		new MultipleRolesHomePage(databaseHelper).show(primaryStage, user);
	    	}
	    });
	    
	    // Button to access the discussion board
	    Button discussionBoardButton = new Button("Discussion Board");
	    discussionBoardButton.setOnAction(a -> {
	            new QuestionListPage(databaseHelper, user).show(primaryStage);
	    });
	    
	    // Button to log out of your account
	    Button logOutButton = new Button("Log Out");
	    logOutButton.setOnAction(a -> {
	    	new UserLoginPage(databaseHelper).show(primaryStage);
	    });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    
	    // "Invite" button for admin to generate invitation codes
	    if ("admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            
            // "One Time Password" button for admin to generate one time password
            Button oneTimePasswordButton = new Button("One Time Password");
            oneTimePasswordButton.setOnAction(a -> {
                new OneTimePasswordPage().show(databaseHelper, primaryStage);
            });
            
            // "otherPages" button allows the admin to look at the other roles
            Button otherPages = new Button("Visit home pages of other roles");
            otherPages.setOnAction(a -> {
            	new MultipleRolesHomePage(databaseHelper).show(primaryStage, user);
            });
            
            HBox buttonSpacer = new HBox(15); // 15 pixels spacing between buttons
            buttonSpacer.getChildren().addAll(inviteButton, oneTimePasswordButton);
            buttonSpacer.setStyle("-fx-alignment: center; -fx-padding: 0;");
            
            layout.getChildren().addAll(buttonSpacer, otherPages);
        }
	    
	    HBox buttonSpacer = new HBox(15); // 15 pixels spacing between buttons
        buttonSpacer.getChildren().addAll(continueButton, logOutButton, quitButton);
        buttonSpacer.setStyle("-fx-alignment: center; -fx-padding: 0;");
        	    
	    layout.getChildren().addAll(welcomeLabel, discussionBoardButton, buttonSpacer, unreadMessagesLabel, no_role_errorLabel);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}