package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class ReviewListPage {
    private final DatabaseHelper databaseHelper;
    private final Answer answer;
    private final User currentUser;
    
    public ReviewListPage(DatabaseHelper databaseHelper, Answer answer, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.answer = answer;
        this.currentUser = currentUser;
    }
    
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label answerLabel = new Label("Answer: " + answer.getAnswerText());
        answerLabel.setWrapText(true);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        ListView<Review> reviewListView = new ListView<>();
        refreshReviews(reviewListView);
        
        // Action buttons container (initially hidden)
        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.setVisible(false);
        
        // Trust Reviewer Button (only for Students)
        Button trustReviewerButton = new Button("Trust Reviewer");
        trustReviewerButton.setVisible(false);
        
        // Update Review Button
        Button updateReviewButton = new Button("Update Review");
        updateReviewButton.setVisible(false);
        
        // Delete Review Button
        Button deleteReviewButton = new Button("Delete Review");
        deleteReviewButton.setVisible(false);
        
        // Set up button actions
        trustReviewerButton.setOnAction(e -> {
            Review selectedReview = reviewListView.getSelectionModel().getSelectedItem();
            if (selectedReview == null) return;
            
            try {
                // Check if already trusted
                if (databaseHelper.isTrustedAnswerer(currentUser.getUserName(), selectedReview.getUserName())) {
                    errorLabel.setText("You already trust this reviewer");
                    return;
                }
                
                // Don't allow trusting yourself
                if (selectedReview.getUserName().equals(currentUser.getUserName())) {
                    errorLabel.setText("You cannot trust yourself");
                    return;
                }
                
                databaseHelper.addTrustedAnswerer(currentUser.getUserName(), selectedReview.getUserName());
                errorLabel.setText("Added " + selectedReview.getName() + " to your trusted answerers");
                errorLabel.setStyle("-fx-text-fill: green;");
            } catch (SQLException ex) {
                errorLabel.setText("Error adding trusted answerer");
                ex.printStackTrace();
            }
        });
        
        updateReviewButton.setOnAction(e -> {
            Review selectedReview = reviewListView.getSelectionModel().getSelectedItem();
            if (selectedReview == null) return;
            
            Stage popupStage = new Stage();
            VBox popupLayout = new VBox(10);
            popupLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
            
            Label reviewLabel = new Label("Update Review:");
            TextArea reviewTextArea = new TextArea(selectedReview.getReviewText());
            reviewTextArea.setWrapText(true);
            
            Button submitButton = new Button("Update");
            submitButton.setOnAction(ev -> {
                String newText = reviewTextArea.getText().trim();
                if (newText.isEmpty()) {
                    errorLabel.setText("Review text cannot be empty");
                    return;
                }
                
                try {
                    selectedReview.setReviewText(newText);
                    databaseHelper.updateReview(selectedReview);
                    refreshReviews(reviewListView);
                    popupStage.close();
                } catch (SQLException ex) {
                    errorLabel.setText("Error updating review");
                    ex.printStackTrace();
                }
            });
            
            popupLayout.getChildren().addAll(reviewLabel, reviewTextArea, submitButton);
            Scene popupScene = new Scene(popupLayout, 400, 300);
            popupStage.setScene(popupScene);
            popupStage.setTitle("Update Review");
            popupStage.show();
        });
        
        deleteReviewButton.setOnAction(e -> {
            Review selectedReview = reviewListView.getSelectionModel().getSelectedItem();
            if (selectedReview == null) return;
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Review");
            alert.setContentText("Are you sure you want to delete this review?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    databaseHelper.deleteReview(selectedReview.getReviewId(), selectedReview.getUserName());
                    refreshReviews(reviewListView);
                    actionButtonsBox.setVisible(false);
                } catch (SQLException ex) {
                    errorLabel.setText("Error deleting review");
                    ex.printStackTrace();
                }
            }
        });
        
        actionButtonsBox.getChildren().addAll(updateReviewButton, deleteReviewButton, trustReviewerButton);
        actionButtonsBox.setStyle("-fx-alignment: center;");
        
        // Show appropriate buttons when a review is selected
        reviewListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                boolean isOwner = newVal.getUserName().equals(currentUser.getUserName());
                boolean isAdmin = currentUser.getRole().equals("admin");
                boolean isStudent = currentUser.getRole().equals("Student");
                boolean isStaff = currentUser.getRole().equalsIgnoreCase("Staff");
                boolean isInstructor = currentUser.getRole().equalsIgnoreCase("Instructor");
                
                // Show update/delete for owner or admin
                updateReviewButton.setVisible(isOwner || isStaff || isAdmin || isInstructor);
                deleteReviewButton.setVisible(isOwner || isStaff || isAdmin || isInstructor);
                
                // Show trust button for students (except for their own reviews)
                trustReviewerButton.setVisible(isStudent && !isOwner);
                
                actionButtonsBox.setVisible(true);
            } else {
                actionButtonsBox.setVisible(false);
            }
        });
        
        // Custom cell factory to show votes and voting buttons
        reviewListView.setCellFactory(lv -> new ListCell<Review>() {
            private final HBox content = new HBox(10);
            private final Label reviewLabel = new Label();
            private final Label scoreLabel = new Label();
            private final Button upvoteButton = new Button("Good Review");
            private final Button downvoteButton = new Button("Bad Review");
            
            {
                content.getChildren().addAll(reviewLabel, scoreLabel, upvoteButton, downvoteButton);
                
                upvoteButton.setOnAction(e -> {
                    Review review = getItem();
                    if (review != null) {
                        try {
                            if (databaseHelper.hasUserVoted(review.getReviewId(), currentUser.getUserName())) {
                                errorLabel.setText("You've already voted on this review");
                                return;
                            }
                            databaseHelper.addVote(review.getReviewId(), currentUser.getUserName(), 1);
                            refreshReviews(reviewListView);
                        } catch (SQLException ex) {
                            errorLabel.setText("Error voting on review");
                            ex.printStackTrace();
                        }
                    }
                });
                
                downvoteButton.setOnAction(e -> {
                    Review review = getItem();
                    if (review != null) {
                        try {
                            if (databaseHelper.hasUserVoted(review.getReviewId(), currentUser.getUserName())) {
                                errorLabel.setText("You've already voted on this review");
                                return;
                            }
                            databaseHelper.addVote(review.getReviewId(), currentUser.getUserName(), -1);
                            refreshReviews(reviewListView);
                        } catch (SQLException ex) {
                            errorLabel.setText("Error voting on review");
                            ex.printStackTrace();
                        }
                    }
                });
            }
            
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setGraphic(null);
                } else {
                    reviewLabel.setText(review.getName() + ": " + review.getReviewText());
                    scoreLabel.setText("Score: " + review.getScore());
                    
                    try {
                        boolean hasVoted = databaseHelper.hasUserVoted(review.getReviewId(), currentUser.getUserName());
                        upvoteButton.setDisable(hasVoted);
                        downvoteButton.setDisable(hasVoted);
                        
                        // Disable voting on own reviews
                        if (review.getUserName().equals(currentUser.getUserName())) {
                            upvoteButton.setDisable(true);
                            downvoteButton.setDisable(true);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    setGraphic(content);
                }
            }
        });
        
        // Add Review Button (only visible for Reviewers)
        Button addReviewButton = new Button("Add Review");
        addReviewButton.setVisible(currentUser.getRole().equals("Reviewer") || currentUser.getRole().equals("Staff")
        	|| currentUser.getRole().equals("admin"));
        
        addReviewButton.setOnAction(e -> {
            Stage popupStage = new Stage();
            VBox popupLayout = new VBox(10);
            popupLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
            
            Label reviewLabel = new Label("Your Review:");
            TextArea reviewTextArea = new TextArea();
            reviewTextArea.setPromptText("Enter your review here");
            
            Button submitButton = new Button("Submit");
            submitButton.setOnAction(ev -> {
                String reviewText = reviewTextArea.getText().trim();
                
                if (reviewText.isEmpty()) {
                    errorLabel.setText("Review text cannot be empty");
                    return;
                }
                
                Review review = new Review(0, answer.getId(), currentUser.getUserName(), 
                                         currentUser.getName(), reviewText, 0, null);
                try {
                    databaseHelper.addReview(review);
                    refreshReviews(reviewListView);
                    popupStage.close();
                } catch (SQLException ex) {
                    errorLabel.setText("Error adding review");
                    ex.printStackTrace();
                }
            });
            
            popupLayout.getChildren().addAll(reviewLabel, reviewTextArea, submitButton);
            Scene popupScene = new Scene(popupLayout, 400, 300);
            popupStage.setScene(popupScene);
            popupStage.setTitle("Add Review");
            popupStage.show();
        });
        
        // Back button
        Button backButton = new Button("Back to Answers");
        backButton.setOnAction(e -> primaryStage.close());
        
        layout.getChildren().addAll(
            answerLabel, 
            reviewListView, 
            actionButtonsBox, 
            addReviewButton, 
            errorLabel, 
            backButton
        );
        
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviews for Answer");
        primaryStage.show();
    }
    
    private void refreshReviews(ListView<Review> reviewListView) {
        try {
            List<Review> reviews = databaseHelper.getReviewsForAnswer(answer.getId());
            reviewListView.getItems().setAll(reviews);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}