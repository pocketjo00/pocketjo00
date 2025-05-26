package application;

// Represents a question a user can ask on the discussion board

public class Question {
    private int id;
    private String questionTitle;
    private String questionText;
    private String userName;
    private boolean isResolved;

    public Question(int id, String questionTitle, String questionText, String userName, boolean isResolved) {
        this.id = id;
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.userName = userName;
        this.isResolved = isResolved;
    }
    
   // Retrieve Question Id 
    public int getId() {
        return id;
    }
    
    // Retrieve UserName of question poster
    public String getUserName() {
        return userName;
    }
    
    // Retrieve Question Title
    public String getQuestionTitle() {
    	return questionTitle;
    }
    
    // Set Question Title used to Update Question
    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }
    
    // Retrieve Question Text 
    public String getQuestionText() {
        return questionText;
    }
    
    // Set Question Text used to Update Question
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    // Formats Question in List View
    @Override
    public String toString() {
        return getQuestionTitle() + " (Posted by: " + getUserName() + ")";
    }
    
    // Check whether a question is resolved
    public boolean isResolved() {
    	return isResolved;
    }
    
    // Update whether a question is resolved
    public void setIsResolved(boolean isResolved) {
    	this.isResolved = isResolved;
    }
}