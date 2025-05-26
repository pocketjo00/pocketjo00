package application;

import databasePart1.DatabaseHelper;
import databasePart1.DatabaseHelper.MessageData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * The StaffHomePage class represents the main interface for staff users.
 * It displays a list of private messages with functionality to view, refresh,
 * and delete messages. Staff members can see messages from all users.
 */
public class InstructorModerationPage {
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    private TabPane tabPane = new TabPane();

    /**
     * Constructs a StaffHomePage with the specified database helper.
     *
     * @param databaseHelper the database helper instance for data operations
     */
    public InstructorModerationPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    /**
     * Displays the staff home page on the given stage.
     *
     * @param primaryStage the primary stage to display the page
     * @param user the currently logged-in staff user
     * @throws SQLException 
     */
    public void show(Stage primaryStage) throws SQLException {

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Tab qTab = new Tab("Questions");
        Tab ansTab = new Tab("Answers");
        Tab messagesTab = new Tab("Messages");
        Tab flaggedUsersTab = new Tab("Flagged Users");
//        
        qTab.setContent(createQTabContent());   
        qTab.setClosable(false);
        
        ansTab.setContent(createAnsTabContent());
        ansTab.setClosable(false);
        
        messagesTab.setContent(createMessageTabContent());
        messagesTab.setClosable(false);
        
        flaggedUsersTab.setContent(createFlaggedUsersContent());
        flaggedUsersTab.setClosable(false);
        
        tabPane.setStyle("-fx-background-color: #f4f4f4;");
            tabPane.setTabMinWidth(100);
            tabPane.setTabMaxWidth(200);
        
        tabPane.getTabs().addAll(qTab, ansTab, messagesTab, flaggedUsersTab);
        

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new InstructorHomePage(databaseHelper).show(primaryStage, currentUser));

        
        layout.getChildren().addAll(
                new Label("Instructor Moderation Dashboard"),
                new Separator(),
                tabPane,
                backButton
            );
        
        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Moderation Page");
        primaryStage.show();
        
    }



    
    private ScrollPane createAnsTabContent() throws SQLException {
        VBox content = new VBox(10);
        
        List<Answer> answers = databaseHelper.getAllAnswers();
        
        if (answers.isEmpty()) {
            content.getChildren().add(new Label("No answers found."));
        } else {
            for (Answer answer : answers) {
                content.getChildren().add(createAnswerCard(answer));
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
    
    private VBox createAnswerCard(Answer answer) {
        VBox card = new VBox(5);
        card.getStyleClass().add("interaction-card");
        
        // Answerer info
        HBox answererBox = new HBox(10);
        Label answererLabel = new Label("Answered by: " + answer.getUserName());
        try {
			if (databaseHelper.isUserFlagged(answer.getUserName())) {
			    answererLabel.getStyleClass().add("flagged-user");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Button flagBtn = new Button("Flag User");
        flagBtn.setOnAction(e -> {
        	
        	try {
        		databaseHelper.flagUser(answer.getUserName());
        	} catch (SQLException ex) {
        		ex.printStackTrace();
        		
        	}});
        Region spacer = new Region();
        
        answererBox.getChildren().addAll(answererLabel, flagBtn, spacer);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Original question reference
        Label questionRef = new Label("Re: Question #" + answer.getQuestionId() + " - ");
        questionRef.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");
        questionRef.setWrapText(true);
        
        // Answer text
        Label answerText = new Label(answer.getAnswerText());
        answerText.setWrapText(true);
        answerText.setStyle("-fx-font-size: 1.1em; -fx-padding: 5 0 0 0;");
        
        card.getChildren().addAll(answererBox, questionRef, answerText);
        return card;
    }
    private ScrollPane createQTabContent() throws SQLException {
        VBox content = new VBox(10);
        
        List<Question> questions = databaseHelper.getAllQuestions();
        
        if (questions.isEmpty()) {
            content.getChildren().add(new Label("No questions found."));
        } else {
            for (Question question : questions) {
                content.getChildren().add(createQuestionCard(question));
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
    
    private VBox createQuestionCard(Question question) {
        VBox card = new VBox(5);
        card.getStyleClass().add("interaction-card");
        
        // Question info
        HBox questionBox = new HBox(10);
        Label questionLabel = new Label("Asked by: " + question.getUserName());
        try {
			if (databaseHelper.isUserFlagged(question.getUserName())) {
			    questionLabel.getStyleClass().add("flagged-user");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Button flagBtn = new Button("Flag User");
        flagBtn.setOnAction(e -> {
        	
        	try {
        		databaseHelper.flagUser(question.getUserName());
        	} catch (SQLException ex) {
        		ex.printStackTrace();
        		
        	}});
        Region spacer = new Region();
        
       questionBox.getChildren().addAll(questionLabel, flagBtn, spacer);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Original question reference
        Label questionRef = new Label("Question #" + question.getId() + " - ");
        questionRef.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");
        questionRef.setWrapText(true);
        
        // Question text
        Label questionText = new Label(question.getQuestionText());
       questionText.setWrapText(true);
       questionText.setStyle("-fx-font-size: 1.1em; -fx-padding: 5 0 0 0;");
        
        card.getChildren().addAll(questionBox, questionRef, questionText);
        return card;
    }

    private ScrollPane createMessageTabContent() throws SQLException {
        VBox content = new VBox(10);
        
        List<PrivateMessage> pm = databaseHelper.getAllMessages();
        
        if (pm.isEmpty()) {
            content.getChildren().add(new Label("No messages found."));
        } else {
            for (PrivateMessage d : pm) {
                content.getChildren().add(createMessageCard(d));
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
    
    private VBox createMessageCard(PrivateMessage d) {
        VBox card = new VBox(5);
        card.getStyleClass().add("interaction-card");
        
        // Sender Info
        HBox senderBox = new HBox(10);
        Label senderLabel = new Label("Sent by: " + d.getSender());
        try {
			if (databaseHelper.isUserFlagged(d.getSender())) {
			    senderLabel.getStyleClass().add("flagged-user");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Button flagBtn = new Button("Flag User");
        flagBtn.setOnAction(e -> {
        	
        	try {
        		databaseHelper.flagUser(d.getSender());
        	} catch (SQLException ex) {
        		ex.printStackTrace();
        		
        	}});
        Region spacer = new Region();
        
       senderBox.getChildren().addAll(senderLabel, flagBtn, spacer);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        
        // Message text
       Label messageText = new Label(d.getMessage());
       messageText.setWrapText(true);
       messageText.setStyle("-fx-font-size: 1.1em; -fx-padding: 5 0 0 0;");
        
        card.getChildren().addAll(senderBox, flagBtn, messageText);
        return card;
    }
    
    private ScrollPane createFlaggedUsersContent() throws SQLException {
        VBox content = new VBox(10);
        
        ListView<String> userListView = new ListView<>();

        try {
            List<String> users = databaseHelper.getFlaggedUsers();
            userListView.getItems().addAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
            userListView.getItems().add("Error loading users");
        }
        
        
        content.getChildren().add(userListView);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
    
    
    
    


}