package com.triage.rest.models.messages;


public class QuestionText extends Question{

	public QuestionText(String questionName, String text) {
		super(questionName);
		this.setText(text);//this.text = text;
	}
}
