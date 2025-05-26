package application;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private int answerId;
    private String userName;
    private String name;
    private String reviewText;
    private int score;
    private Timestamp createdAt;
    
    public Review(int reviewId, int answerId, String userName, String name, 
                 String reviewText, int score, Timestamp createdAt) {
        this.reviewId = reviewId;
        this.answerId = answerId;
        this.userName = userName;
        this.name = name;
        this.reviewText = reviewText;
        this.score = score;
        this.createdAt = createdAt;
    }
    
    // Getters and setters
    public int getReviewId() { return reviewId; }
    public int getAnswerId() { return answerId; }
    public String getUserName() { return userName; }
    public String getName() { return name; }
    public String getReviewText() { return reviewText; }
    public int getScore() { return score; }
    public Timestamp getCreatedAt() { return createdAt; }
    
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public void setScore(int score) { this.score = score; }
    
    @Override
    public String toString() {
        return String.format("%s - Score: %d - %s", name, score, 
            createdAt.toString().substring(0, 16));
    }
}