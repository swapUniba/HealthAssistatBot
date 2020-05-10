package com.triage.rest.models.users;

import com.triage.utils.NLPUtils;

import java.util.ArrayList;
import java.util.Date;

public class Tracking {
	private int id;
	private String name;
	private Date date;
	private String dateNewFormat;
	private ArrayList<Tracking_image> images;
	//just a commodity
	private double result;
	
	public Tracking(int id, String name, Date date) {
		this.id = id;
		this.name = name;
		this.setDate(date);
		this.images = new ArrayList<Tracking_image>();
	}

	public Tracking(String name,double result) {
		this.name = name;
		this.result=result;
	}

	public double getResult(){ return this.result;}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Tracking_image> getImages() {
		return images;
	}

	public void setImages(ArrayList<Tracking_image> images) {
		this.images = images;
	}
	
	public void setSingleImage(Tracking_image image){
		this.images.add(image);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date){
		this.date=date;
		if (date!=null)
			this.dateNewFormat=  NLPUtils.getDateWithAnotherFormat(date);
	}

	public String getDateNewFormat(){
		return this.dateNewFormat;
	}
}
