package com.triage.rest.models.symptomChecker;

public class SymcatSymptomProb {
	private String name;
	private double probability;
	
	public SymcatSymptomProb(String name, int probability) {
		this.name = name;
		this.probability = probability;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public void setProbability(int probability) {
		this.probability = probability;
	}
	
	@Override
	public String toString() {
		return "SymcatSymptomProb [name=" + name + ", probability=" + probability + "]";
	}
}
