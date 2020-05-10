package com.triage.rest.models.users;

import java.util.ArrayList;
import java.util.Date;

public class Therapy {
	private int id;
	private String name;
	private String dosage;
	private ArrayList<String> hours;
	private ArrayList<String> days;
	private TherapyType type;
	private Date startTime;
	private Date endTime;
	private String drugName;
	private int intervalDays;

	public Therapy(int id, String name) {
		this.id = id;
		this.name = name;
		this.hours = new ArrayList<String>();
		this.days = new ArrayList<String>();
	}
	
	public Therapy(int id, String name, String dosage, Date endTime) {
		this.id = id;
		this.name = name;
		this.dosage = dosage;
		this.hours = new ArrayList<String>();
		this.days = new ArrayList<String>();
		this.endTime = endTime;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public ArrayList<String> getHours() {
		return hours;
	}
	
	public String getHoursToString(){
		StringBuilder sb = new StringBuilder();
		for(String hour : this.hours){
			sb.append(hour);
			sb.append(", ");
		}
		String hoursStr = sb.toString().trim();
		return hoursStr.substring(0, hoursStr.length()-1);
	}

	public void setHours(ArrayList<String> hours) {
		this.hours = hours;
	}
	
	public void setSingleHour(String hour) {
		this.hours.add(hour);
	}

	public ArrayList<String> getDays() {
		return days;
	}

	public void setDays(ArrayList<String> days) {
		this.days = days;
	}

	public void setSingleDay(String day) {
		this.days.add(day);
	}
	
	public TherapyType getType() {
		return type;
	}

	public void setType(TherapyType type) {
		this.type = type;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "Therapy [id=" + id + ", name=" + name + ", dosage=" + dosage + ", hours=" + hours + ", days=" + days
				+ ", type=" + type + ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}

	public int getIntervalDays() {
		return intervalDays;
	}

	public void setIntervalDays(int intervalDays) {
		this.intervalDays = intervalDays;
	}
}
