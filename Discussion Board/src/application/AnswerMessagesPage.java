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

public class AnswerMessagesPage {
    
    private final DatabaseHelper databaseHelper;
    private final Question question;
    private final Answer answer;
    private final User currentUser;
    
    public AnswerMessagesPage(DatabaseHelper databaseHelper, Question question, Answer answer, User user) {
        this.databaseHelper = databaseHelper;
        this.question = question;
        this.answer = answer;
        this.currentUser = user;
    }
    
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("Private Messages");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        String answerPreview = answer.getAnswerText().length() > 50 
            ? answer.getAnswerText().substring(0, 50) + "..." 
            : answer.getAnswerText();
        Label answerLabel = new Label("Answer: " + answerPreview);
        answerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        answerLabel.setWrapText(true);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Message
        VBox messagesBox = new VBox(5);
        messagesBox.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        
        // Load messages
        try {
            List<PrivateMessage> messages;
            
            if (currentUser.getRole().contains("Staff")) {
        		messages = databaseHelper.getPrivateMessages(question.getUserName(), answer.getQuestionId());
        	} else {
        		messages = databaseHelper.getPrivateMessages(currentUser.getUserName(), answer.getQuestionId());
        	}
            
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
                    
                    // Mark message as read if receiver == currentUser
                    if (!message.getIsRead() && message.getReceiver().equals(currentUser.getUserName())) {
                        databaseHelper.readMessage(message.getId());
                    }
                }
                
                messagesBox.getChildren().add(messageBox);
            }
        } catch (SQLException ex) {
            errorLabel.setText("Error loading");
            ex.printStackTrace();
        }
        
        // Message compose area
        Label composeLabel = new Label("Send a message:");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("...");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(4);
        
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String messageText = messageArea.getText().trim();
            
            if (messageText.isEmpty()) {
                errorLabel.setText("Message cannot be empty");
                return;
            }
            
            try {
                // Send message to answer poser
                String receiver = answer.getUserName();
                
                // If current user is answerer, they can't send a message to themselves
                if (currentUser.getUserName().equals(receiver)) {
                    errorLabel.setText("Message cannot be sent to self");
                    return;
                }
                
                databaseHelper.sendPrivateMessage(
                    answer.getQuestionId(),
                    answer.getId(),
                    currentUser.getUserName(), 
                    receiver,
                    messageText
                );
                
                // Retrieve messages
                messagesBox.getChildren().clear();
                List<PrivateMessage> updatedMessages;
                
                if (currentUser.getRole().contains("Staff")) {
            		updatedMessages = databaseHelper.getPrivateMessages(question.getUserName(), answer.getQuestionId());
            	} else {
            		updatedMessages = databaseHelper.getPrivateMessages(currentUser.getUserName(), answer.getQuestionId());
            	}
                
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                
                for (PrivateMessage message : updatedMessages) {
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
                        
                        // Mark message as read if recipient is current user
                        if (!message.getIsRead() && message.getReceiver().equals(currentUser.getUserName())) {
                            databaseHelper.readMessage(message.getId());
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
            // You'll need to modify this to go back to the appropriate page
            // New AnswerListPage(databaseHelper, answer.getQuestionId(), currentUser).show(primaryStage);
            new AnswerListPage(databaseHelper, question, currentUser).show(primaryStage);
        });
        
        HBox buttonBox = new HBox(10, sendButton, backButton);
        buttonBox.setStyle("-fx-alignment: center;");
        
        layout.getChildren().addAll(titleLabel, answerLabel, scrollPane, 
                                   composeLabel, messageArea, buttonBox, errorLabel);
        
        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Answer Messages");
    }
}