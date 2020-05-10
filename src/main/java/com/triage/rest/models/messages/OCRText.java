package com.triage.rest.models.messages;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class OCRText {
	private String photoID;
	private String text;

	public OCRText(String photoID, String text) {
		this.photoID = photoID;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPhotoID() {
		return photoID;
	}

	public void setPhotoID(String photoID) {
		this.photoID = photoID;
	}

	@Override
	public String toString() {
		return "OCRText [photoID=" + photoID + ", text=" + text + "]";
	}
}
