package application;

// Represents an answer to a discussion question.

public class Answer {
    private int id;
    private int questionId;
    private String answerText;
    private String userName;
    private boolean isResolved;
    
    public Answer(int id, int questionId, String answerText, String userName, boolean isResolved) {
        this.id = id;
        this.questionId = questionId;
        this.answerText = answerText;
        this.userName = userName;
        this.isResolved = isResolved;
    }
    
    // Retrieve Answer Id
    public int getId() {
        return id;
    }
    
    // Retrieve Question Id
    public int getQuestionId() {
        return questionId;
    }
    
    // Retrieve userName of answer poster
    public String getUserName() {
    	return userName;
    }
    
    // Retrieve Answer to a Question
    public String getAnswerText() {
        return answerText;
    }
    
    // Set the Answer to a Question used to update the answer
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    
    // Formats Answer in List View
    @Override
    public String toString() {
        return "Answer by " + getUserName();
    }
    
    // Check whether an answer is resolved
    public boolean isResolved() {
    	return isResolved;
    }
    
    // Update whether an answer is resolved
    public void setIsResolved(boolean isResolved) {
    	this.isResolved = isResolved;
    }
    
}