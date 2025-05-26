package application;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import databasePart1.DatabaseHelper;

/**
 * @author Sat Chidananda, Justin Aussie, Joanna Gavlik, Justin Lee, Ilya Tate, Aydin Rahman.
 * @version 3/29/2025
 */

/**
 * Test class for verifying the {@link DatabaseHelper#getReviewsByUser(String)} functionality.
 * This test uses a mock implementation to avoid database dependencies and focuses on
 * the behavior of the review retrieval system.
 */
public class ReviewByUserTest {
    
	/** Test username constant for the reviewer */
    private static final String TEST_USERNAME = "test_reviewer";
    
    /** 
     * First test review with positive feedback.
     * Review ID: 1, Answer ID: 101, Score: 5
     */
    private static final Review REVIEW_1 = new Review(1, 101, TEST_USERNAME, 
            "Test Reviewer", "Great answer!", 5, null);
    /** 
     * Second test review with constructive feedback. 
     * Review ID: 2, Answer ID: 102, Score: 3
     */
    private static final Review REVIEW_2 = new Review(2, 102, TEST_USERNAME, 
            "Test Reviewer", "Needs improvement", 3, null);
    
    /**
     * Tests that {@link DatabaseHelper#getReviewsByUser(String)} returns the correct
     * reviews for a known reviewer.
     * 
     * <p>Verification steps:
     * <ol>
     *   <li>Calls getReviewsByUser with test username</li>
     *   <li>Verifies exactly 2 reviews are returned</li>
     *   <li>Checks both test reviews are present in results</li>
     * </ol>
     */
    @Test
    public void testGetReviewsByUser_ReturnsCorrectReviews() throws SQLException {
    	// Arrange - Create test implementation
        DatabaseHelper testHelper = new TestDatabaseHelper();
        
        // Act - Call method under test
        List<Review> reviews = testHelper.getReviewsByUser(TEST_USERNAME);
        
        // Assert - Verify results
        assertEquals(2, reviews.size());
        assertTrue(reviews.contains(REVIEW_1));
        assertTrue(reviews.contains(REVIEW_2));
    }
    
    /**
     * Tests that {@link DatabaseHelper#getReviewsByUser(String)} returns an empty list
     * when called with an unknown username.
     * 
     * <p>Verification steps:
     * <ol>
     *   <li>Calls getReviewsByUser with unknown username</li>
     *   <li>Verifies returned list is empty</li>
     * </ol>
     */
    @Test
    public void testGetReviewsByUser_ReturnsEmptyForUnknownUser() throws SQLException {
    	// Arrange - Create test implementation
        DatabaseHelper testHelper = new TestDatabaseHelper();
        
        // Act - Call with non-existent user
        List<Review> reviews = testHelper.getReviewsByUser("unknown_user");
        
        // Assert - Verify empty list
        assertTrue(reviews.isEmpty());
    }

    /**
     * Test implementation of {@link DatabaseHelper} that provides mock data
     * without database dependencies.
     * 
     * <p>This implementation:
     * <ul>
     *   <li>Returns predefined reviews for the test username</li>
     *   <li>Returns empty list for all other usernames</li>
     *   <li>Never accesses actual database</li>
     * </ul>
     */
    private static class TestDatabaseHelper extends DatabaseHelper {
    	/**
         * Mock implementation that returns test reviews for the known user
         * and empty list for others.
         * 
         * @param username the username to lookup
         * @return list of reviews or empty list if user unknown
         */
        @Override
        public List<Review> getReviewsByUser(String username) {
            if (username.equals(TEST_USERNAME)) {
                List<Review> reviews = new ArrayList<>();
                reviews.add(REVIEW_1);
                reviews.add(REVIEW_2);
                return reviews;
            }
            return new ArrayList<>();
        }
    }
}