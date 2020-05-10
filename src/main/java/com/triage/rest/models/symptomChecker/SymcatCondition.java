package com.triage.rest.models.symptomChecker;

import java.util.ArrayList;

public class SymcatCondition {
	String name;
	String url;
	String wikipedia;
	ArrayList<String> descriptions;
	ArrayList<String> senses;
	ArrayList<SymcatSymptomProb> symptoms;
	
	public SymcatCondition(String name, String url, String wikipedia, ArrayList<String> descriptions,
			ArrayList<String> senses, ArrayList<SymcatSymptomProb> symptoms) {
		this.name = name;
		this.url = url;
		this.wikipedia = wikipedia;
		this.descriptions = descriptions;
		this.senses = senses;
		this.symptoms = symptoms;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getWikipedia() {
		return wikipedia;
	}
	
	public void setWikipedia(String wikipedia) {
		this.wikipedia = wikipedia;
	}
	
	public ArrayList<String> getDescriptions() {
		return descriptions;
	}
	
	public void setDescriptions(ArrayList<String> descriptions) {
		this.descriptions = descriptions;
	}
	
	public ArrayList<String> getSenses() {
		return senses;
	}
	
	public void setSenses(ArrayList<String> senses) {
		this.senses = senses;
	}
	
	public ArrayList<SymcatSymptomProb> getSymptoms() {
		return symptoms;
	}
	
	public ArrayList<String> getSymptomsUrls() {
		ArrayList<String> symptoms = new ArrayList<String>();
		for(SymcatSymptomProb ssp : this.symptoms){
			symptoms.add(ssp.getName());
		}
		
		return symptoms;
	}
	
	public void setSymptoms(ArrayList<SymcatSymptomProb> symptoms) {
		this.symptoms = symptoms;
	}
	
	@Override
	public String toString() {
		return "SymcatCondition [name=" + name + ", url=" + url + ", wikipedia=" + wikipedia + ", descriptions="
				+ descriptions + ", senses=" + senses + ", symptoms=" + symptoms + "]";
	}
}
