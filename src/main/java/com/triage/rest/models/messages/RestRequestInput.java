package com.triage.rest.models.messages;

public class RestRequestInput {
	private String chatId;
	private String messageId;
	private String date;
	private String firstname;
	private String lastname;
	private String username;
	private String text;
	private String photoId;
	
	public RestRequestInput(String chatId, String messageId, String date, String firstname, String lastname,
			String username, String text) {
		this.chatId = chatId;
		this.messageId = messageId;
		this.date = date;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.text = text;
		this.photoId = null;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	@Override
	public String toString() {
		return "RestRequestInput [chatId=" + chatId + ", messageId=" + messageId + ", date=" + date + ", firstname="
				+ firstname + ", lastname=" + lastname + ", username=" + username + ", text=" + text + ", photoId="
				+ photoId + "]";
	}	
}
