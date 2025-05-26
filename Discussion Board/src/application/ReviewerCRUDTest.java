package application;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import databasePart1.DatabaseHelper;

/**
 * @author Sat Chidananda, Justin Aussie, Joanna Gavlik, Justin Lee, Ilya Tate, Aydin Rahman.
 * @version 4/2/2025
 *
 * Checks to see if a reviewer can do all CRUD operations (create, read, update, delete)
 * on reviews.
 */

public class ReviewerCRUDTest {
	/** 
	 * Test user username. 
	 */
	private static final String TEST_USERNAME = "test_reviewer";	
	
	/**
	 * Testing review.
	 */
	private static final Review REVIEW = new Review(1, 101, TEST_USERNAME, "Test_Reviewer", "Test text", 4, null);
	
    /**
     * Test creating a review.
     */
	@Test
	void testCreateReview() throws SQLException {
		TestingDatabase database = new TestingDatabase();
		database.addReview(REVIEW);
		
		List<Review> reviews = database.getReviewsByUser(TEST_USERNAME);
		
		assertEquals(1, reviews.size());
		assertEquals(REVIEW.getReviewText(), reviews.get(0).getReviewText());
	}
	
    /**
     * Test reading a review.
     * Then checks if stored properly.
     */
	@Test
	void testReadReview() throws SQLException {
		TestingDatabase database = new TestingDatabase();
		database.addReview(REVIEW);
		List<Review> reviews = database.getReviewsByUser(TEST_USERNAME);
		
		assertFalse(reviews.isEmpty());
		assertEquals(1, reviews.get(0).getReviewId());
	}
	
    /**
     * Test updating a review.
     * Checks if an added review and be accessed.
     */
	@Test
	void testUpdateReview() throws SQLException {
		String newText = "Updated test text";
		TestingDatabase database = new TestingDatabase();
		database.addReview(REVIEW);
		
		// Create new review and update it with database
		Review review = new Review(1, 101, TEST_USERNAME, "Test_Reviewer", newText, 5, null);
		database.updateReview(review);
		
		// Checker
		List<Review> reviews = database.getReviewsByUser(TEST_USERNAME);
		assertEquals(newText, reviews.get(0).getReviewText());
		assertEquals(5, reviews.get(0).getScore());
	}
	
    /**
     * Test deleting a review.
     */
	@Test
	void testDeleteReview() throws SQLException {
		TestingDatabase database = new TestingDatabase();
		database.addReview(REVIEW);
		database.deleteReview(REVIEW.getReviewId());
		
		List<Review> reviews = database.getReviewsByUser(TEST_USERNAME);
		assertTrue(reviews.isEmpty());
	}
	
	
	/**
	 * Simulated database to handle code above
	 */
	private static class TestingDatabase extends DatabaseHelper {
		private final Map<Integer, Review> reviews = new HashMap<>();
		
		public void addReview(Review review) {
			reviews.put(review.getReviewId(), review);
		}
		
		public void updateReview(Review newReview) {
			reviews.put(newReview.getReviewId(), newReview);
		}
		
		public void deleteReview(int id) {
			reviews.remove(id);
		}
		
		@Override
		public List<Review> getReviewsByUser(String username) {
			List<Review> output = new ArrayList<>();
			for (Review review : reviews.values()) {
				if (review.getUserName().equals(username)) {
					output.add(review);
				}
			}
			
			return output;
		}
	}
}
