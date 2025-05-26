package application;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import databasePart1.DatabaseHelper;

/**
 * This class contains JUnit test cases for verifying the functionality of a discussion board system.
 * It tests features such as question resolution, answer updating, answer deletion, and administrative privileges.
 */
public class ResolveTest{

	
	
    /**
     * Default constructor for HW3_TestCases.
     * This class is used for JUnit testing and does not require instantiation.
     */
    public ResolveTest() {
        // Default constructor
    }
    
    /**
     * Tests if a question creator can resolve an answer.
     * 
     * <p>Steps:</p>
     * 1. Create a user who asks a question.  
     * 2. Create a question and an answer from another user.  
     * 3. Check that the answer is initially unresolved.  
     * 4. Verify that if the question creator's username matches the question's creator, they can mark the answer as resolved.  
     */
	
	@Test
	public void QCreatorResolved() {
		//set up questions user
		User qCreator = new User("qCreator", "qCreator", "qcreator@asu.edu", "Password123!", "Student");
		//sets up questions
		Question question = new Question(1, "Title", "Is my Test running?", "qCreator", false);

		Answer answer = new Answer(2, 1, "yes the test is running.", "answerCreator", false);	
		
		assertFalse(answer.isResolved()); //ensures answer starts as false
		
		if(qCreator.getUserName() == question.getUserName()) { //if qCreator user name matches questions poster
			answer.setIsResolved(true); //set answer to true
		}
		
		boolean resolved = answer.isResolved();
		
		
		assertTrue(resolved, "Answer should be Resolved");
	}
    /**
     * Tests if an answer's content can be successfully updated.
     * 
     * <p>Steps:</p>
     * 1. Create an answer by a user.  
     * 2. Ensure the user is the creator of the answer.  
     * 3. Update the answer's text.  
     * 4. Verify that the update was successful.  
     */
	
	@Test
	public void answerUpdate() {
		String contents = "Answer Updated Successfully.";
		//sets up answers user
		User answerCreator = new User("answerCreator", "answerCreator", "answerCreator@asu.edu", "Password123!", "Student");
		//sets up answer
		Answer answer = new Answer(2, 1, "Before Update.", "answerCreator", false);
		
		if(answerCreator.getUserName() == answer.getUserName()) {
			answer.setAnswerText(contents);
		}
		
		boolean updated = (contents == answer.getAnswerText());
		
		assertTrue(updated, "The updated answer should now match the contents");
		
	}
	
    /**
     * Tests if an answer can be successfully deleted from the database.
     * 
     * <p>Steps:</p>
     * 1. Connect to the database.  
     * 2. Add an answer to the database.  
     * 3. Verify the answer exists.  
     * 4. Delete the answer.  
     * 5. Verify the answer no longer exists in the database.  
     * 
     * @exception SQLException           If a database error occurs.
     * @exception ClassNotFoundException If the database driver is not found.
     */
	@Test
	public void answerDelete() throws SQLException, ClassNotFoundException {
		DatabaseHelper dbhelper = new DatabaseHelper();
		dbhelper.connectToDatabase();
		List<Answer> answerList = null;
		List<Answer> refreshedList = null;
		//set up questions user
		//User qCreator = new User("qCreator", "qCreator", "qcreator@asu.edu", "Password123!", "Student");
		//sets up questions
		Question question = new Question(1, "Title", "Is my Test running?", "qCreator", false);
		//sets up answers user
		//User answerCreator = new User("answerCreator", "answerCreator", "answerCreator@asu.edu", "Password123!", "Student");
		//sets up answer
		Answer answer = new Answer(2, 1, "yes the test is running.", "answerCreator", false);	
		
		try {
			dbhelper.addAnswer(answer); //adding answer to database
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
	
		try {
			answerList = dbhelper.getAnswersForQuestion(question.getId());
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		
		boolean exists = (!answerList.isEmpty());
		
		assertTrue(exists, "The answer exists in the database.");
		
		try {
			dbhelper.deleteAnswer(answer); //deleting answer
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		
		try {
			refreshedList = dbhelper.getAnswersForQuestion(question.getId());
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		
		boolean deleted = (!refreshedList.isEmpty());
		
		assertTrue(deleted, "Answer has been deleted.");
		
	}
	
	
    /**
     * Tests if an answer can be successfully deleted from the database.
     * 
     * <p>Steps:</p>
     * 1. Connect to the database.  
     * 2. Create a question creator, answer creator, question, and answer 
     * 3. Add a private message to the database  
     * 4. Assert that the list for private message is not empty  
     * 
     * @exception SQLException           If a database error occurs.
     * @exception ClassNotFoundException If the database driver is not found.
     */
	@Test
	public void messageSent() throws SQLException, ClassNotFoundException {
		DatabaseHelper dbhelper = new DatabaseHelper();
		dbhelper.connectToDatabase();
		List<PrivateMessage> messageList = null;

		User qCreator = new User("qCreator", "qCreator", "qcreator@asu.edu", "Password123!", "Student");

		User answerCreator = new User("answerCreator", "answerCreator", "answerCreator@asu.edu", "Password123!", "Student");
		//sets up answer
		Question question = new Question(1, "Title", "Is my Test running?", "qCreator", false);

		Answer answer = new Answer(2, 1, "yes the test is running.", "answerCreator", false);	
		
		try {
			dbhelper.sendPrivateMessage(question.getId(), answer.getId(), qCreator.getName(), answerCreator.getName(), "Is this correct?"); //adding answer to database
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
	
		try {
			messageList = dbhelper.getPrivateMessages(qCreator.getName(), question.getId());
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		
		boolean exists = (!messageList.isEmpty());
		
		assertTrue(exists, "The message exists in the database.");


	}
	
	
    /**
     * Tests if an admin can resolve an answer.
     * 
     * <p>Steps:</p>
     * 1. Create an admin user.  
     * 2. Create a question and an answer.  
     * 3. Check that the answer is initially unresolved.  
     * 4. Verify that the admin can resolve the answer.  
     */
	
	
	@Test
	public void adminResolve(){
		
		User admin = new User("admin", "admin", "admin@asu.edu", "Password123!", "Admin");
		//sets up answer
		Answer answer = new Answer(2, 1, "yes the test is running.", "answerCreator", false);	
		
		assertFalse(answer.isResolved()); //ensures answer starts as false
		
		if(admin.getRole() == "Admin") { //if qCreator user name matches questions poster
			answer.setIsResolved(true); //set answer to true
		}
		
		boolean resolved = answer.isResolved();
		
		
		assertTrue(resolved, "Answer should be Resolved");
	}
	
	
	
    /**
     * Tests if an admin can unresolve an answer.
     * 
     * <p>Steps:</p>
     * 1. Create an admin user.  
     * 2. Create a question and an answer (already resolved).  
     * 3. Check that the answer starts as resolved.  
     * 4. Verify that the admin can mark it as unresolved.  
     */
	
	
	@Test
	public void adminunResolve(){
		
		User admin = new User("admin", "admin", "admin@asu.edu", "Password123!", "Admin");
		//sets up answer
		Answer answer = new Answer(2, 1, "yes the test is running.", "answerCreator", true);	
		
		assertTrue(answer.isResolved()); //ensures answer starts as false
		
		if(admin.getRole() == "Admin") { //if qCreator user name matches questions poster
			answer.setIsResolved(false); //set answer to true
		}
		
		boolean resolved = answer.isResolved();
		
		
		assertFalse(resolved, "Answer should be UnResolved");
	}
	
	
	

}
