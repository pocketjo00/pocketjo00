package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

/**
 * This page displays a welcome message and shows all reviews written by the Reviewer user.
 */
public class ReviewerHomePage {
    private final DatabaseHelper databaseHelper;
    private final User currentUser;

    // Constructor to initialize databaseHelper
    public ReviewerHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display Hello user
        Label userLabel = new Label("Hello, Reviewer!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Label for reviews section
        Label reviewsLabel = new Label("Your Reviews:");
        reviewsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // ListView to display all reviews written by this reviewer
        ListView<String> reviewsList = new ListView<>();
        try {
            List<Review> reviews = databaseHelper.getReviewsByUser(currentUser.getUserName());
            for (Review review : reviews) {
                String reviewText = String.format("Review: %s | Score: %d",
                	review.getReviewText(),
                    review.getScore());
                reviewsList.getItems().add(reviewText);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            reviewsList.getItems().add("Error loading reviews");
        }

        // Button to view detailed review (when a review is selected)
        Button viewReviewButton = new Button("View Selected Review");
        viewReviewButton.setOnAction(a -> {
            String selected = reviewsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Stage popupStage = new Stage();
                VBox popupLayout = new VBox(10);
                popupLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
                
                Label reviewLabel = new Label(selected);
                reviewLabel.setWrapText(true);
                
                Button closeButton = new Button("Close");
                closeButton.setOnAction(event -> popupStage.close());
                
                popupLayout.getChildren().addAll(reviewLabel, closeButton);
                Scene popupScene = new Scene(popupLayout, 400, 300);
                popupStage.setTitle("Review Details");
                popupStage.setScene(popupScene);
                popupStage.show();
            }
        });

        // Button to Log Out and go back to Setup and Login Page
        Button logOutButton = new Button("Log Out");
        logOutButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        layout.getChildren().addAll(
            userLabel, 
            reviewsLabel, 
            reviewsList, 
            viewReviewButton, 
            logOutButton
        );

        Scene userScene = new Scene(layout, 800, 600);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Reviewer Page");
    }
}