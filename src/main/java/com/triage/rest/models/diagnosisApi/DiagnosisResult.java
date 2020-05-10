package com.triage.rest.models.diagnosisApi;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class DiagnosisResult {
	private ArrayList<DiseaseProbability> diseases;
	
	@SerializedName("symptoms")
	private ArrayList<String> parsedSymptoms;

	public DiagnosisResult(ArrayList<DiseaseProbability> diseases, ArrayList<String> parsedSymptoms) {
		super();
		this.diseases = diseases;
		this.parsedSymptoms = parsedSymptoms;
	}

	public ArrayList<DiseaseProbability> getDiseases() {
		return diseases;
	}

	public void setDiseases(ArrayList<DiseaseProbability> diseases) {
		this.diseases = diseases;
	}

	public ArrayList<String> getParsedSymptoms() {
		return parsedSymptoms;
	}

	public void setParsedSymptoms(ArrayList<String> parsedSymptoms) {
		this.parsedSymptoms = parsedSymptoms;
	}

	@Override
	public String toString() {
		return "DiagnosisResult [diseases=" + diseases + ", parsedSymptoms=" + parsedSymptoms + "]";
	}
}
