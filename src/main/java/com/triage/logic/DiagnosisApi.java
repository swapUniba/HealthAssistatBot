package com.triage.logic;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.triage.rest.models.diagnosisApi.DiagnosisResult;

public class DiagnosisApi {
	private ArrayList<String> symptoms;
	
	private DiagnosisResult result;
	
	public DiagnosisApi(ArrayList<String> symptoms) {
		this.symptoms = symptoms;
	
		this.result = callApi();
	}
	
	private final String URL = "http://localhost:10146/classify";
	public DiagnosisResult callApi(){
		String symtpomsParam = "";
		for(String sym : this.symptoms){
			if(sym.contains("http://www.symcat.com/symptoms/"))
				symtpomsParam += sym.replace("http://www.symcat.com/symptoms/", "");
			else
				symtpomsParam += sym;
			symtpomsParam += ",";
		}
		symtpomsParam = symtpomsParam.substring(0, symtpomsParam.length()-1);//Remove last ','

		Client client = Client.create();
		WebResource service = client
				.resource(UriBuilder.fromUri(URL).build())
				.queryParam("symptoms", symtpomsParam);
		String json = service.accept(MediaType.APPLICATION_JSON).get(String.class);
		
		DiagnosisResult result = new Gson().fromJson(json, DiagnosisResult.class);
		//ArrayList<ClassifierResult> res = parseJson(json);
		return result;
	}
	
	public ArrayList<String> getSymptoms() {
		return symptoms;
	}
	
	public DiagnosisResult getResult() {
		return result;
	}
}
