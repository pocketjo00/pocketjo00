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


public class QuestionMessagesPage {
	private final DatabaseHelper databaseHelper;
	private final Question question;
	private final User user;
	
    public QuestionMessagesPage(DatabaseHelper databaseHelper, Question question, User user) {
        this.databaseHelper = databaseHelper;
        this.question = question;
        this.user = user;
    }
	
	public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("Private Messages");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label questionLabel = new Label("Question: " + question.getQuestionTitle());
        questionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        questionLabel.setWrapText(true);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Message display area
        VBox messagesBox = new VBox(5);
        messagesBox.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        
        // Scroll bar stuff
        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        
        // Load messages
        try {
        	List<PrivateMessage> messages;
        	
        	if (user.getRole().contains("Staff")) {
        		messages = databaseHelper.getPrivateMessages(question.getUserName(), question.getId());
        	} else {
        		messages = databaseHelper.getPrivateMessages(user.getUserName(), question.getId());
        	}
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            
            for (PrivateMessage message : messages) {
                VBox messageBox = new VBox(5);
                
                boolean isFromUser = message.getSender().equals(user.getUserName());
                String sender = isFromUser ? "You" : message.getSender();
                
                Label senderLabel = new Label(sender + " - " + sdf.format(message.getTime()));
                Label messageLabel = new Label(message.getMessage());
                senderLabel.setStyle("-fx-font-weight: bold;");
                messageLabel.setWrapText(true);
                
                messageBox.getChildren().addAll(senderLabel, messageLabel);
                if (isFromUser) {
                    messageBox.setStyle("-fx-background-color: #d1f0d1; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                } else {
                    messageBox.setStyle("-fx-background-color: #e0e0ff; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                    
                    // Mark message as read if recipient is current user
                    if (user.getUserName() != null && !message.getIsRead() && message.getReceiver().equals(user.getUserName())) {
                        databaseHelper.readMessage(message.getId());
                    }
                }
                
                messagesBox.getChildren().add(messageBox);
            }
            
        } catch (SQLException ex) {
            errorLabel.setText("Message Error");
            ex.printStackTrace();
        }
        
        // Message writing and sending
        Label sendMessageLabel = new Label("Send a message:");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("...");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(4);
        
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String message = messageArea.getText().trim();
            
            if (message.isEmpty()) {
                errorLabel.setText("Message cannot be empty");
                return;
            }
            
            try {
                // Send message to question asker
                String reciever = question.getUserName();
                
                if (user.getUserName().equals(reciever)) {
                    errorLabel.setText("Message cannot be sent to self");
                    return;
                }
                
                databaseHelper.sendPrivateMessage(
                	question.getId(),
                	null, // No answer id needed
                    user.getUserName(), 
                    reciever,
                    message
                );
                
                // Refresh messages
                messagesBox.getChildren().clear();
                List<PrivateMessage> updatedMessages;
            	
            	if (user.getRole().contains("Staff")) {
            		updatedMessages = databaseHelper.getPrivateMessages(question.getUserName(), question.getId());
            	} else {
            		updatedMessages = databaseHelper.getPrivateMessages(user.getUserName(), question.getId());
            	}
                
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                
                for (PrivateMessage privateMessage : updatedMessages) {
                    VBox messageBox = new VBox(5);
                    
                    boolean isFromCurrentUser = privateMessage.getSender().equals(user.getUserName());
                    String sender = isFromCurrentUser ? "You" : privateMessage.getSender();
                    Label senderLabel = new Label(sender + " - " + sdf.format(privateMessage.getTime()));
                    Label messageLabel = new Label(privateMessage.getMessage());
                    senderLabel.setStyle("-fx-font-weight: bold;");
                    messageLabel.setWrapText(true);
                    
                    messageBox.getChildren().addAll(senderLabel, messageLabel);
                    
                    if (isFromCurrentUser) {
                        messageBox.setStyle("-fx-background-color: #d1f0d1; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                    } else {
                        messageBox.setStyle("-fx-background-color: #e0e0ff; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
                        
                        // Mark message as read if receiver == user
                        if (!privateMessage.getIsRead() && privateMessage.getReceiver().equals(user.getUserName())) {
                            databaseHelper.readMessage(privateMessage.getId());
                        }
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
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new QuestionListPage(databaseHelper, user).show(primaryStage);
        });
        
        HBox buttonBox = new HBox(10, sendButton, backButton);
        buttonBox.setStyle("-fx-alignment: center;");
        
        layout.getChildren().addAll(titleLabel, questionLabel, scrollPane, 
                                   sendMessageLabel, messageArea, buttonBox, errorLabel);
        
        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Question Messages");
    }
}
