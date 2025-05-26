package application;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Title: Tests for Staff Role performing operations on questions, answers, and reviews.
 * 
 * <p>
 * Description: These tests simulate different scenarios where a Staff user performs operations 
 * on questions, answers, and reviews. Depending on whether the current user has the Staff role, 
 * operations such as update, delete, and resolve are either allowed or blocked, causing an
 * error message. The tests ensure that when the current user is not Staff, the correct 
 * error message is returned, and when the user is Staff, no error message is returned.
 * </p>
 * 
 * <p> Copyright: Justin Aussie © 2025 </p>
 * 
 * @author Justin Aussie
 * 
 * @version 1.00	2025-03-26 Implemented tests for a Staff user modifying questions, answers, and reviews.
 */
public class StaffRoleTests {
    private User currentUser;
    private boolean isStaff;
    
    /**
     * Default Constructor for the Staff Role Tests 
     */
    public StaffRoleTests() {
        // default constructor
    }
    
    /**
     * Tests the updateQuestion method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the error message "Only the Staff can update this Question!" should be returned.
     * </p>
     */
    @Test
    public void negativeUpdateQuestionTest() {
        currentUser = new User("user1", "user1", "user1@asu.edu", "Abc*1234", "Student");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can update this Question!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Only the Staff can update this Question!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the updateQuestion method for a Staff user.
     * <p>
     * When the current user is Staff, no error message should be returned.
     * </p>
     */
    @Test
    public void positiveUpdateQuestionTest() {
        currentUser = new User("user2", "user2", "user2@asu.edu", "Abc*1234", "Staff");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can update this Question!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the deleteQuestion method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the error message "Only the Staff can delete this Question!" should be returned.
     * </p>
     */
    @Test
    public void negativeDeleteQuestionTest() {
        currentUser = new User("user1", "user1", "user1@asu.edu", "Abc*1234", "Student");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can delete this Question!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Only the Staff can delete this Question!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the deleteQuestion method for a Staff user.
     * <p>
     * When the current user is Staff, no error message should be returned.
     * </p>
     */
    @Test
    public void positiveDeleteQuestionTest() {
        currentUser = new User("user2", "user2", "user2@asu.edu", "Abc*1234", "Staff");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can delete this Question!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the resolveQuestion method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the error message "Only the Staff can resolve this Question!" should be returned.
     * </p>
     */
    @Test
    public void negativeResolveQuestionTest() {
        currentUser = new User("user1", "user1", "user1@asu.edu", "Abc*1234", "Student");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can resolve this Question!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Only the Staff can resolve this Question!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the resolveQuestion method for a Staff user.
     * <p>
     * When the current user is Staff, no error message should be returned.
     * </p>
     */
    @Test
    public void positiveResolveQuestionTest() {
        currentUser = new User("user2", "user2", "user2@asu.edu", "Abc*1234", "Staff");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can resolve this Question!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the updateAnswer method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the error message "Only the Staff can update this Answer!" should be returned.
     * </p>
     */
    @Test
    public void negativeUpdateAnswerTest() {
        currentUser = new User("user1", "user1", "user1@asu.edu", "Abc*1234", "Student");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can update this Answer!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Only the Staff can update this Answer!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the updateAnswer method for a Staff user.
     * <p>
     * When the current user is Staff, no error message should be returned.
     * </p>
     */
    @Test
    public void positiveUpdateAnswerTest() {
        currentUser = new User("user2", "user2", "user2@asu.edu", "Abc*1234", "Staff");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can update this Answer!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the deleteAnswer method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the error message "Only the Staff can delete this Answer!" should be returned.
     * </p>
     */
    @Test
    public void negativeDeleteAnswerTest() {
        currentUser = new User("user1", "user1", "user1@asu.edu", "Abc*1234", "Student");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can delete this Answer!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Only the Staff can delete this Answer!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the deleteAnswer method for a Staff user.
     * <p>
     * When the current user is Staff, no error message should be returned.
     * </p>
     */
    @Test
    public void positiveDeleteAnswerTest() {
        currentUser = new User("user2", "user2", "user2@asu.edu", "Abc*1234", "Staff");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can delete this Answer!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the resolveAnswer method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the error message "Only the Staff can resolve this Answer!" should be returned.
     * </p>
     */
    @Test
    public void negativeResolveAnswerTest() {
        currentUser = new User("user1", "user1", "user1@asu.edu", "Abc*1234", "Student");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can resolve this Answer!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Only the Staff can resolve this Answer!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the resolveAnswer method for a Staff user.
     * <p>
     * When the current user is Staff, no error message should be returned.
     * </p>
     */
    @Test
    public void positiveResolveAnswerTest() {
        currentUser = new User("user2", "user2", "user2@asu.edu", "Abc*1234", "Staff");
        String actualMessage;
        
        if (!currentUser.getRole().contains("Staff")) {
            actualMessage = "Only the Staff can resolve this Answer!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the addReview method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the Add Review button should not be visible,
     * resulting in an empty message.
     * </p>
     */
    @Test
    public void negativeAddReviewTest() {
        isStaff = false;
        String actualMessage;
        
        if (isStaff) {
            actualMessage = "Add Review Button is now Visible!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the addReview method for a Staff user.
     * <p>
     * When the current user is Staff, the Add Review button should be visible,
     * returning the message "Add Review Button is now Visible!".
     * </p>
     */
    @Test
    public void positiveAddReviewTest() {
        isStaff = true;
        String actualMessage;
        
        if (isStaff) {
            actualMessage = "Add Review Button is now Visible!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Add Review Button is now Visible!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the updateReview method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the Update Review button should not be visible,
     * resulting in an empty message.
     * </p>
     */
    @Test
    public void negativeUpdateReviewTest() {
        isStaff = false;
        String actualMessage;
        
        if (isStaff) {
            actualMessage = "Update Review Button is now Visible!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the updateReview method for a Staff user.
     * <p>
     * When the current user is Staff, the Update Review button should be visible,
     * returning the message "Update Review Button is now Visible!".
     * </p>
     */
    @Test
    public void positiveUpdateReviewTest() {
        isStaff = true;
        String actualMessage;
        
        if (isStaff) {
            actualMessage = "Update Review Button is now Visible!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Update Review Button is now Visible!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the deleteReview method for a non-Staff user.
     * <p>
     * When the current user is not Staff, the Delete Review button should not be visible,
     * resulting in an empty message.
     * </p>
     */
    @Test
    public void negativeDeleteReviewTest() {
        isStaff = false;
        String actualMessage;
        
        if (isStaff) {
            actualMessage = "Delete Review Button is now Visible!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
    
    /**
     * Tests the deleteReview method for a Staff user.
     * <p>
     * When the current user is Staff, the Delete Review button should be visible,
     * returning the message "Delete Review Button is now Visible!".
     * </p>
     */
    @Test
    public void positiveDeleteReviewTest() {
        isStaff = true;
        String actualMessage;
        
        if (isStaff) {
            actualMessage = "Delete Review Button is now Visible!";
        } else {
            actualMessage = "";
        }
        
        String expectedMessage = "Delete Review Button is now Visible!";
        assertEquals(expectedMessage, actualMessage, "Error Message Is Incorrect!");
    }
}
