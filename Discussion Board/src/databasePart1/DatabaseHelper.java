package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.UUID;

import application.User;
import application.Question;
import application.Answer;
import application.PrivateMessage;
import application.Review;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, handling invitation codes and one time passwords.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	public void createTables() throws SQLException {
		// Create the user table
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "name VARCHAR(255), "
				+ "email VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(255))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "dateGenerated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // Create the one time passwords table
	    String oneTimePasswordsTable = "CREATE TABLE IF NOT EXISTS OneTimePasswords ("
	    		+ "userName VARCHAR(255) PRIMARY KEY, "
	    		+ "otp VARCHAR(20), "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(oneTimePasswordsTable);
	    
	    // Create the questions table
        String questionsTable = "CREATE TABLE IF NOT EXISTS questions ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "questionTitle VARCHAR(100), "
                + "questionText VARCHAR(1000), "
                + "userName VARCHAR(255), "
                + "isResolved BOOLEAN DEFAULT FALSE)";
        statement.execute(questionsTable);
        
        // Create the answers table
        String answersTable = "CREATE TABLE IF NOT EXISTS answers (" 
                + "answerId INT AUTO_INCREMENT PRIMARY KEY, "
                + "questionId INT, "
                + "answerText VARCHAR(1000), "
                + "userName VARCHAR(255), "
                + "isResolved BOOLEAN DEFAULT FALSE) ";
        statement.execute(answersTable);
        
        // Create the pending reviewer requests table
        String reviewerRequestsTable = "CREATE TABLE IF NOT EXISTS ReviewerRequests ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255), "
                + "status VARCHAR(20) DEFAULT 'pending')"; // Status can be 'pending', 'accepted', or 'declined'
        statement.execute(reviewerRequestsTable);
                
        // Create private messages table
        String privateMessagesTable = "CREATE TABLE IF NOT EXISTS PrivateMessages ("
        	    + "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
        	    + "id INT AUTO_INCREMENT PRIMARY KEY, "
        	    + "questionId INT, "
        	    + "answerId INT, "
        	    + "sender VARCHAR(255), "
        	    + "receiver VARCHAR(255), "
        	    + "message VARCHAR(1000), "
        	    + "isRead BOOLEAN DEFAULT FALSE) ";
        
        statement.execute(privateMessagesTable);

		String createTrustedAnswerersTable = "CREATE TABLE IF NOT EXISTS trusted_answerers (" +
					"id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
					"username VARCHAR(255) NOT NULL, " +
					"trusted_user_name VARCHAR(255) NOT NULL, " +
					"UNIQUE(username, trusted_user_name)) "; // Ensures a user can’t add the same trusted user twice

		statement.execute(createTrustedAnswerersTable);
		
		// Table for reviews for an answer
		String reviewsTable = "CREATE TABLE IF NOT EXISTS reviews (" +
		        "review_id INT AUTO_INCREMENT PRIMARY KEY, " +
		        "answer_id INT, " +
		        "user_name VARCHAR(255), " +
		        "review_text VARCHAR(1000), " +
		        "score INT DEFAULT 0, " + 
		        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
		        "FOREIGN KEY (answer_id) REFERENCES answers(answerId) ON DELETE CASCADE, " +
		        "FOREIGN KEY (user_name) REFERENCES cse360users(userName))";
		statement.execute(reviewsTable);
			
			// Table for tracking votes
			String reviewVotesTable = "CREATE TABLE IF NOT EXISTS review_votes (" +
			    "vote_id INT AUTO_INCREMENT PRIMARY KEY, " +
			    "review_id INT, " +
			    "user_name VARCHAR(255), " +
			    "vote_type INT, " +  // 1 for upvote, -1 for downvote
			    "UNIQUE (review_id, user_name), " +  // Prevent duplicate votes
			    "FOREIGN KEY (review_id) REFERENCES reviews(review_id), " +
			    "FOREIGN KEY (user_name) REFERENCES cse360users(userName))";
			statement.execute(reviewVotesTable);
			
			// Table to Track flagged users
			String flaggedUsersTable = "CREATE TABLE IF NOT EXISTS flagged_users (" +
			    "flag_id INT AUTO_INCREMENT PRIMARY KEY, " +
			    "user_id INT, " +
			    "username VARCHAR(255), " +
			    "FOREIGN KEY (user_id) REFERENCES cse360users(id))";
			statement.execute(flaggedUsersTable);
		}



	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, name, email, password, role) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getName());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getPassword());
			pstmt.setString(5, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Updates the user's password after they have reset it
	public void updateUserPassword(String userName, String newPassword) {
		String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } 
	    catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Checks if the user's userName, name, email is valid
	public boolean verifyUserCredentials(String userName, String name, String email) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ? AND name = ? AND email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        pstmt.setString(2, name);
			pstmt.setString(3, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role"); // Retrieve stored roles
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Clears out all the codes that are older than 60 seconds.
	public void clearExpiredCodes() {
		String query = "DELETE FROM InvitationCodes WHERE " + "DATEDIFF('SECOND', dateGenerated, CURRENT_TIMESTAMP) > 60";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
		clearExpiredCodes();
		
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Checks to see if validation code has been timed out.
	public boolean isTimedOut(String code) {
		String checkQuery = "SELECT *, DATEDIFF('SECOND', dateGenerated, CURRENT_TIMESTAMP) as age " + "FROM InvitationCodes WHERE code = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getBoolean("isUsed")) {
					// Timed out from code already being used
					return true;
				}
				if (rs.getInt("age") > 60) {
					// Timed out from being too old
					return true;
				}
				markInvitationCodeAsUsed(code);
				return false; // Valid code
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true; // Otherwise invalid
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? " + "AND isUsed = FALSE " + "AND DATEDIFF('SECOND', dateGenerated, CURRENT_TIMESTAMP) <= 60";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Generates a one time password, inserts it into the database, and sets it to the user's password.
	public void generateOneTimePassword(String userName) {
	    String otp = UUID.randomUUID().toString().substring(0, 8); // Generate a random 8-character one time password
	    String query = "MERGE INTO OneTimePasswords (userName, otp, isUsed) KEY(userName) VALUES (?, ?, FALSE)";
	    String query2 = "UPDATE cse360users SET password = ? where userName = ?";
	    String password = otp;
	    
	    try {
	    	// Inserts the one time password in the database
	    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    		pstmt.setString(1, userName);
	    		pstmt.setString(2, otp);
	    		pstmt.executeUpdate();
	    	}
	    	// Sets the one time password to the user's password
	    	try (PreparedStatement pstmt2 = connection.prepareStatement(query2)) {
	    		pstmt2.setString(1, password);
	    		pstmt2.setString(2, userName);
	    		pstmt2.executeUpdate();
	    	}
	    	
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Allows the admin to retrieve the one time password for the user
	public String retrieveOneTimePassword() {
		String query = "SELECT otp FROM OneTimePasswords";
		String otp = null; 
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				otp = rs.getString("otp"); // retrieve one time password
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return otp;
	}
	
	// Validates the one time password to check if it is unused.
	public boolean validateOneTimePassword(String userName, String otp) {
	    String query = "SELECT * FROM OneTimePasswords WHERE userName = ? AND otp = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.setString(2, otp);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the one time password as used
	            markOneTimePasswordAsUsed(userName);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the one time password as used in the database.
	private void markOneTimePasswordAsUsed(String userName) {
	    String query = "UPDATE OneTimePasswords SET isUsed = TRUE WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	// Fetch all registered users with their roles
	// Fetch all registered users with their roles
	public List<String> getAllUsersWithRoles() throws SQLException {
	    List<String> users = new ArrayList<>();
	    String query = "SELECT userName, name, email, role FROM cse360users";
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            String user = String.format("%s (%s)(%s)(%s)", rs.getString("userName"),
						rs.getString("role"), rs.getString("name"), rs.getString("email"));
	            users.add(user);
	        }
	    }
	    return users;
	}
	// Updates a user's role after selection by admin.
	public void updateUserRoles(String userName, String newRoles) throws SQLException {
	    String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newRoles); // Store multiple roles as a comma-separated string
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    }
	}
	// Method to delete a user
	public void deleteUser(String userName) throws SQLException {
	    String query = "DELETE FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.executeUpdate();
	    }
	}
	
	// Add a Question to the Database
	public void addQuestion(Question question) throws SQLException {
	    String query = "INSERT INTO questions (questionTitle, questionText, userName) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, question.getQuestionTitle());
	        pstmt.setString(2, question.getQuestionText());
	        pstmt.setString(3, question.getUserName());
	        pstmt.executeUpdate();
	    }
	}
	
	// Update question in Database
	public void updateQuestion(Question question) throws SQLException {
	    String query = "UPDATE questions SET questionTitle = ?, questionText = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, question.getQuestionTitle());
	        pstmt.setString(2, question.getQuestionText());
	        pstmt.setInt(3, question.getId());
	        pstmt.executeUpdate();
	    }
	}
	
	// Delete a Question from the Database
	public void deleteQuestion(Question question) throws SQLException {
	    String query = "DELETE FROM questions WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, question.getId());
	        pstmt.executeUpdate();
	    }
	}
	
	// Add an Answer to a Question in the Database
	public void addAnswer(Answer answer) throws SQLException {
	    String query = "INSERT INTO answers (questionId, answerText, userName, isResolved) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, answer.getQuestionId());
	        pstmt.setString(2, answer.getAnswerText());
	        pstmt.setString(3, answer.getUserName());
	        pstmt.setBoolean(4, answer.isResolved());
	        pstmt.executeUpdate();
	    }
	}
	
	// Update answer in Database
	public void updateAnswer(Answer answer) throws SQLException {
	    String query = "UPDATE answers SET answerText = ? WHERE answerId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, answer.getAnswerText());
	        pstmt.setInt(2, answer.getId());
	        pstmt.executeUpdate();
	    }
	}

	// Delete answer from Database
	public void deleteAnswer(Answer answer) throws SQLException {
	    String query = "DELETE FROM answers WHERE answerId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, answer.getId());
	        pstmt.executeUpdate();
	    }
	}
	
	// Get all the Questions from the Database
	public List<Question> getAllQuestions() throws SQLException {
	    List<Question> questions = new ArrayList<>();
	    String query = "SELECT * FROM questions";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String title = rs.getString("questionTitle");
	            String text = rs.getString("questionText");
	            String userName = rs.getString("userName");
	            Boolean isResolved = rs.getBoolean("isResolved");
	            questions.add(new Question(id, title, text, userName, isResolved));
	        }
	    }
	    return questions;
	}
	
	// Get all the Messages from the Database
	public List<PrivateMessage> getAllMessages() throws SQLException {
        List<PrivateMessage> messages = new ArrayList<>();

        String sql = "SELECT * FROM PrivateMessages";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            PrivateMessage msg = new PrivateMessage();
            msg.setId(rs.getInt("id"));
            msg.setTime(rs.getTimestamp("time"));
            msg.setSender(rs.getString("sender"));
            msg.setReceiver(rs.getString("receiver"));
            msg.setMessage(rs.getString("message"));
            msg.setIsRead(rs.getBoolean("isRead"));
            // Populate additional fields if necessary.
            messages.add(msg);
        }
        
        rs.close();
        ps.close();
        return messages;
    }
	
	// Get all the Answers to all Questions from the Database
	public List<Answer> getAllAnswers() throws SQLException {
	    List<Answer> answers = new ArrayList<>();
	    String query = "SELECT * FROM answers";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            int answerId = rs.getInt("answerId");
	            int questionId = rs.getInt("questionId");
	            String answerText = rs.getString("answerText");
	            String userName = rs.getString("userName");
	            Boolean isResolved = rs.getBoolean("isResolved");
	            answers.add(new Answer(answerId, questionId, answerText, userName, isResolved));
	        }
	    }
	    return answers;
	}
	
	// Get all the answers to a Question from the Database
	public List<Answer> getAnswersForQuestion(int questionId) throws SQLException {
	    List<Answer> answers = new ArrayList<>();
	    String query = "SELECT * FROM answers WHERE questionId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            int answerId = rs.getInt("answerId");
	            String answerText = rs.getString("answerText");
	            String userName = rs.getString("userName");
	            Boolean isResolved = rs.getBoolean("isResolved");
	            answers.add(new Answer(answerId, questionId, answerText, userName, isResolved));
	        }
	    }
	    return answers;
	}
	
	// Update whether an answer has been resolved or not
	public void updateResolve(Answer answer, boolean val) {
		String query = "UPDATE answers SET isResolved = ? WHERE answerId = ?";
		
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
	        pstmt.setBoolean(1, val);  // Set the resolved value
	        pstmt.setInt(2, answer.getId());  // Set the answer ID
	        pstmt.executeUpdate();	
		} catch(SQLException e) {
			 e.printStackTrace();
		}
	}
	
	// Update whether a question has been resolved or not
	public void updateQuestionResolve(Question question, boolean val) {
		String query = "UPDATE questions SET isResolved = ? WHERE id = ?";
		
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
	        pstmt.setBoolean(1, val);  // Set the resolved value
	        pstmt.setInt(2, question.getId());  // Set the question ID
	        pstmt.executeUpdate();	
		} catch(SQLException e) {
			 e.printStackTrace();
		}
	}
	
	// Saves the user who requested the reviewer role
	public void requestReviewerRole(String userName) throws SQLException {
	    String query = "INSERT INTO ReviewerRequests (userName) VALUES (?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.executeUpdate();
	    }
	}
	
	// Returns a list of pending reviewer requests
	public List<String> getPendingReviewerRequests() throws SQLException {
	    List<String> requests = new ArrayList<>();
	    String query = "SELECT id, userName FROM ReviewerRequests WHERE status = 'pending'";
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            String user = rs.getString("userName");
	            requests.add("Pending Reviewer Request From: " + user);
	        }
	    }
	    return requests;
	}

	// Admin/Instructor updates the user's reviewer role request
	public void updateReviewerRequestStatus(int requestId, String status) throws SQLException {
	    String query = "UPDATE ReviewerRequests SET status = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, status);
	        pstmt.setInt(2, requestId);
	        pstmt.executeUpdate();
	    }
	}
	
	// Retrieve id of the pending reviewer request
	public int getPendingReviewerRequestIdForUser(String userName) throws SQLException {
	    String query = "SELECT id FROM ReviewerRequests WHERE userName = ? AND status = 'pending'";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("id");
	            }
	        }
	    }
	    return -1;
	}
	
	// Adds private message to database
	public void sendPrivateMessage(int questionId, Integer answerId, String sender, String receiver, String message) throws SQLException {
		String query = "INSERT INTO privateMessages (questionId, answerId, sender, receiver, message) VALUES(?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        
	        // Takes care of null case, otherwise type issues
	        if (answerId == null || answerId <= 0) {
	        	pstmt.setNull(2,  java.sql.Types.INTEGER);
	        } else {
	        	pstmt.setInt(2,  answerId);
	        }
	        pstmt.setString(3, sender);
	        pstmt.setString(4, receiver);
	        pstmt.setString(5, message);
	        pstmt.executeUpdate();
		}
	}
	
	// List of private messages for specified question
	public List<PrivateMessage> getPrivateMessages(String name, int questionId) throws SQLException {
		List<PrivateMessage> privateMessages = new ArrayList<>();
		
	    String query = "SELECT * FROM privateMessages WHERE (sender = ? OR receiver = ?) AND questionId = ? ORDER BY time ASC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, name);
	        pstmt.setString(2, name);
	        pstmt.setInt(3, questionId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	        	int id = rs.getInt("id");
	        	int question = rs.getInt("questionId");
	        	
	        	Integer answer = null;
	        	// Check for not null
	        	int tmp = rs.getInt("answerId");
	        	if (!rs.wasNull()) {
	        		answer = tmp;
	        	}
	        			
	        	String sender = rs.getString("sender");
	        	String receiver = rs.getString("receiver");
	        	String message = rs.getString("message");
	        	Timestamp time = rs.getTimestamp("time");
	        	boolean isRead = rs.getBoolean("isRead");
	        	PrivateMessage privateMessage = new PrivateMessage(id, question, answer, sender, receiver, message, time, isRead);
	        	privateMessages.add(privateMessage);
	        }
	    }
	    
	    return privateMessages;
	}
	
	// Finds number of unread messages that a user has
	public int countUnreadMessages(String name) throws SQLException {
	    String query = "SELECT COUNT(*) FROM privateMessages WHERE receiver = ? AND isRead = FALSE";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, name);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    }
	    
	    return 0;
	}
	
	// Marks messages as read
	public void readMessage(int id) throws SQLException {
	    String query = "UPDATE privateMessages SET isRead = TRUE WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);
	        pstmt.executeUpdate();
	    }
	}

	public boolean isTrustedAnswerer(String currentUser, String answerer) throws SQLException {
		String query = "SELECT COUNT(*) FROM trusted_answerers WHERE username = ? AND trusted_user_name = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, currentUser);
			stmt.setString(2, answerer);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		}
		return false;
	}

	public void addTrustedAnswerer(String currentUser, String answerer) throws SQLException {
		String query = "INSERT INTO trusted_answerers (username, trusted_user_name) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, currentUser);
			pstmt.setString(2, answerer);
			pstmt.executeUpdate();
		}
	}

	public List<String> getTrustedAnswerers(String userName) throws SQLException {
		List<String> trustedAnswerers = new ArrayList<>();
		String query = "SELECT trusted_user_name FROM trusted_answerers WHERE username = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				trustedAnswerers.add(rs.getString("trusted_user_name"));
			}
		}
		return trustedAnswerers;
	}
	
	public void addReview(Review review) throws SQLException {
	    String query = "INSERT INTO reviews (answer_id, user_name, review_text, score) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, review.getAnswerId());
	        pstmt.setString(2, review.getUserName());
	        pstmt.setString(3, review.getReviewText());
	        pstmt.setInt(4, review.getScore());  // Changed from rating to score
	        pstmt.executeUpdate();
	    }
	}

	public List<Review> getReviewsForAnswer(int answerId) throws SQLException {
	    List<Review> reviews = new ArrayList<>();
	    String query = "SELECT r.*, u.name FROM reviews r " +
	                   "JOIN cse360users u ON r.user_name = u.userName " +
	                   "WHERE r.answer_id = ? ORDER BY r.created_at DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, answerId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            Review review = new Review(
	                rs.getInt("review_id"),
	                rs.getInt("answer_id"),
	                rs.getString("user_name"),
	                rs.getString("name"),
	                rs.getString("review_text"),
	                rs.getInt("score"),  // Changed from rating to score
	                rs.getTimestamp("created_at")
	            );
	            reviews.add(review);
	        }
	    }
	    return reviews;
	}

	public void updateReview(Review review) throws SQLException {
	    String query = "UPDATE reviews SET review_text = ?, score = ? WHERE review_id = ? AND user_name = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, review.getReviewText());
	        pstmt.setInt(2, review.getScore());  // Changed from rating to score
	        pstmt.setInt(3, review.getReviewId());
	        pstmt.setString(4, review.getUserName());
	        pstmt.executeUpdate();
	    }
	}

	public void deleteReview(int reviewId, String userName) throws SQLException {
		
		// First delete all votes for this review
	    String deleteVotes = "DELETE FROM review_votes WHERE review_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteVotes)) {
	        pstmt.setInt(1, reviewId);
	        pstmt.executeUpdate();
	    }
	    
	    String query = "DELETE FROM reviews WHERE review_id = ? AND user_name = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewId);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    }
	}

	public boolean isReviewOwner(int reviewId, String userName) throws SQLException {
	    String query = "SELECT COUNT(*) FROM reviews WHERE review_id = ? AND user_name = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewId);
	        pstmt.setString(2, userName);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next() && rs.getInt(1) > 0;
	    }
	}
	
	public void addVote(int reviewId, String userName, int voteType) throws SQLException {
	    String query = "MERGE INTO review_votes (review_id, user_name, vote_type) KEY(review_id, user_name) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewId);
	        pstmt.setString(2, userName);
	        pstmt.setInt(3, voteType);
	        pstmt.executeUpdate();
	    }
	    
	    // Update the review score
	    String updateQuery = "UPDATE reviews SET score = score + ? WHERE review_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setInt(1, voteType);
	        pstmt.setInt(2, reviewId);
	        pstmt.executeUpdate();
	    }
	}

	public int getUserVote(int reviewId, String userName) throws SQLException {
	    String query = "SELECT vote_type FROM review_votes WHERE review_id = ? AND user_name = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewId);
	        pstmt.setString(2, userName);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next() ? rs.getInt("vote_type") : 0;
	    }
	}

	public boolean hasUserVoted(int reviewId, String userName) throws SQLException {
	    String query = "SELECT COUNT(*) FROM review_votes WHERE review_id = ? AND user_name = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewId);
	        pstmt.setString(2, userName);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next() && rs.getInt(1) > 0;
	    }
	}
	
	public List<Review> getReviewsByUser(String userName) throws SQLException {
	    List<Review> reviews = new ArrayList<>();
	    String query = "SELECT r.*, u.name FROM reviews r " +
	                   "JOIN cse360users u ON r.user_name = u.userName " +
	                   "WHERE r.user_name = ? ORDER BY r.created_at DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            Review review = new Review(
	                rs.getInt("review_id"),
	                rs.getInt("answer_id"),
	                rs.getString("user_name"),
	                rs.getString("name"),
	                rs.getString("review_text"),
	                rs.getInt("score"),
	                rs.getTimestamp("created_at")
	            );
	            reviews.add(review);
	        }
	    }
	    return reviews;
	}
	
	// Method to retrieve all group chat messages.
    public List<PrivateMessage> getGroupChatMessages() throws SQLException {
        List<PrivateMessage> messages = new ArrayList<>();
        // We select only rows where questionId and answerId are NULL and receiver equals "GROUP"
        String sql = "SELECT * FROM PrivateMessages WHERE questionId IS NULL AND answerId IS NULL AND receiver = 'GROUP' ORDER BY time ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            PrivateMessage msg = new PrivateMessage();
            msg.setId(rs.getInt("id"));
            msg.setTime(rs.getTimestamp("time"));
            msg.setSender(rs.getString("sender"));
            msg.setReceiver(rs.getString("receiver"));
            msg.setMessage(rs.getString("message"));
            msg.setIsRead(rs.getBoolean("isRead"));
            // Populate additional fields if necessary.
            messages.add(msg);
        }
        
        rs.close();
        ps.close();
        return messages;
    }

    // Method to send a group chat message.
    public void sendGroupChatMessage(String sender, String message) throws SQLException {
        // Inserting a row with null for questionId and answerId and using a special receiver "GROUP"
        String sql = "INSERT INTO PrivateMessages (questionId, answerId, sender, receiver, message) VALUES (NULL, NULL, ?, 'GROUP', ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, sender);
        ps.setString(2, message);
        ps.executeUpdate();
        ps.close();
    }


	public boolean deletePrivateMessage(int messageId) throws SQLException {
		String query = "DELETE FROM PrivateMessages WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, messageId);
			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

	public List<MessageData> loadMessages() throws SQLException {
		List<MessageData> messages = new ArrayList<>();
		String query = "SELECT id, time, sender, message FROM PrivateMessages " +
				"ORDER BY time DESC";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				messages.add(new MessageData(
						rs.getInt("id"),
						rs.getTimestamp("time"),
						rs.getString("sender"),
						rs.getString("message")
				));
			}
		}
		return messages;
	}

	public static class MessageData {
		public final int id;
		public final Timestamp timestamp;
		public final String sender;
		public final String message;

		public MessageData(int id, Timestamp timestamp, String sender, String message) {
			this.id = id;
			this.timestamp = timestamp;
			this.sender = sender;
			this.message = message;
		}
	}
	
	// Flag a user for inappropriate behavior
	public boolean flagUser(String username) throws SQLException {
	    String query = "INSERT INTO flagged_users (username) VALUES (?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        return pstmt.executeUpdate() > 0;
	    }
	}
	
	// Unflag a user
	public boolean unflagUser(String username) throws SQLException {
	    String query = "DELETE FROM flagged_users (username) VALUES (?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        return pstmt.executeUpdate() > 0;
	    }
	}
	// Check if a user is flagged
	public boolean isUserFlagged(String userName) throws SQLException {
	    String query = "SELECT 1 FROM flagged_users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            return rs.next();
	        }
	    }
	}
	
	
	public List<String> getFlaggedUsers() throws SQLException {
	    List<String> flaggedUsers = new ArrayList<>();
	    String query = "SELECT username FROM flagged_users";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {
	        
	        while (rs.next()) {
	            flaggedUsers.add(rs.getString("username"));
	        }
	    }
	    return flaggedUsers;
	}
	
}


