package com.triage.rest.models.suggestDoctor;

public class Doctor {
	private String completeName;
	private int activityTotal;
	private String webpage;
	private String specialtyRefined;
	
	public Doctor(String completeName, int activityTotal, String webpage, String specialtyRefined) {
		this.completeName = completeName;
		this.activityTotal = activityTotal;
		this.webpage = webpage;
		this.specialtyRefined = specialtyRefined;
	}
	
	public String getCompleteName() {
		return completeName;
	}
	
	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}
	
	public int getActivityTotal() {
		return activityTotal;
	}
	
	public void setActivityTotal(int activityTotal) {
		this.activityTotal = activityTotal;
	}
	
	public String getWebpage() {
		return webpage;
	}
	
	public void setWebpage(String webpage) {
		this.webpage = webpage;
	}
	
	public String getSpecialtyRefined() {
		return specialtyRefined;
	}
	
	public void setSpecialtyRefined(String specialtyRefined) {
		this.specialtyRefined = specialtyRefined;
	}

	@Override
	public String toString() {
		return "Doctor [completeName=" + completeName + ", activityTotal=" + activityTotal + ", webpage=" + webpage
				+ ", specialtyRefined=" + specialtyRefined + "]";
	}
}
