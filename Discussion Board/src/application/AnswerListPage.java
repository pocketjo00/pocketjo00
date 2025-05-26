package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class AnswerListPage {
    
    private final DatabaseHelper databaseHelper;
    private final Question question;
    private final User currentUser;
    public boolean isSelected  = false;
    public String no_selection_error = "No Answer Selected";
    
    public AnswerListPage(DatabaseHelper databaseHelper, Question question, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.question = question;
        this.currentUser = currentUser;
    }
    
    
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label questionLabel = new Label("Question: " + question.getQuestionTitle() + "\n" 
        		+ question.getQuestionText() + "\n" + "Posted by: " + question.getUserName());
        
        questionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        questionLabel.setWrapText(true);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        ListView<Answer> answerListView = new ListView<>();
        try {
            List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getId());
            answerListView.getItems().setAll(answers);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search answers...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAnswers(newValue, answerListView);
        });

        // Trust Answerer Button
        Button trustAnswererButton = new Button("Trust Answerer");
        trustAnswererButton.setOnAction(e -> {
            Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();

            if (selectedAnswer == null) {
                errorLabel.setText("No Answer Selected");
                System.out.println("No Answer Selected");
                return;
            }

            String answererName = selectedAnswer.getUserName();

            if (currentUser.getUserName().equalsIgnoreCase(answererName)) {
                errorLabel.setText("You cannot trust yourself.");
                return;
            }

            try {
                if (databaseHelper.isTrustedAnswerer(currentUser.getUserName(), answererName)) {
                    errorLabel.setText(answererName + " is already in your trusted list.");
                    return;
                }

                databaseHelper.addTrustedAnswerer(currentUser.getUserName(), answererName);
                errorLabel.setText(answererName + " has been added to your trusted answerers list.");
                errorLabel.setStyle("-fx-background-color: green;");
            } catch (SQLException ex) {
                ex.printStackTrace();
                errorLabel.setText("Error adding trusted answerer.");
            }
        });

        // View Answer Button
        Button viewAnswerButton = new Button("View Answer");
        viewAnswerButton.setOnAction(e -> {
        	Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
        	
        	if (selectedAnswer == null) {
        		errorLabel.setText("No Answer Selected");
        		System.out.println("No Answer Selected");
        		return;
        	}
        	
        	Stage popupStage = new Stage();
            VBox popupLayout = new VBox(10);
            popupLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
            
            Label answerLabel = new Label("Answer: \n" + selectedAnswer.getAnswerText() 
            		+ "\nPosted by: " + selectedAnswer.getUserName());
            
            answerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            answerLabel.setWrapText(true);
   
            Button closeButton = new Button("Close");
            closeButton.setOnAction(event -> popupStage.close());
            

            
            popupLayout.getChildren().addAll(answerLabel, closeButton);
            Scene popupScene = new Scene(popupLayout, 400, 300);
            popupStage.setTitle("View Answer");
            popupStage.setScene(popupScene);
            popupStage.show();
        });
        
        answerListView.setCellFactory(lv -> new ListCell<Answer>() {
        	protected void updateItem(Answer answer, boolean empty) {
        		super.updateItem(answer, empty);
        		
        		if(empty || answer == null) {
        			setText(null);
        			setStyle(null);
        		} else {
        			setText(answer.getAnswerText());
        			if(answer.isResolved()) {
        			setStyle("-fx-background-color: lightgreen; -fx-font-weight: bold;");
        			} else {
        				setStyle("");
        			}
        			
        		}
        	}
        });
        
        //will bring up the resolve popup; only admins or questions poster can resolve
        Button resolveButton = new Button("Resolve?");
        
        resolveButton.setOnAction(e -> {
        	 Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
        	 if (selectedAnswer == null) {
        		    errorLabel.setText("No Answer Selected");
        		    System.out.println("No Answer Selected");
        		    return;
        	}
        	 
        	 if (!currentUser.getUserName().contains(question.getUserName()) && !currentUser.getRole().contains("Staff") &&
        		        !currentUser.getRole().contains("admin") && !currentUser.getRole().contains("Reviewer")
        		        && !currentUser.getRole().contains("Instructor")) {
        		    
        		    errorLabel.setText("Only the question poster, reviewer, staff, or admin can resolve this answer");
        		    System.out.println("Only the question poster, reviewer, staff, or admin can resolve this answer");
        		    return;
        	}
        	 
        	 Stage popUpStage = new Stage();
        	 popUpStage.setTitle("Resolve Answer Confirmation");

        	 VBox popUpLayout = new VBox(10);
        	 popUpLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        	 Label confirmLabel = new Label("Choose to either Resolve or Unresolve this answer.");
        	 confirmLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        	 
        	 Button resolveCButton = new Button("Resolve");
        	 resolveCButton.setOnAction(er -> {
        	     try {
        	         databaseHelper.updateResolve(selectedAnswer, true);
        	         List<Answer> updatedAnswers = databaseHelper.getAnswersForQuestion(question.getId());
        	         answerListView.getItems().setAll(updatedAnswers);
        	     } catch (SQLException ex) {
        	         ex.printStackTrace();
        	     }
        	     answerListView.refresh();
        	     popUpStage.close();
        	 });
        	 
        	 // Button to unresolve and answer
        	 Button unresolveButton = new Button("Unresolve");
        	 unresolveButton.setOnAction(eu -> {
        	     try {
        	         databaseHelper.updateResolve(selectedAnswer, false);
        	         List<Answer> updatedAnswers = databaseHelper.getAnswersForQuestion(question.getId());
        	         answerListView.getItems().setAll(updatedAnswers);
        	     } catch (SQLException ex) {
        	         ex.printStackTrace();
        	     }
        	     answerListView.refresh();
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
        

        
        // Post Answer Button
        Button postAnswerButton = new Button("Post Answer");
        postAnswerButton.setOnAction(e -> {
            Stage popUpStage = new Stage();
            popUpStage.setTitle("Post an Answer");
            
            VBox popUpLayout = new VBox(10);
            popUpLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
            
            Label answerLabel = new Label("Your Answer:");
            TextArea answerTextArea = new TextArea();
            answerTextArea.setPromptText("Enter your answer here");
            answerTextArea.setWrapText(true);
            answerTextArea.setPrefRowCount(4);
            
            Label popUpErrorLabel = new Label();
            popUpErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            
            Button submitButton = new Button("Submit Answer");
            submitButton.setOnAction(ev -> {
                String answerText = answerTextArea.getText().trim();
                
                if (answerText.isEmpty()) {
                    popUpErrorLabel.setText("Answer text is empty.");
                    return;
                }
                
                Answer newAnswer = new Answer(0, question.getId(), answerText, currentUser.getUserName(), false);
                try {
                    databaseHelper.addAnswer(newAnswer);
                    List<Answer> updatedAnswers = databaseHelper.getAnswersForQuestion(question.getId());
                    answerListView.getItems().setAll(updatedAnswers);
                } catch (SQLException ex) {
                    popUpErrorLabel.setText("Error posting answer.");
                    ex.printStackTrace();
                }
                popUpStage.close();
            });
            
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(ev -> popUpStage.close());
            
            HBox buttonBox = new HBox(10, submitButton, cancelButton);
            buttonBox.setStyle("-fx-alignment: center;");
            
            popUpLayout.getChildren().addAll(answerLabel, answerTextArea, popUpErrorLabel, buttonBox);
            Scene popUpScene = new Scene(popUpLayout, 400, 300);
            popUpStage.setScene(popUpScene);
            popUpStage.initModality(Modality.APPLICATION_MODAL);
            popUpStage.showAndWait();
        });
        
        // Update Answer Button
        Button updateAnswerButton = new Button("Update Answer");
        updateAnswerButton.setOnAction(e -> {
            Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
            
            if (selectedAnswer == null) {
        		errorLabel.setText("No Answer Selected");
        		System.out.println("No Answer Selected");
        		return;
        	}
            
            if (!currentUser.getUserName().contains(selectedAnswer.getUserName()) && !currentUser.getRole().contains("Staff") &&
    		        !currentUser.getRole().contains("admin") && !currentUser.getRole().contains("Instructor")) {
            	
                errorLabel.setText("Only the poster, staff, or admin can update this answer");
        		System.out.println("Only the poster, staff, or admin can update this answer");
                return;
            }
            
            Stage popUpStage = new Stage();
            popUpStage.setTitle("Update Answer");
            
            VBox popUpLayout = new VBox(10);
            popUpLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
            
            Label answerLabel = new Label("Update Your Answer:");
            TextArea answerTextArea = new TextArea(selectedAnswer.getAnswerText());
            answerTextArea.setWrapText(true);
            answerTextArea.setPrefRowCount(4);
            
            Label popUpErrorLabel = new Label();
            popUpErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            
            Button submitButton = new Button("Submit Update");
            submitButton.setOnAction(ev -> {
                String updatedText = answerTextArea.getText().trim();
                
                if (updatedText.isEmpty()) {
                    popUpErrorLabel.setText("Answer text is empty.");
                    return;
                }
                
                selectedAnswer.setAnswerText(updatedText);
                try {
                    databaseHelper.updateAnswer(selectedAnswer);
                    List<Answer> updatedAnswers = databaseHelper.getAnswersForQuestion(question.getId());
                    answerListView.getItems().setAll(updatedAnswers);
                } catch (SQLException ex) {
                    popUpErrorLabel.setText("Error updating answer.");
                    ex.printStackTrace();
                }
                popUpStage.close();
            });
            
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(ev -> popUpStage.close());
            
            HBox buttonBox = new HBox(10, submitButton, cancelButton);
            buttonBox.setStyle("-fx-alignment: center;");
            
            popUpLayout.getChildren().addAll(answerLabel, answerTextArea, popUpErrorLabel, buttonBox);
            Scene popUpScene = new Scene(popUpLayout, 400, 300);
            popUpStage.setScene(popUpScene);
            popUpStage.initModality(Modality.APPLICATION_MODAL);
            popUpStage.showAndWait();
        });
        
        // Delete Answer Button
        Button deleteAnswerButton = new Button("Delete Answer");
        deleteAnswerButton.setOnAction(e -> {
            Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
            
            if (selectedAnswer == null) {
        		errorLabel.setText("No Answer Selected");
        		System.out.println("No Answer Selected");
        		return;
        	}
            
            if (!currentUser.getUserName().contains(selectedAnswer.getUserName()) && !currentUser.getRole().contains("Staff") &&
    		        !currentUser.getRole().contains("admin") && !currentUser.getRole().contains("Instructor")) {
            	
            	errorLabel.setText("Only the poster or admin can delete this answer");
        		System.out.println("Only the poster or admin can delete this answer");
                return;
            }
            
            Stage popUpStage = new Stage();
            popUpStage.setTitle("Delete Answer Confirmation");
            
            VBox popUpLayout = new VBox(10);
            popUpLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
            
            Label confirmLabel = new Label("Are you sure you want to delete this answer?");
            confirmLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            Button confirmButton = new Button("Confirm");
            confirmButton.setOnAction(ev -> {
                try {
                    databaseHelper.deleteAnswer(selectedAnswer);
                    List<Answer> updatedAnswers = databaseHelper.getAnswersForQuestion(question.getId());
                    answerListView.getItems().setAll(updatedAnswers);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                popUpStage.close();
            });
            
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(ev -> popUpStage.close());
            
            HBox buttonBox = new HBox(10, confirmButton, cancelButton);
            buttonBox.setStyle("-fx-alignment: center;");
            
            popUpLayout.getChildren().addAll(confirmLabel, buttonBox);
            Scene popUpScene = new Scene(popUpLayout, 400, 200);
            popUpStage.setScene(popUpScene);
            popUpStage.initModality(Modality.APPLICATION_MODAL);
            popUpStage.showAndWait();
        });
        
        Button backButton = new Button("Back to Questions");
        backButton.setOnAction(e -> {
            new QuestionListPage(databaseHelper, currentUser).show(primaryStage);
        });
        
        Button messagesButton = new Button("Messages");
        messagesButton.setOnAction(e -> {
        	Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
        	if (selectedAnswer == null) {
        		errorLabel.setText("No Answer Selected");
        	} else {
        		new AnswerMessagesPage(databaseHelper, question, selectedAnswer, currentUser).show(primaryStage);
        	}
        });
        
        Button viewReviewsButton = new Button("Reviews");
        viewReviewsButton.setOnAction(e -> {
            Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
            if (selectedAnswer == null) {
                errorLabel.setText("No Answer Selected");
                return;
            }
            new ReviewListPage(databaseHelper, selectedAnswer, currentUser).show(new Stage());
        });
        
        HBox answerButtonsBox = new HBox(15);
        		
        answerButtonsBox.getChildren().addAll(viewAnswerButton, postAnswerButton, 
        		updateAnswerButton, deleteAnswerButton, resolveButton, trustAnswererButton, messagesButton, viewReviewsButton);
        
        answerButtonsBox.setStyle("-fx-alignment: center;");
        
        Label answerLabel = new Label("Answers: ");
        answerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox mainLayout = new VBox(10);
        mainLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        mainLayout.getChildren().addAll(questionLabel, searchField, answerLabel, answerListView,
                answerButtonsBox, errorLabel, backButton);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Answer List");
    }
    
    private void filterAnswers(String query, ListView<Answer> answerListView) {
        try {
            List<Answer> allAnswers = databaseHelper.getAnswersForQuestion(question.getId());
            List<Answer> filteredAnswers = allAnswers.stream()
                .filter(a -> a.getAnswerText().toLowerCase().contains(query.toLowerCase()) ||
                              a.getUserName().toLowerCase().contains(query.toLowerCase()))
                .toList();
            answerListView.getItems().setAll(filteredAnswers);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
