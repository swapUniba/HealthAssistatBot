package com.triage.rest.models.symptomChecker;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SymcatSymptom {
	String name;
	String nameEN;
	
	String url;
	
	String wikipedia;
	String wikipediaEN;
	
	ArrayList<String> descriptions;
	ArrayList<String> descriptionsEN;
	
	ArrayList<String> senses;
	ArrayList<String> sensesEN;
	
	@SerializedName("correlate_symptoms")
	ArrayList<SymcatSymptomProb> correlatedSymptoms;

	public SymcatSymptom(String name, String url, String wikipedia, ArrayList<String> descriptions,
			ArrayList<String> senses, ArrayList<SymcatSymptomProb> correlatedSymptoms) {
		super();
		this.name = name;
		this.url = url;
		this.wikipedia = wikipedia;
		
		this.descriptionsEN = new ArrayList<String>();
		this.descriptions = descriptions;
		
		this.senses = senses;
		this.sensesEN = new ArrayList<String>();
		
		this.correlatedSymptoms = correlatedSymptoms;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameEN() {
		return nameEN;
	}

	public void setNameEN(String nameEN) {
		this.nameEN = nameEN;
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

	public String getWikipediaEN() {
		return wikipediaEN;
	}

	public void setWikipediaEN(String wikipediaEN) {
		this.wikipediaEN = wikipediaEN;
	}

	public ArrayList<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(ArrayList<String> descriptions) {
		this.descriptions = descriptions;
	}

	public ArrayList<String> getDescriptionsEN() {
		return descriptionsEN;
	}

	public void setDescriptionsEN(ArrayList<String> descriptionsEN) {
		this.descriptionsEN = descriptionsEN;
	}
	
	public ArrayList<String> getSenses() {
		return senses;
	}

	public void setSenses(ArrayList<String> senses) {
		this.senses = senses;
	}
	
	public ArrayList<String> getSensesEN() {
		return sensesEN;
	}

	public void setSensesEN(ArrayList<String> sensesEN) {
		this.sensesEN = sensesEN;
	}

	public ArrayList<SymcatSymptomProb> getCorrelatedSymptoms() {
		return correlatedSymptoms;
	}
	
	public ArrayList<String> getCorrelatedSymptomsUrls() {
		ArrayList<String> correlatedSymptoms = new ArrayList<String>();
		for(SymcatSymptomProb ssp : this.correlatedSymptoms){
			correlatedSymptoms.add(ssp.getName());
		}
		
		return correlatedSymptoms;
	}
	
	public void setCorrelatedSymptoms(ArrayList<SymcatSymptomProb> correlatedSymptoms) {
		this.correlatedSymptoms = correlatedSymptoms;
	}

	@Override
	public String toString() {
		return "SymcatSymptom [name=" + name + ", nameEN=" + nameEN + ", url=" + url + ", wikipedia=" + wikipedia
				+ ", wikipediaEN=" + wikipediaEN + ", descriptions=" + descriptions + ", descriptionsEN="
				+ descriptionsEN + ", senses=" + senses + ", sensesEN=" + sensesEN + ", correlatedSymptoms="
				+ correlatedSymptoms + "]";
	}
}