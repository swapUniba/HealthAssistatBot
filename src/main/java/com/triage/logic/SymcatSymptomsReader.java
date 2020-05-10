package com.triage.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.triage.rest.models.symptomChecker.SymcatSymptom;

public class SymcatSymptomsReader {
	private static SymcatSymptomsReader instance = null; //Singleton
	
	private SymcatSymptom[] symptoms;
	private HashMap<String, Integer> index;
	
	private SymcatSymptomsReader(){
		JsonReader reader = null;
		try {
			File file = new File(getClass().getResource("/com/triage/symcatData/datasetSymptomsIT.json").getFile());
			reader = new JsonReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		this.symptoms = new Gson().fromJson(reader, SymcatSymptom[].class);
		this.index = new HashMap<String, Integer>();
		for(int i=0; i<this.symptoms.length; i++){
			this.index.put(this.symptoms[i].getUrl(), i);
		}
		
		try {
			File file = new File(getClass().getResource("/com/triage/symcatData/datasetSymptomsEN.json").getFile());
			reader = new JsonReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		SymcatSymptom[] symptomsEn = new Gson().fromJson(reader, SymcatSymptom[].class);
		//Put all in SymcatSymptoms objects
		for(int i = 0; i < symptomsEn.length; i++){
			int idx = this.index.get(symptomsEn[i].getUrl());
			
			this.symptoms[idx].setNameEN(symptomsEn[i].getName());//Set english name
			this.symptoms[idx].setWikipediaEN(symptomsEn[i].getWikipedia());//Set english wikilink 
			this.symptoms[idx].setSensesEN(symptomsEn[i].getSenses());//Set english senses 
			this.symptoms[idx].setDescriptionsEN(symptomsEn[i].getDescriptions());//Set english descriptions 
		}
	}

	public static SymcatSymptomsReader getInstance() {
		if(instance == null) {
			instance = new SymcatSymptomsReader();
		}
		return instance;
	}
	
	public SymcatSymptom[] getSymptoms() {
		return symptoms;
	}

	public HashMap<String, Integer> getIndex() {
		return index;
	}
	
	/*public SymcatSymptom searchByWikiLink(String wikiLink){
		for(int i=0; i<this.symptoms.length; i++){
			SymcatSymptom symptom = this.symptoms[i];
			if(symptom.getWikipediaIT() == null)
				continue;
			
			if(symptom.getWikipediaIT().equalsIgnoreCase(wikiLink))
				return symptom;
		}
		
		return null;
	}*/
	
	public SymcatSymptom searchByWikiLink(String wikiLink, Language lang){
		for(int i=0; i<this.symptoms.length; i++){
			SymcatSymptom symptom = this.symptoms[i];
			if(lang == Language.ENG){
				if(symptom.getWikipediaEN() == null)
					continue;
				
				if(symptom.getWikipediaEN().equalsIgnoreCase(wikiLink))
					return symptom;
			}else{
				if(symptom.getWikipedia() == null)
					continue;
				
				if(symptom.getWikipedia().equalsIgnoreCase(wikiLink))
					return symptom;
			}
		}
		
		return null;
	}
	
	public ArrayList<SymcatSymptom> searchByUrls(ArrayList<String> urls){
		ArrayList<SymcatSymptom> symptoms = new ArrayList<SymcatSymptom>();
		
		for(String url : urls){
			symptoms.add(searchByUrl(url));
		}
		
		return symptoms;
	}
	
	public SymcatSymptom searchByUrl(String url){
		return this.symptoms[this.index.get(url)];
	}
	
	public SymcatSymptom searchByName(String name, Language lang){
		switch (lang) {
		case ENG:
			for(SymcatSymptom ss : this.symptoms){
				if(ss.getNameEN().equalsIgnoreCase(name))
					return ss;
			}
			break;
		case ITA:
		default:
			for(SymcatSymptom ss : this.symptoms){
				if(ss.getName().equalsIgnoreCase(name))
					return ss;
			}
			break;
		}
		
		return null;
	}
	
	public SymcatSymptom searchByName(String name){
		for(SymcatSymptom ss : this.symptoms){
			if(ss.getName().equalsIgnoreCase(name))
				return ss;
		}
		
		return null;
	}
	
	public SymcatSymptom searchBySynonyms(String synonym, Language lang){
		for(SymcatSymptom ss : this.symptoms){
			if(lang == Language.ENG){
				for(String syn : ss.getSensesEN()){
					if(syn.equalsIgnoreCase(synonym))
						return ss;
				}
			}else{
				for(String syn : ss.getSenses()){
					if(syn.equalsIgnoreCase(synonym))
						return ss;
				}
			}
		}
		
		return null;
	}
	/*public SymcatSymptom searchBySynonyms(String synonym){
		for(SymcatSymptom ss : this.symptoms){
			for(String syn : ss.getSenses()){
				if(syn.equalsIgnoreCase(synonym))
					return ss;
			}
		}
		
		return null;
	}*/
}
