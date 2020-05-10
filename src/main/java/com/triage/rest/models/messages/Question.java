package com.triage.rest.models.messages;

import java.util.ArrayList;

public abstract class Question {
	private int id;
	private String questionName;
	private String text;
	private String preText;
	private ArrayList<Answer> answers;
	private boolean setMenuButton;
	private boolean showChart;
	private ArrayList<String> imagesLink;
	private ArrayList<String> imagesSub;
	
	public Question(String questionName) {
		this.questionName = questionName;
		this.preText = null;
		this.setMenuButton = true;
		this.answers = new ArrayList<Answer>();
		this.imagesLink = new ArrayList<String>();
	}

	public String getPreText() {
		return preText;
	}

	public boolean isShowChart() {
		return showChart;
	}

	public void setShowChart(boolean showChart) {
		this.showChart = showChart;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public ArrayList<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<Answer> answers) {
		this.answers = answers;
	}

	public void addSingleAnswer(Answer a){
		if(this.answers == null)
			answers = new ArrayList<Answer>();
		
		this.answers.add(a);
	}
	
	public boolean isSetMenuButton() {
		return setMenuButton;
	}

	public void setMenuButton(boolean setMenuButton) {
		this.setMenuButton = setMenuButton;
	}

	public void setPreText(String preText){
		this.preText = preText;
	}
	
	public String getQuestionText(){
		if(this.preText != null)
			return this.preText + " " + this.text;
		return this.text;
	}
		
	public ArrayList<String> getImagesLink() {
		return imagesLink;
	}

	public void setImagesLink(ArrayList<String> imagesLink) {
		this.imagesLink = imagesLink;
	}

	
	public void addSingleImage(String link, String sub){
		if(this.imagesLink == null)
			this.imagesLink = new ArrayList<String>();
		if(this.imagesSub == null)
			this.imagesSub = new ArrayList<String>();
		
		this.imagesLink.add(link);
		this.imagesSub.add(sub);
	}
	
	public ArrayList<String> getImagesSub() {
		return imagesSub;
	}

	public void setImagesSub(ArrayList<String> imagesSub) {
		this.imagesSub = imagesSub;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", questionName=" + questionName + ", text=" + text + ", preText=" + preText
				+ ", answers=" + answers + ", setMenuButton=" + setMenuButton + ", imagesLink=" + imagesLink
				+ ", imagesSub=" + imagesSub + "]";
	}
}
