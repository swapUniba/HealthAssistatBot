package com.triage.rest.models.tagmeApi;

import com.google.gson.annotations.SerializedName;

public class Annotation {
	private String title;
	
	@SerializedName("link_probability") 
	double probability;
	
	public Annotation(String title, double probability) {
		super();
		this.title = title;
		this.probability = probability;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public String toString() {
		return "Annotation [title=" + title + ", probability=" + probability + "]";
	}
}
