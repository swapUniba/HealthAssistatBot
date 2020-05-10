package com.triage.rest.models.messages;

/**
 * Answer to a question
 * @author Domenico
 *
 */
public class Answer {
	private int id;
	private int questionid;
	private String text;
	private String classification;
	
	public Answer(int id, int questionid, String text, String classification) {
		this.id = id;
		this.questionid = questionid;
		this.text = text;
		this.classification = classification;
	}
	
	public Answer(int questionid, String text) {
		this.questionid = questionid;
		this.text = text;
	}
	
	public Answer(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuestionid() {
		return questionid;
	}

	public void setQuestionid(int questionid) {
		this.questionid = questionid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	@Override
	public String toString() {
		return "Answer [id=" + id + ", questionid=" + questionid + ", text=" + text + ", classification="
				+ classification + "]";
	}
}
