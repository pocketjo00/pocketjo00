package application;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Title: Staff and Instructor Group Chat
 * 
 * <p>
 * The StaffInstructorGroupChatPage class is the user interface for the group chat available 
 * to Staff and Instructors. This class allows users to send and receive group chat messages 
 * using the methods for getting and sending messages from the PrivateMessages table.
 * </p>
 * 
 * <p>
 * The group chat page displays a list of messages, a text area to compose a new message, and buttons to send 
 * the message or return to the previous home page (which depends on the user's role). The messages are 
 * formatted with a time stamp and also display which user sent the message in the group chat.
 * </p>
 * 
 * <p> Copyright: Justin Aussie © 2025 </p>
 * 
 * @author Justin Aussie
 * 
 * @version 1.00 	2025-04-11 Implemented a group chat for Staff and Instructor users
 */
public class StaffInstructorGroupChatPage {
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    /**
     * Constructs a {@code StaffInstructorGroupChatPage} using {@link DatabaseHelper} and the current user.
     *
     * @param databaseHelper the database helper that provides database operations.
     * @param currentUser the current user accessing the group chat.
     */
    public StaffInstructorGroupChatPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
    
    /**
     * Displays the group chat page for Staff and Instructors. This method displays the user interface,
     * loads all group chat messages, and sets up the send and back button actions.
     * 
     * <p>
     * The group chat messages are retrieved via a database method getGroupChatMessages(). A group chat 
     * message is sent from the text area using a database method sendGroupChatMessage() and the chat 
     * message list is then refreshed. Finally, the back button navigates the user to the appropriate 
     * home page based on their role.
     * </p>
     * 
     * @param primaryStage the primary stage for this application.
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Title and info labels
        Label titleLabel = new Label("Private Group Chat");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label infoLabel = new Label("Chat with fellow Staff and Instructors");
        infoLabel.setStyle("-fx-font-size: 14px;");
        
        // Label for displaying error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Container for displaying chat messages
        VBox messagesBox = new VBox(5);
        messagesBox.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        
        // Scroll pane to allow scrolling through messages
        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        
        // Load group chat messages into the messageBox
        try {
            List<PrivateMessage> messages = databaseHelper.getGroupChatMessages();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            
            for (PrivateMessage message : messages) {
                VBox messageBox = new VBox(5);
                boolean isFromCurrentUser = message.getSender().equals(currentUser.getUserName());
                String sender = isFromCurrentUser ? "You" : message.getSender();
                
                Label senderLabel = new Label(sender + " - " + sdf.format(message.getTime()));
                senderLabel.setStyle("-fx-font-weight: bold;");
                Label messageLabel = new Label(message.getMessage());
                messageLabel.setWrapText(true);
                
                messageBox.getChildren().addAll(senderLabel, messageLabel);
                
                if (isFromCurrentUser) {
                    messageBox.setStyle("-fx-background-color: #d1f0d1; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                } else {
                    messageBox.setStyle("-fx-background-color: #e0e0ff; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                }
                
                messagesBox.getChildren().add(messageBox);
            }
        } catch (SQLException ex) {
            errorLabel.setText("Error loading messages.");
            ex.printStackTrace();
        }
        
        // Message composition area with label and text area
        Label sendMessageLabel = new Label("Send a message:");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type your message here...");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(4);
        
        // Send button that sends a group chat message
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String messageText = messageArea.getText().trim();
            if (messageText.isEmpty()) {
                errorLabel.setText("Message cannot be empty");
                return;
            }
            
            try {
                databaseHelper.sendGroupChatMessage(currentUser.getUserName(), messageText);
                
                // Refresh messages: clear the container and load messages again
                messagesBox.getChildren().clear();
                List<PrivateMessage> messages = databaseHelper.getGroupChatMessages();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                for (PrivateMessage message : messages) {
                    VBox messageBox = new VBox(5);
                    boolean isFromCurrentUser = message.getSender().equals(currentUser.getUserName());
                    String sender = isFromCurrentUser ? "You" : message.getSender();
                    
                    Label senderLabel = new Label(sender + " - " + sdf.format(message.getTime()));
                    senderLabel.setStyle("-fx-font-weight: bold;");
                    Label messageLabel = new Label(message.getMessage());
                    messageLabel.setWrapText(true);
                    
                    messageBox.getChildren().addAll(senderLabel, messageLabel);
                    
                    if (isFromCurrentUser) {
                        messageBox.setStyle("-fx-background-color: #d1f0d1; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                    } else {
                        messageBox.setStyle("-fx-background-color: #e0e0ff; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                    }
                    
                    messagesBox.getChildren().add(messageBox);
                }
                
                messageArea.clear();
                errorLabel.setText("");
            } catch (SQLException ex) {
                errorLabel.setText("Error sending message.");
                ex.printStackTrace();
            }
        });
        
        // Back button to navigate to the appropriate home page based on the user's role
        Button backButton = new Button("Back");
        if (currentUser.getRole().contains("Staff")) {
            backButton.setOnAction(e -> new StaffHomePage(databaseHelper).show(primaryStage, currentUser));
        } else if (currentUser.getRole().contains("Instructor")) {
            backButton.setOnAction(e -> new InstructorHomePage(databaseHelper).show(primaryStage, currentUser));
        } else if (currentUser.getRole().contains("admin")) {
            backButton.setOnAction(e -> new AdminHomePage(databaseHelper).show(primaryStage, currentUser));
        } else {
            errorLabel.setText("Invalid User.");
        }
        
        HBox buttonBox = new HBox(10, sendButton, backButton);
        buttonBox.setStyle("-fx-alignment: center;");
        
        // Add all UI elements to the layout in order.
        layout.getChildren().addAll(titleLabel, infoLabel, scrollPane, sendMessageLabel, messageArea, buttonBox, errorLabel);
        
        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff and Instructor Group Chat");
        primaryStage.show();
    }
}
