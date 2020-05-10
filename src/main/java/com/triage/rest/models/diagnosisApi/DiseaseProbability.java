package com.triage.rest.models.diagnosisApi;

import com.google.gson.annotations.SerializedName;

public class DiseaseProbability {
	private String name;
	
	@SerializedName("prob")
	private double probability;

	public DiseaseProbability(String name, double probability) {
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

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public String toString() {
		return "DiseaseProbability [name=" + name + ", probability=" + probability + "]";
	}
}
