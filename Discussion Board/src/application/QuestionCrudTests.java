package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

/**
 * Unit tests for classes using crud.
 *
 * <p>
 * <ul>
 * <li>Question Create</li>
 * <li>Question Update</li>
 * <li>Question Delete</li>
 * <li>Question Resolve</li>
 * <li>Admin User Role Updates</li>
 * </ul>
 * </p>
 * 
 *
 * @author Sat Chidananda, Justin Aussie, Joanna Gavlik, Justin Lee, Ilya Tate,
 *         Aydin Rahman.Ilya Tate
 * @version 4/2/2025
 **/

public class QuestionCrudTests {
    /**
     * Main
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
    }

    /**
     * Question Create
     * Adds new question.
     * 
     * @param database connected to DatabaseHelper instance
     * @param Question params
     * @throws SQLException if database error occurs
     **/
    public static void testQuestionCreate(DatabaseHelper database) throws SQLException {
        Question question = new Question(0, "TestQuestionCrate", "Test: Create Question", "testor", false);
        database.addQuestion(question);

        System.out.println("\nCreate Question Passed");
    }

    /**
     * Question Update
     * Updates question.
     * 
     * @param database connected to DatabaseHelper instance
     * @param Question
     * @throws SQLException if database error occurs
     **/
    public static void testQuestionUpdate(DatabaseHelper database) throws SQLException {
        List<Question> questions = database.getAllQuestions();
        if (questions.size() == 0) {
            System.out.println("\nEmpty Database");
            return;
        } else {
            Question question = questions.getFirst();
            question.setQuestionTitle("Test: Update Question");
            database.updateQuestion(question);
        }

        System.out.println("\nUpdate Question Passed");
    }

    /**
     * Question Delete
     * Adds new question, then deletes it.
     * 
     * @param database connected to DatabaseHelper instance
     * @param Question
     * @throws SQLException if database error occurs
     **/
    public static void testQuestionDelete(DatabaseHelper database) throws SQLException {
        // Adds temp question to later be deleted
        Question question = new Question(0, "TMP", "tmp question", "Tmp Author", false);
        database.addQuestion(question);
        System.out.println("\nDelete Question Progress: Added question");

        database.deleteQuestion(question);
        System.out.println("\nDelete Question Passed");
    }

    /**
     * Question Resolve
     * Adds new question, then resolves if unresolved (+ vice versa)
     * 
     * @param database connected to DatabaseHelper instance
     * @param Question
     * @throws SQLException if database error occurs
     **/
    public static void testQuestionResolve(DatabaseHelper database) throws SQLException {
        Question question = new Question(0, "TMP", "tmp question", "Tmp Author", false);
        database.addQuestion(question);

        // Allows tests from either resolved or unresolved
        if (question.isResolved()) {
            database.updateQuestionResolve(question, true);
        } else {
            database.updateQuestionResolve(question, false);
        }
        System.out.println("\nResolve Question Passed");
    }

    /**
     * Admin Update
     * Updates admin
     * 
     * @param database connected to DatabaseHelper instance
     * @param User
     * @param String
     * @throws SQLException if database error occurs
     **/
    public static void testAdminUpdate(DatabaseHelper database) throws SQLException {
        String role;
        String newUsername = "Test";
        // Checks for admin and gives the role
        if (database.doesUserExist("admin")) {
            role = "admin";
        } else {
            return;
        }

        database.updateUserRoles(newUsername, role);
        System.out.println("Role After Update: " + role);

        System.out.println("Update Admin Passed");
    }
}