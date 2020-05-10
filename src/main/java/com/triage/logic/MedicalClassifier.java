package com.triage.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.triage.rest.models.classifier.ClassifierResult;

public class MedicalClassifier {
	private String text;
	private int nresult;
	
	private ArrayList<ClassifierResult> results;
	private ArrayList<String> resultsICPCCodes;
	private HashMap<String, String> mappingCode;
	
	public MedicalClassifier(String text, int nresult) {
		this.text = text;
		this.nresult = nresult;
		
		this.results = new ArrayList<ClassifierResult>();
		this.resultsICPCCodes = new ArrayList<String>();
		this.mappingCode = initizalizeMappingDict();
		
		this.results = classify();
	}
	
	public ArrayList<String> getICPCCodes(){
		ArrayList<String> codes = new ArrayList<String>();
		for(ClassifierResult cr : this.results){
			String crcode = this.mappingCode.get(cr.getName());
			codes.add(crcode);
		}
		
		this.resultsICPCCodes = codes;
		return codes;
	}
	
	public ArrayList<ClassifierResult> classify(){
		Client client = Client.create();
		WebResource service = client
				.resource(UriBuilder.fromUri("http://localhost:10145/classifyTop").build())
				.queryParam("text", this.text);//.queryParam("top", Integer.toString(nresult));
		String json = service.accept(MediaType.APPLICATION_JSON).get(String.class);
		ArrayList<ClassifierResult> dirtyClassR = parseJson(json);
		Collections.sort(dirtyClassR);
		
		ArrayList<ClassifierResult> classRes = new ArrayList<ClassifierResult>();
		int i = 0;
		for(ClassifierResult cr: dirtyClassR){
			if(i >= this.nresult) break;
			classRes.add(cr);
			i++;
		}
		
		return classRes;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<ClassifierResult> parseJson(String json){
		ArrayList<ClassifierResult> results = new ArrayList<ClassifierResult>();
		
		Map<String, Object> javaRootMapObject = new Gson().fromJson(json, Map.class);
		for(Object s : ((Map)javaRootMapObject.get("PREDICTIONS")).keySet()){
			ClassifierResult cr = new ClassifierResult(s.toString(), 
					(Double) ((Map)javaRootMapObject.get("PREDICTIONS")).get(s));
			results.add(cr);
		}
		
		return results;
	}
	
	private HashMap<String, String> initizalizeMappingDict(){
		HashMap<String, String> dict = new HashMap<String, String>();
		dict.put("Allergologia-e-immunologia", "B");
		dict.put("Andrologia", "Y");
		dict.put("Cardiologia", "K");
		dict.put("Chirurgia-generale", "A");
		dict.put("Colonproctologia", "D");
		dict.put("Dermatologia-e-venereologia", "S");
		dict.put("Ematologia", "B");
		dict.put("Endocrinologia", "T");
		dict.put("Gastroenterologia-e-endoscopia-digestiva", "D");
		dict.put("Ginecologia-e-ostetricia", "X");
		dict.put("Malattie-infettive", "A");
		dict.put("Medicina-generale", "A");
		dict.put("Neurochirurgia", "N");
		dict.put("Neurologia", "N");
		dict.put("Oculistica", "F");
		dict.put("Odontoiatria-e-odontostomatologia", "D");
		dict.put("Oncologia-medica", "B");
		dict.put("Ortopedia", "L");
		dict.put("Otorinolaringoiatria", "H");
		dict.put("Pneumologia", "R");
		dict.put("Psichiatria", "P");
		dict.put("Psicologia", "P");
		dict.put("Senologia", "X");
		dict.put("Urologia", "U");
		
		return dict;
	}

	public ArrayList<ClassifierResult> getResults() {
		return results;
	}

	public ArrayList<String> getResultsICPCCodes() {
		return resultsICPCCodes;
	}

	public HashMap<String, String> getMappingCode() {
		return mappingCode;
	}

	public void setMappingCode(HashMap<String, String> mappingCode) {
		this.mappingCode = mappingCode;
	}
}
