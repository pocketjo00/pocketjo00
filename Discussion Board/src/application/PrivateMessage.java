package application;

import java.sql.Timestamp;

public class PrivateMessage {
	private int id;
	private int questionId;
	private Integer answerId;
	private String sender;
	private String receiver;
	private String message;
	private Timestamp time;
	private boolean isRead;
	
	// Default constructor
    public PrivateMessage() {
    }

    // Normal Constructor
	public PrivateMessage(int id, int questionId, Integer answerId, String sender, String receiver, String message, Timestamp time, boolean isRead) {
		this.id = id;
		this.questionId = questionId;
		this.answerId = answerId;
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.time = time;
		this.isRead = isRead;
	}
	
	// Getters
	public int getId() {
		return id;
	}
	public int getQuestionId() {
		return questionId;
	}
	public int getAnswerId() {
		return answerId;
	}
	public String getSender() {
		return sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public String getMessage() {
		return message;
	}
	public Timestamp getTime() {
		return time;
	}
	public boolean getIsRead() {
		return isRead;
	}
	
	// Setters
	public void setId(int id) {
		this.id = id;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public void setIsRead(boolean isRead) {
		this.isRead = isRead;
	}
	
	// Converts the message to a formatted String for the receiver
	public String toString() {
		return "From: " + sender + "(" + time + "), READ: " + isRead;
	}
}
