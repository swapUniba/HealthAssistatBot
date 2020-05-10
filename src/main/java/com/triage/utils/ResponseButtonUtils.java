package com.triage.utils;

import java.util.ArrayList;

import com.triage.rest.models.messages.Answer;

public class ResponseButtonUtils {
	public static ArrayList<ArrayList<String>> transformToLinearButton(ArrayList<Answer> ans){
		ArrayList<ArrayList<String>> bts = new ArrayList<ArrayList<String>>();
		for(Answer a : ans){
			ArrayList<String> rb = new ArrayList<String>();
			rb.add(a.getText());
			
			bts.add(rb);
		}
		
		return bts;
	}
	
	public static ArrayList<ArrayList<String>> transformToGridButton(ArrayList<Answer> ans, int ncol){
		ArrayList<ArrayList<String>> bts = new ArrayList<ArrayList<String>>();
		ArrayList<String> rowb = new ArrayList<String>();
		for(Answer a : ans){
			if(rowb.size() == ncol){
				bts.add(rowb);
				rowb = new ArrayList<String>();
			}
			rowb.add(a.getText());
		}
		bts.add(rowb);
		
		return bts;
	}
}
