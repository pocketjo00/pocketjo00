package application;

import databasePart1.DatabaseHelper;
import databasePart1.DatabaseHelper.MessageData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
public class StaffMessagePage {
    private final DatabaseHelper databaseHelper;
    private User currentUser;
    private ListView<String> messagesListView;
    private ObservableList<String> messages;
    private List<Integer> messageIds;

    /**
     * Constructs a StaffHomePage with the specified database helper.
     *
     * @param databaseHelper the database helper instance for data operations
     */
    public StaffMessagePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the staff home page on the given stage.
     *
     * @param primaryStage the primary stage to display the page
     * @param user the currently logged-in staff user
     */
    public void show(Stage primaryStage, User user) {
        this.currentUser = user;

        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Header with username
        Label userLabel = new Label("Hello, " + user.getUserName() + " (Staff)");
        userLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Initialize messages components
        messages = FXCollections.observableArrayList();
        messageIds = new ArrayList<>();
        messagesListView = new ListView<>(messages);
        messagesListView.setPrefSize(700, 300);
        messagesListView.setPlaceholder(new Label("No messages available"));

        // Status label
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #666;");

        // Delete button
        Button deleteButton = new Button("Delete Selected Message");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            int selectedIndex = messagesListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                deleteSelectedMessage(selectedIndex, statusLabel);
            } else {
                statusLabel.setText("Please select a message to delete");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Refresh button
        Button refreshButton = new Button("Refresh Messages");
        refreshButton.setOnAction(e -> loadMessages(statusLabel));

        // Button container
        HBox buttonBox = new HBox(10, deleteButton, refreshButton);

        layout.getChildren().addAll(
                userLabel,
                new Separator(),
                messagesListView,
                statusLabel,
                buttonBox,
                createLogOutButton(primaryStage)
        );

        // Initial load of messages
        loadMessages(statusLabel);

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Portal - " + user.getUserName());
    }

    /**
     * Loads messages from the database and displays them in the ListView.
     *
     * @param statusLabel the label to display loading status/errors
     */
    private void loadMessages(Label statusLabel) {
        try {
            messages.clear();
            messageIds.clear();

            // Use the new DatabaseHelper method
            List<MessageData> messageData = databaseHelper.loadMessages();

            for (MessageData data : messageData) {
                messageIds.add(data.id);
                messages.add(String.format("[%s] From %s:\n%s",
                        data.timestamp,
                        data.sender,
                        data.message));
            }

            statusLabel.setText("Loaded " + messages.size() + " messages");
            statusLabel.setStyle("-fx-text-fill: green;");

        } catch (SQLException e) {
            statusLabel.setText("Error loading messages: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    /**
     * Creates and returns a logout button.
     *
     * @param primaryStage the primary stage to use for navigation
     * @return the configured logout button
     */
    private Button createLogOutButton(Stage primaryStage) {
        Button logOutButton = new Button("Log Out");
        logOutButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));
        return logOutButton;
    }

    /**
     * Deletes the selected message from the database and refreshes the view.
     *
     * @param selectedIndex the index of the selected message in the ListView
     * @param statusLabel the label to display operation status/errors
     */
    private void deleteSelectedMessage(int selectedIndex, Label statusLabel) {
        try {
            int messageId = messageIds.get(selectedIndex);
            boolean success = databaseHelper.deletePrivateMessage(messageId);

            if (success) {
                statusLabel.setText("Message deleted successfully");
                statusLabel.setStyle("-fx-text-fill: green;");
                loadMessages(statusLabel); // Refresh the list
            } else {
                statusLabel.setText("Failed to delete message");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error deleting message: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}