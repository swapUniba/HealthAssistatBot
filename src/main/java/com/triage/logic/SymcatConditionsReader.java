package com.triage.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.triage.rest.models.symptomChecker.SymcatCondition;

public class SymcatConditionsReader {
	private static SymcatConditionsReader instance = null; //Singleton
	
	private SymcatCondition[] conditions;
	private HashMap<String, Integer> index;
	
	private SymcatConditionsReader(){
		JsonReader reader = null;
		try {
			File file = new File(getClass().getResource("/com/triage/symcatData/datasetConditionsIT.json").getFile());
			reader = new JsonReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		this.conditions = new Gson().fromJson(reader, SymcatCondition[].class);
		this.index = new HashMap<String, Integer>();
		for(int i=0; i<this.conditions.length; i++){
			this.index.put(this.conditions[i].getUrl(), i);
		}
	}

	public static SymcatConditionsReader getInstance() {
		if(instance == null) {
			instance = new SymcatConditionsReader();
		}
		return instance;
	}
	
	public SymcatCondition[] getConditions() {
		return conditions;
	}

	public HashMap<String, Integer> getIndex() {
		return index;
	}
	
	public ArrayList<SymcatCondition> searchByUrls(ArrayList<String> urls){
		ArrayList<SymcatCondition> conditions = new ArrayList<SymcatCondition>();
		
		for(String url : urls){
			conditions.add(searchByUrl(url));
		}
		
		return conditions;
	}
	
	public SymcatCondition searchByUrl(String url){
		return this.conditions[this.index.get(url)];
	}
}
