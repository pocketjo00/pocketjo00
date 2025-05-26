package application;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Title: Tests for question handling methods when no question is selected.
 * 
 * <p>
 * Description: These tests simulate the scenario where no question is selected by using a boolean 
 * variable, isQuestionSelected, that is always false. Each simulated method (viewQuestion, 
 * updateQuestion, deleteQuestion, answerQuestion, and resolveQuestion) checks if a question is 
 * selected; since none are, they set the error message to "No Question Selected" and return it.
 * The tests then verify that the returned error message matches the expected 
 * output of "No Question Selected".
 * </p>
 * 
 * <p> Copyright: Justin Aussie © 2025 </p>
 * 
 * @author Justin Aussie
 * 
 * @version 1.00	2025-03-26 Implemented a set of automated test cases for Question methods
 */
public class NoQuestionSelectedTests {
    private boolean isQuestionSelected; // A boolean variable that indicates whether a question is selected
    private String errorMessage; // A string variable that holds the generated error message
    
    /**
     * Constructs a new instance of the class.
     */
    public NoQuestionSelectedTests() {
        // Default constructor
    }
    
    /**
     * Sets up the test prior to them running.
     * <p>
     * Resets the isQuestionSelected flag to false and the error message to an empty string.
     * </p>
     */
    @BeforeEach
    public void setUp() {
    	isQuestionSelected = false; // Resets isQuestionSelected to false
        errorMessage = ""; // Resets errorMessage to ""
    }
    
    /**
     * Simulates the viewQuestion method.
     * <p>
     * Checks if a question is selected. If not, it sets the error message to "No Question Selected".
     * </p>
     * 
     * @return the error message after checking if a question is selected
     */
    public String viewQuestion() {
        if (!isQuestionSelected) {
            errorMessage = "No Question Selected";
        }
        return errorMessage;
    }
    
    /**
     * Simulates the updateQuestion method.
     * <p>
     * Checks if a question is selected. If not, it sets the error message to "No Question Selected".
     * </p>
     * 
     * @return the error message after checking if a question is selected
     */
    public String updateQuestion() {
        if (!isQuestionSelected) {
            errorMessage = "No Question Selected";
        }
        return errorMessage;
    }
    
    /**
     * Simulates the deleteQuestion method.
     * <p>
     * Checks if a question is selected. If not, it sets the error message to "No Question Selected".
     * </p>
     * 
     * @return the error message after checking if a question is selected
     */
    public String deleteQuestion() {
        if (!isQuestionSelected) {
            errorMessage = "No Question Selected";
        }
        return errorMessage;
    }
    
    /**
     * Simulates the answerQuestion method.
     * <p>
     * Checks if a question is selected. If not, it sets the error message to "No Question Selected".
     * </p>
     * 
     * @return the error message after checking if a question is selected
     */
    public String answerQuestion() {
        if (!isQuestionSelected) {
            errorMessage = "No Question Selected";
        }
        return errorMessage;
    }
    
    /**
     * Simulates the resolveQuestion method.
     * <p>
     * Checks if a question is selected. If not, it sets the error message to "No Question Selected".
     * </p>
     * 
     * @return the error message after checking if a question is selected
     */
    public String resolveQuestion() {
        if (!isQuestionSelected) {
            errorMessage = "No Question Selected";
        }
        return errorMessage;
    }
    
    /**
     * Tests the viewQuestion method when no question is selected.
     * <p>
     * Confirms that the returned error message is "No Question Selected".
     * If the test fails, a message will pop up saying "Error Message Is Incorrect!".
     * </p>
     */
    @Test
    public void testViewQuestion() {
        String expectedErrorMessage = "No Question Selected";
        String actualErrorMessage = viewQuestion();
        assertEquals(expectedErrorMessage, actualErrorMessage, "Error Message Is Incorrect!");
    }

    /**
     * Tests the updateQuestion method when no question is selected.
     * <p>
     * Confirms that the returned error message is "No Question Selected".
     * If the test fails, a message will pop up saying "Error Message Is Incorrect!".
     * </p>
     */
    @Test
    public void testUpdateQuestion() {
        String expectedErrorMessage = "No Question Selected";
        String actualErrorMessage = updateQuestion();
        assertEquals(expectedErrorMessage, actualErrorMessage, "Error Message Is Incorrect!");
    }

    /**
     * Tests the deleteQuestion method when no question is selected.
     * <p>
     * Confirms that the returned error message is "No Question Selected".
     * If the test fails, a message will pop up saying "Error Message Is Incorrect!".
     * </p>
     */
    @Test
    public void testDeleteQuestion() {
        String expectedErrorMessage = "No Question Selected";
        String actualErrorMessage = deleteQuestion();
        assertEquals(expectedErrorMessage, actualErrorMessage, "Error Message Is Incorrect!");
    }

    /**
     * Tests the answerQuestion method when no question is selected.
     * <p>
     * Confirms that the returned error message is "No Question Selected".
     * If the test fails, a message will pop up saying "Error Message Is Incorrect!".
     * </p>
     */
    @Test
    public void testAnswerQuestion() {
        String expectedErrorMessage = "No Question Selected";
        String actualErrorMessage = answerQuestion();
        assertEquals(expectedErrorMessage, actualErrorMessage, "Error Message Is Incorrect!");
    }

    /**
     * Tests the resolveQuestion method when no question is selected.
     * <p>
     * Confirms that the returned error message is "No Question Selected".
     * If the test fails, a message will pop up saying "Error Message Is Incorrect!".
     * </p>
     */
    @Test
    public void testResolveQuestion() {
        String expectedErrorMessage = "No Question Selected";
        String actualErrorMessage = resolveQuestion();
        assertEquals(expectedErrorMessage, actualErrorMessage, "Error Message Is Incorrect!");
    }
}

