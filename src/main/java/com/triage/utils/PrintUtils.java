package com.triage.utils;

import java.util.ArrayList;

import com.triage.rest.models.symptomChecker.SymcatCondition;
import com.triage.rest.models.symptomChecker.SymcatSymptom;

public class PrintUtils {
	
	public static void printSymcatSymptoms(ArrayList<SymcatSymptom> symptoms, String desc){
		ArrayList<String> prints = new ArrayList<String>();
		for(SymcatSymptom ss : symptoms){
			prints.add(ss.getUrl());
		}
		System.out.println("[SymptomChecker] (" + desc + ") " + prints);
	}
	
	public static void printSymcatConditions(ArrayList<SymcatCondition> conditions, String desc){
		ArrayList<String> prints = new ArrayList<String>();
		for(SymcatCondition sc : conditions){
			prints.add(sc.getUrl());
		}
		System.out.println("[SymptomChecker] (" + desc + ") " + prints);
	}
}
