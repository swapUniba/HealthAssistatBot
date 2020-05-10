package com.triage.rest.models.classifier;

public class ClassifierResult implements Comparable<ClassifierResult>{
	private String name;
	private double score;
	
	public ClassifierResult() {
	}
	
	public ClassifierResult(String name, double score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "ClassifierResult [name=" + name + ", score=" + score + "]";
	}

	public int compareTo(ClassifierResult cr1) {
		if (this.getScore() < cr1.getScore()) return 1;
	    if (this.getScore() > cr1.getScore()) return -1;
		return 0;
	}
}
