package application;

import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * @author Sat Chidananda, Justin Aussie, Joanna Gavlik, Justin Lee, Ilya Tate, Aydin Rahman.
 * @version 3/29/2025
 */

/**
 * This class contains JUnit test cases for the AnswerListPage and Answer class.
 * It tests various functionalities such as viewing, posting, resolving, updating, and deleting answers.
 */
public class AnswerListPageTest {
    private DatabaseHelper databaseHelper;
    private User adminUser;
    private Question question;
    private AnswerListPage answerlistpage;

    /**
     * Sets up the test environment before each test case.
     * Initializes the database connection, creates an admin user, a question, and an answer.
     * Also initializes the {@link AnswerListPage} instance for testing.
     *
     * @throws SQLException if there is an issue with the database connection.
     */
    @BeforeEach
    public void setUp() throws SQLException {

        // Connect to the original database
        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase();
        // Create admin user
        adminUser = new User("admin", "admin", "admin@gmail.com", "Abc*1234", "admin");
        // Create the question
        question = new Question(1, "This is a question.", "Question.", "admin", false);
        // Create AnswerListPage
        answerlistpage = new AnswerListPage(databaseHelper, question, adminUser);
    }

    /**
     * Cleans up the test environment after each test case.
     * Closes the database connection to ensure no resource leaks.
     */
    @AfterEach
    public void tearDown() {
        databaseHelper.closeConnection();
    }

    /**
     * Tests the functionality of viewing an answer.
     * Verifies that the answer is correctly fetched from the database and matches the expected values.
     * Checks to see if an answer has been selected before doing CRUD operations.
     *
     * @throws SQLException if there is an issue with the database query.
     */
    @Test
    public void testViewAnswer_Success() throws SQLException {
        List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getId());

        // If no answer has been selected then match the error
        if (answerlistpage.isSelected == false) {
            assertEquals("No Answer Selected", answerlistpage.no_selection_error);
        } 
        else {
            // Check that the answer list is not empty
            assertFalse(answers.isEmpty());

            // Check if the answer matches expected values
            Answer fetchedAnswer = answers.get(0);
            assertEquals("This is an answer.", fetchedAnswer.getAnswerText());
            assertEquals("admin", fetchedAnswer.getUserName());
        }
    }

    /**
     * Tests the functionality of posting a new answer.
     * Verifies that the new answer is successfully added to the database and can be retrieved.
     * Checks to see if an answer has been selected before doing CRUD operations.
     *
     * @throws SQLException if there is an issue with the database query.
     */
    @Test
    public void testPostAnswer_Success() throws SQLException {
        // If no answer has been selected then match the error
        if (answerlistpage.isSelected == false) {
            assertEquals("No Answer Selected", answerlistpage.no_selection_error);
        } else {
            Answer newAnswer = new Answer(2, question.getId(), "Another test answer.", "admin", false);

            // Add new answer to the database
            databaseHelper.addAnswer(newAnswer);

            List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getId());

            // Check that 2 answers now exist
            assertEquals(2, answers.size());
            assertEquals("Another test answer.", answers.get(1).getAnswerText());
        }
    }

    /**
     * Tests the functionality of resolving an answer.
     * Verifies that the answer is successfully marked as resolved in the database.
     * Checks to see if an answer has been selected before doing CRUD operations.
     *
     * @throws SQLException if there is an issue with the database query.
     */
    @Test
    public void testResolveAnswer_Success() throws SQLException {
        List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getId());

        // If no answer has been selected then match the error
        if (answerlistpage.isSelected == false) {
            assertEquals("No Answer Selected", answerlistpage.no_selection_error);
        } else {
            // Select the first answer and resolve it
            Answer answerToResolve = answers.get(0);
            databaseHelper.updateResolve(answerToResolve, true);

            // Fetch updated answers and verify resolution
            List<Answer> updatedAnswers = databaseHelper.getAnswersForQuestion(question.getId());
            assertTrue(updatedAnswers.get(0).isResolved());
        }
    }

    /**
     * Tests the functionality of updating an answer.
     * Verifies that the answer text is successfully updated in the database.
     * Checks to see if an answer has been selected before doing CRUD operations.
     *
     * @throws SQLException if there is an issue with the database query.
     */
    @Test
    public void testUpdateAnswer_Success() throws SQLException {
        List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getId());

        // If no answer has been selected then match the error
        if (answerlistpage.isSelected == false) {
            assertEquals("No Answer Selected", answerlistpage.no_selection_error);
        } else {
            // Select the answer and update its text
            Answer answerToUpdate = answers.get(0);
            answerToUpdate.setAnswerText("Updated answer text.");
            databaseHelper.updateAnswer(answerToUpdate);

            // Fetch updated answers and verify change
            List<Answer> updatedAnswers = databaseHelper.getAnswersForQuestion(question.getId());
            assertEquals("Updated answer text.", updatedAnswers.get(0).getAnswerText());
        }
    }

    /**
     * Tests the functionality of deleting an answer.
     * Verifies that the answer is successfully removed from the database.
     * Checks to see if an answer has been selected before doing CRUD operations.
     *
     * @throws SQLException if there is an issue with the database query.
     */
    @Test
    public void testDeleteAnswer_Success() throws SQLException {
        List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getId());

        // If no answer has been selected then match the error
        if (answerlistpage.isSelected == false) {
            assertEquals("No Answer Selected", answerlistpage.no_selection_error);
        } 
        else {
            // Delete the answer
            Answer answerToDelete = answers.get(0);
            databaseHelper.deleteAnswer(answerToDelete);

            // Check that the answer list is now empty
            List<Answer> updatedAnswers = databaseHelper.getAnswersForQuestion(question.getId());
            assertTrue(updatedAnswers.isEmpty());
        }
    }
}