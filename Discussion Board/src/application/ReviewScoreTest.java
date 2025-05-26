package application;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

/**
 * Test class for verifying that review scores are updated correctly when a vote is added.
 * This test class uses a mock {@link DatabaseHelper} implementation (TestDatabaseHelper) to simulate the tests.
 * <p>
 * Each test verifies that adding a vote (either +1 or -1) properly updates the score of a specific review.
 * </p>
 * 
 * <p> Copyright © 2025 </p>
 * 
 * @author Sat Chidananda, Justin Aussie, Joanna Gavlik, Justin Lee, Ilya Tate, Aydin Rahman.
 * @version 1.00	4/2/2025 Implemented Tests for Updating Review Scores
 */
public class ReviewScoreTest {
    
    /** Instance of a review with reviewId 1, initial score 0. */
    private Review review1;
    
    /** Instance of a review with reviewId 2, initial score 1. */
    private Review review2;
    
    /** Instance of a review with reviewId 3, initial score -1. */
    private Review review3;
    
    /** Instance of the test helper used to simulate database operations. */
    private TestDatabaseHelper testHelper;
    
    /**
     * Sets up each test before running them.
     * <p>
     * This method creates new instances of reviews and initializes the TestDatabaseHelper
     * with a list containing these reviews.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        // Create a new list of reviews.
        List<Review> reviews = new ArrayList<>();
        review1 = new Review(1, 101, "user1", "John", "Great answer!", 0, null);
        review2 = new Review(2, 102, "user2", "Bob", "Needs improvement", 1, null);
        review3 = new Review(3, 103, "user3", "Connor", "Keep Trying", -1, null);
        
        reviews.add(review1);
        reviews.add(review2);
        reviews.add(review3);
        
        testHelper = new TestDatabaseHelper(reviews);
    }
    
    /**
     * Test that adds a vote of +1 to a review with an initial score of 0 and updates the score to 1.
     * 
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void zeroVotesAddOne() throws SQLException {
        testHelper.addVote(1, "user1", 1);
        
        int expectedScore = review1.getScore();
        int actualScore = 1;
        
        assertEquals(expectedScore, actualScore, "The review score should be 1.");
    }
    
    /**
     * Test that adds a vote of -1 to a review with an initial score of 0 and updates the score to -1.
     * 
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void zeroVotesMinusOne() throws SQLException {
        testHelper.addVote(1, "user1", -1);
        
        int expectedScore = review1.getScore();
        int actualScore = -1;
        
        assertEquals(expectedScore, actualScore, "The review score should be -1.");
    }
    
    /**
     * Test that adds a vote of +1 to a review with an initial score of 1 and updates the score to 2.
     * 
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void oneVoteAddOne() throws SQLException {
        testHelper.addVote(2, "user2", 1);
        
        int expectedScore = review2.getScore();
        int actualScore = 2;
        
        assertEquals(expectedScore, actualScore, "The review score should be 2.");
    }
    
    /**
     * Test that adds a vote of -1 to a review with an initial score of 1 and updates the score to 0.
     * 
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void oneVoteMinusOne() throws SQLException {
        testHelper.addVote(2, "user2", -1);
        
        int expectedScore = review2.getScore();
        int actualScore = 0;
        
        assertEquals(expectedScore, actualScore, "The review score should be 0.");
    }
    
    /**
     * Test that adds a vote of +1 to a review with an initial score of -1 and updates the score to 0.
     * 
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void minusOneVoteAddOne() throws SQLException { 
        testHelper.addVote(3, "user3", 1);
        
        int expectedScore = review3.getScore();
        int actualScore = 0;
        
        assertEquals(expectedScore, actualScore, "The review score should be 0.");
    }
    
    /**
     * Test that adds a vote of -1 to a review with an initial score of -1 and updates the score to -2.
     * 
     * @throws SQLException if a database access error occurs (simulated here).
     */
    @Test
    public void minusOneVoteMinusOne() throws SQLException {
        testHelper.addVote(3, "user3", -1);
        
        int expectedScore = review3.getScore();
        int actualScore = -2;
        
        assertEquals(expectedScore, actualScore, "The review score should be -2.");
    }
    
    /**
     * Mock implementation of {@link DatabaseHelper} that simulates vote updates on reviews
     * without the use of an actual database.
     * <p>
     * This implementation also uses a list of {@link Review} objects and updates the score
     * of the review matching the given reviewId and userName by adding the voteType value to it.
     * </p>
     */
    private static class TestDatabaseHelper extends DatabaseHelper {
        /** List of reviews used for testing. */
        List<Review> reviews;
        
        /**
         * Constructs a new TestDatabaseHelper with the specified list of reviews.
         * 
         * @param reviews the list of reviews to be used by the helper.
         */
        public TestDatabaseHelper(List<Review> reviews) {
            this.reviews = reviews;
        }
        
        /**
         * Updates the score of the review matching the given reviewId and userName by adding voteType.
         * <p>
         * Only voteType values of 1 or -1 are processed.
         * </p>
         * 
         * @param reviewId the identifier of the review to update.
         * @param userName the user who is voting.
         * @param voteType the vote value to add (should be 1 or -1).
         * @throws SQLException if a simulated database access error occurs.
         */
        @Override
        public void addVote(int reviewId, String userName, int voteType) throws SQLException {
            if (voteType == 1 || voteType == -1) {
                for (int i = 0; i < reviews.size(); i++) {
                    Review review = reviews.get(i);
                    if (review.getReviewId() == reviewId && review.getUserName().equals(userName)) {
                        review.setScore(review.getScore() + voteType);
                        return;
                    }
                }
            }
        }
    }
}
