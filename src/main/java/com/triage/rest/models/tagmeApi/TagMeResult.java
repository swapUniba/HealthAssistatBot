package com.triage.rest.models.tagmeApi;

import java.util.ArrayList;

public class TagMeResult {
	private String timestamp;
	private ArrayList<Annotation> annotations;
	
	public TagMeResult(String timestamp, ArrayList<Annotation> annotations) {
		this.timestamp = timestamp;
		this.annotations = annotations;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public ArrayList<Annotation> getAnnotations() {
		return annotations;
	}
	
	public void setAnnotations(ArrayList<Annotation> annotations) {
		this.annotations = annotations;
	}
	
	private final String WIKIPEDIA_BASE_URL = "https://it.wikipedia.org/wiki/";
	public ArrayList<String> getWikipediaLinks(){
		ArrayList<String> links = new ArrayList<String>();
		for(Annotation ann : this.annotations){
			links.add(WIKIPEDIA_BASE_URL + ann.getTitle().replaceAll(" ", "_"));
		}
		
		return links;
	}
	
	@Override
	public String toString() {
		return "TagMeResult [timestamp=" + timestamp + ", annotations=" + annotations + "]";
	}
}
