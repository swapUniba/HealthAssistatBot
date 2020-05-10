package com.triage.rest.models.messages;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"id", "chatId", "messageId", "isBot", "unixDate", "text"})
public class Message {
	private int id;
	private int chatId;
	private int messageId;
	private boolean isBot;
	private long unixTime;
	private String text;
	
	public Message(){}
	
	public Message(int chatId, boolean isBot, long unixTime){
		this.chatId = chatId;
		this.isBot = isBot;
		this.unixTime = unixTime;
	}
	
	public Message(int chatId, boolean isBot, long unixTime, String text){
		this.chatId = chatId;
		this.isBot = isBot;
		this.unixTime = unixTime;
		this.text = text;
	}
	
	//No id field
	public Message(int chatId, int messageId, boolean isBot, long unixTime, String text) {
		this.chatId = chatId;
		this.messageId = messageId;
		this.isBot = isBot;
		this.unixTime = unixTime;
		this.text = text;
	}
	
	//Complete constructor
	public Message(int id, int chatId, int messageId, boolean isBot, long unixTime, String text) {
		this.id = id;
		this.chatId = chatId;
		this.messageId = messageId;
		this.isBot = isBot;
		this.unixTime = unixTime;
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public boolean isBot() {
		return isBot;
	}

	public void setBot(boolean isBot) {
		this.isBot = isBot;
	}

	public long getDateMs() {
		return unixTime;
	}

	public void setDateMs(long unixTime) {
		this.unixTime = unixTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", chatId=" + chatId + ", messageId=" + messageId + 
				", isBot=" + isBot + ", unixTime=" + unixTime + ", text=" + text + "]";
	}
}
