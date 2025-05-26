package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class QuestionListPage {
    
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    public QuestionListPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser; 
    }
    
    public void show(Stage primaryStage) {
        VBox mainLayout = new VBox(10);
        mainLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label welcomeLabel = new Label("Welcome to the Discussion Board!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label questionsLabel = new Label("Discussion Board Questions:");
        questionsLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        // Load questions from the database
        List<Question> questions = null;
        try {
            questions = databaseHelper.getAllQuestions();
        } catch (SQLException ex) {
            errorLabel.setText("Error loading questions.");
            ex.printStackTrace();
        }
        
        ListView<Question> questionListView = new ListView<>();
        if (questions != null) {
            questionListView.getItems().setAll(questions);
        }
        
        // Color question green if resolved
        questionListView.setCellFactory(lv -> new ListCell<Question>() {
        	protected void updateItem(Question question, boolean empty) {
        		super.updateItem(question, empty);
        		
        		if(empty || question == null) {
        			setText(null);
        			setStyle(null);
        		} else {
        			setText(question.getQuestionText());
        			if(question.isResolved()) {
        			setStyle("-fx-background-color: lightgreen; -fx-font-weight: bold;");
        			} else {
        				setStyle("");
        			}
        			
        		}
        	}
        });
        
        //will bring up the resolve popup; only admins, questions poster, or reviewers can resolve
        Button resolveButton = new Button("Resolve?");
        
        resolveButton.setOnAction(e -> {
        	 Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        	 if (selectedQuestion == null) {
        		    errorLabel.setText("No Question Selected");
        		    System.out.println("No Question Selected");
        		    return;
        		}
        	 
        	 if (!currentUser.getUserName().contains(selectedQuestion.getUserName()) && !currentUser.getRole().contains("Staff") &&
        			 !currentUser.getRole().contains("admin") && !currentUser.getRole().contains("Reviewer")
        			 && !currentUser.getRole().contains("Instructor")) {
        		    
        		    errorLabel.setText("Only the question poster, reviewer, staff, or admin can resolve this question");
        		    System.out.println("Only the question poster, reviewer, staff, or admin can resolve this question");
        		    return;
        	}
        	 
        	 Stage popUpStage = new Stage();
        	 popUpStage.setTitle("Resolve Question Confirmation");

        	 VBox popUpLayout = new VBox(10);
        	 popUpLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        	 Label confirmLabel = new Label("Choose to either Resolve or Unresolve this question.");
        	 confirmLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        	 
        	 Button resolveCButton = new Button("Resolve");
        	 resolveCButton.setOnAction(er -> {
        	     try {
        	         databaseHelper.updateQuestionResolve(selectedQuestion, true);
        	         List<Question> updatedQuestions = databaseHelper.getAllQuestions();
        	         questionListView.getItems().setAll(updatedQuestions);
        	     } catch (SQLException ex) {
        	         ex.printStackTrace();
        	     }
        	     questionListView.refresh();
        	     popUpStage.close();
        	 });
        	 
        	 // Button to unresolve question
        	 Button unresolveButton = new Button("Unresolve");
        	 unresolveButton.setOnAction(eu -> {
        	     try {
        	         databaseHelper.updateQuestionResolve(selectedQuestion, false);
        	         List<Question> updatedQuestions = databaseHelper.getAllQuestions();
        	         questionListView.getItems().setAll(updatedQuestions);
        	     } catch (SQLException ex) {
        	         ex.printStackTrace();
        	     }
        	     questionListView.refresh();
        	     popUpStage.close();
        		 
        	 });
        	 
        	 Button cancelButton = new Button("Cancel");
        	 cancelButton.setOnAction(ev -> popUpStage.close());

        	 HBox buttonBox = new HBox(10, resolveCButton, unresolveButton, cancelButton);
        	 buttonBox.setStyle("-fx-alignment: center;");

        	 popUpLayout.getChildren().addAll(confirmLabel, buttonBox);
        	 Scene popUpScene = new Scene(popUpLayout, 400, 200);
        	 popUpStage.setScene(popUpScene);
        	 popUpStage.initModality(Modality.APPLICATION_MODAL);
        	 popUpStage.showAndWait();
  
        });
        
        // Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search questions...");
      //Filter checkboxes
        CheckBox answeredCheckBox = new CheckBox("Show Answered Questions");
        CheckBox unansweredCheckBox = new CheckBox("Show Unanswered Questions");
        
        // Set both checked by default
        answeredCheckBox.setSelected(true);
        unansweredCheckBox.setSelected(true);
        
        //Listener for both checkboxes
        Runnable updateFilter = () -> {
            String searchText = searchField.getText();
            boolean showAnswered = answeredCheckBox.isSelected();
            boolean showUnanswered = unansweredCheckBox.isSelected();
            
            try {
                List<Question> allQuestions = databaseHelper.getAllQuestions();
                List<Question> filteredQuestions = allQuestions.stream()
                    .filter(q -> {
                        // Filter by search text
                        boolean matchesSearch = searchText.isEmpty() || 
                            q.getQuestionTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                            q.getQuestionText().toLowerCase().contains(searchText.toLowerCase());
                        
                        // Filter by answer status
                        boolean hasAnswers;
                        try {
                            hasAnswers = !databaseHelper.getAnswersForQuestion(q.getId()).isEmpty();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            hasAnswers = false;
                        }
                        
                        boolean matchesAnswerFilter = (showAnswered && hasAnswers) || 
                                                    (showUnanswered && !hasAnswers);
                        
                        return matchesSearch && matchesAnswerFilter;
                    })
                    .toList();
                questionListView.getItems().setAll(filteredQuestions);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        };
        // Adding Listeners to both checkboxes
        answeredCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateFilter.run());
        unansweredCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateFilter.run());
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter.run());
        
        // Button to search for Reviewers
        Button searchReviewersButton = new Button("Search for Reviewers");
        searchReviewersButton.setOnAction(e -> {
            new ReviewerListPage(databaseHelper, currentUser).show(primaryStage);
        });
        
        // Create a container for the filter checkboxes
        HBox filterBox = new HBox(10, answeredCheckBox, unansweredCheckBox, searchReviewersButton);
        filterBox.setStyle("-fx-alignment: center;");
        
        // Buttons for question operations
        Button viewQuestionButton = new Button("View Question");
        viewQuestionButton.setOnAction(a -> viewQuestion(primaryStage, questionListView, errorLabel));
        
        Button postQuestionButton = new Button("Post Question");
        postQuestionButton.setOnAction(e -> postQuestion(primaryStage, currentUser, questionListView, errorLabel));
        
        Button updateQuestionButton = new Button("Update Question");
        updateQuestionButton.setOnAction(e -> updateQuestion(primaryStage, currentUser, questionListView, errorLabel));
        
        Button deleteQuestionButton = new Button("Delete Question");
        deleteQuestionButton.setOnAction(e -> deleteQuestion(primaryStage, currentUser, questionListView, errorLabel));
        
        // Button to manage answers for the selected question
        Button answerQuestionButton = new Button("Answer Question");
        answerQuestionButton.setOnAction(e -> {
            Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                new AnswerListPage(databaseHelper, selectedQuestion, currentUser).show(primaryStage);
            } else {
                errorLabel.setText("No Question Selected.");
            }
        });
        
        Button messagesButton = new Button("Messages");
        messagesButton.setOnAction(e -> {
        	Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        	if (selectedQuestion == null) {
        		errorLabel.setText("No Question Selected");
        	} else {
        		new QuestionMessagesPage(databaseHelper, selectedQuestion, currentUser).show(primaryStage);
        	}
        });
        
        
        HBox questionButtons = new HBox(10);
        questionButtons.getChildren().addAll(viewQuestionButton, postQuestionButton, updateQuestionButton, 
        		deleteQuestionButton, answerQuestionButton, resolveButton, messagesButton);
        
        questionButtons.setStyle("-fx-alignment: center;");
        
        Button logOutButton = new Button("Log Out");
        logOutButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));
        
        mainLayout.getChildren().addAll(welcomeLabel, searchField, filterBox, questionsLabel, questionListView, 
        		questionButtons, errorLabel, logOutButton);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Question List");
        primaryStage.show();
    }
    
    // View Question Method
    private void viewQuestion(Stage primaryStage, ListView<Question> questionListView, Label errorLabel) {
    	errorLabel.setText("");
    	Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        
    	if (selectedQuestion == null) {
        	errorLabel.setText("No Question Selected");
         	System.out.println("No Question Selected");
         	return;
        }
    	
    	Stage popupStage = new Stage();
        VBox popupLayout = new VBox(10);
        popupLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Display the full question text and user name
        Label questionLabel = new Label("Question: " + selectedQuestion.getQuestionTitle() + "\n" 
        		+ selectedQuestion.getQuestionText() + "\nPosted by: " + selectedQuestion.getUserName());
        
        questionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        questionLabel.setWrapText(true);
        
        // Add a close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> popupStage.close());
        
        popupLayout.getChildren().addAll(questionLabel, closeButton);
        Scene popupScene = new Scene(popupLayout, 400, 300);
        popupStage.setTitle("View Question");
        popupStage.setScene(popupScene);
        popupStage.show();
    }
    
    // Post Question method
    private void postQuestion(Stage primaryStage, User user, ListView<Question> questionListView, Label errorLabel) {
        errorLabel.setText("");
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Post a New Question");
        
        VBox popUpLayout = new VBox(10);
        popUpLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("Question Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter question title");
        
        Label questionTextLabel = new Label("Question Text:");
        TextArea questionTextArea = new TextArea();
        questionTextArea.setPromptText("Enter your question here");
        questionTextArea.setWrapText(true);
        questionTextArea.setPrefRowCount(4);
        
        Label popUpErrorLabel = new Label();
        popUpErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String qText = questionTextArea.getText().trim();
            
            if (title.isEmpty() && qText.isEmpty()) {
                popUpErrorLabel.setText("Both title and question text are empty.");
                return;
            } else if (title.isEmpty()) {
                popUpErrorLabel.setText("Title is empty.");
                return;
            } else if (qText.isEmpty()) {
                popUpErrorLabel.setText("Question text is empty.");
                return;
            }
            popUpErrorLabel.setText("");
            
            Question newQuestion = new Question(0, title, qText, user.getUserName(), false);
            try {
                databaseHelper.addQuestion(newQuestion);
                List<Question> updatedQuestions = databaseHelper.getAllQuestions();
                questionListView.getItems().setAll(updatedQuestions);
            } catch (SQLException ex) {
                popUpErrorLabel.setText("Error posting question.");
                ex.printStackTrace();
            }
            popUpStage.close();
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> popUpStage.close());
        
        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setStyle("-fx-alignment: center;");
        
        popUpLayout.getChildren().addAll(titleLabel, titleField, questionTextLabel, 
        		questionTextArea, popUpErrorLabel, buttonBox);
        
        Scene popUpScene = new Scene(popUpLayout, 400, 300);
        popUpStage.setScene(popUpScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }
    
    // Update Question Method
    private void updateQuestion(Stage primaryStage, User user, ListView<Question> questionListView, Label errorLabel) {
        errorLabel.setText("");
        Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        
        if (selectedQuestion == null) {
            errorLabel.setText("No Question Selected");
            System.out.println("No Question Selected");
            return;
        }
        
        if (!currentUser.getUserName().contains(selectedQuestion.getUserName()) && !currentUser.getRole().contains("Staff") &&
   			 !currentUser.getRole().contains("admin") && !currentUser.getRole().contains("Instructor")) {
            errorLabel.setText("Only the poster, staff, or admin can update this question");
            System.out.println("Only the poster, staff, or admin can update this question");
            return;
        }
        
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Update Question");
        
        VBox popUpLayout = new VBox(10);
        popUpLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("Update Title:");
        TextField titleField = new TextField(selectedQuestion.getQuestionTitle());
        
        Label textLabel = new Label("Update Text:");
        TextArea textArea = new TextArea(selectedQuestion.getQuestionText());
        textArea.setWrapText(true);
        textArea.setPrefRowCount(4);
        
        Label popUpErrorLabel = new Label();
        popUpErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button submitButton = new Button("Submit Update");
        submitButton.setOnAction(e -> {
            String newTitle = titleField.getText().trim();
            String newText = textArea.getText().trim();
            
            if (newTitle.isEmpty() && newText.isEmpty()) {
                popUpErrorLabel.setText("Both title and text are empty.");
                return;
            } else if (newTitle.isEmpty()) {
                popUpErrorLabel.setText("Title is empty.");
                return;
            } else if (newText.isEmpty()) {
                popUpErrorLabel.setText("Text is empty.");
                return;
            }
            
            popUpErrorLabel.setText("");
            selectedQuestion.setQuestionTitle(newTitle);
            selectedQuestion.setQuestionText(newText);
            try {
                databaseHelper.updateQuestion(selectedQuestion);
                List<Question> updatedQuestions = databaseHelper.getAllQuestions();
                questionListView.getItems().setAll(updatedQuestions);
            } catch (SQLException ex) {
                popUpErrorLabel.setText("Error updating question.");
                ex.printStackTrace();
            }
            popUpStage.close();
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> popUpStage.close());
        
        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setStyle("-fx-alignment: center;");
        
        popUpLayout.getChildren().addAll(titleLabel, titleField, textLabel, 
        		textArea, popUpErrorLabel, buttonBox);
        
        Scene popUpScene = new Scene(popUpLayout, 400, 300);
        popUpStage.setScene(popUpScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }
    
    // Delete Question Method
    private void deleteQuestion(Stage primaryStage, User user, ListView<Question> questionListView, Label errorLabel) {
        errorLabel.setText("");
        Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        
        if (selectedQuestion == null) {
            errorLabel.setText("No Question Selected");
            System.out.println("No Question Selected");
            return;
        }
        
        if (!currentUser.getUserName().contains(selectedQuestion.getUserName()) && !currentUser.getRole().contains("Staff") &&
      			 !currentUser.getRole().contains("admin") && !currentUser.getRole().contains("Instructor")) {
               errorLabel.setText("Only the poster, staff, or admin can delete this question");
               System.out.println("Only the poster, staff, or admin can delete this question");
               return;
           }
        
        Stage popUpStage = new Stage();
        popUpStage.setTitle("Delete Question Confirmation");
        
        VBox popUpLayout = new VBox(10);
        popUpLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label confirmLabel = new Label("Are you sure you want to delete this question?");
        confirmLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            try {
                databaseHelper.deleteQuestion(selectedQuestion);
                List<Question> updatedQuestions = databaseHelper.getAllQuestions();
                questionListView.getItems().setAll(updatedQuestions);
            } catch (SQLException ex) {
                errorLabel.setText("Error deleting question.");
                ex.printStackTrace();
            }
            popUpStage.close();
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> popUpStage.close());
        
        HBox btnBox = new HBox(10, confirmButton, cancelButton);
        btnBox.setStyle("-fx-alignment: center;");
        
        popUpLayout.getChildren().addAll(confirmLabel, btnBox);
        Scene popUpScene = new Scene(popUpLayout, 400, 200);
        popUpStage.setScene(popUpScene);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();
    }
}