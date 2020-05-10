package com.triage.rest.models.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.text.WordUtils;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import me.xdrop.fuzzywuzzy.ratios.SimpleRatio;

public class TherapyDay {
	
	private final static String LUNEDI = "Lunedì"; 
	private final static String LUNEDI_PREF = "Lun"; 
	private final static String MARTEDI = "Martedì"; 
	private final static String MARTEDI_PREF = "Mar";
	private final static String MERCOLEDI = "Mercoledì";
	private final static String MERCOLEDI_PREF = "Mer"; 
	private final static String GIOVEDI = "Giovedì";
	private final static String GIOVEDI_PREF = "Gio";
	private final static String VENERDI = "Venerdì";
	private final static String VENERDI_PREF = "Ven";
	private final static String SABATO = "Sabato";
	private final static String SABATO_PREF = "Sab";
	private final static String DOMENICA = "Domenica";
	private final static String DOMENICA_PREF = "Dom";
	
	private static String[] days = new String[]{DOMENICA, LUNEDI, MARTEDI, MERCOLEDI,
			GIOVEDI, VENERDI, SABATO};
	
	private static String[] days_pref = new String[]{LUNEDI_PREF, MARTEDI_PREF, MERCOLEDI_PREF,
			GIOVEDI_PREF, VENERDI_PREF, SABATO_PREF, DOMENICA_PREF};
	
	private static ArrayList<String> daysList(){
		ArrayList<String> daysList = new ArrayList<String>(Arrays.asList(days));
		ArrayList<String> lowerDaysList = new ArrayList<String>();
		for(String day : daysList){
			lowerDaysList.add(day.toLowerCase());
		}
		return lowerDaysList;
	}
	
	private static ArrayList<String> daysPrefList(){
		ArrayList<String> daysList = new ArrayList<String>(Arrays.asList(days_pref));
		ArrayList<String> lowerDaysList = new ArrayList<String>();
		for(String day : daysList){
			lowerDaysList.add(day.toLowerCase());
		}
		return lowerDaysList;
	}
	
	public static ArrayList<String> parseDays(String text){
		ArrayList<String> daysList = daysList();//new ArrayList<String>(Arrays.asList(days));
		ArrayList<String> daysPrefList = daysPrefList();//new ArrayList<String>(Arrays.asList(days_pref));
		
		String[] words = text.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split(" ");
		ArrayList<String> parsedDays = new ArrayList<String>();
		for(int i=0; i<words.length; i++){
			String toParse = words[i].trim();
			if(toParse.equals(""))
				continue;
			
			List<ExtractedResult> res = FuzzySearch.extractTop(toParse, daysList, new SimpleRatio(), 3, 75);
			System.out.println("On complete day:" + res + " from: " + toParse);
			if(res.size() > 0){
				if(!parsedDays.contains(res.get(0).getString()))
					parsedDays.add(WordUtils.capitalize(res.get(0).getString()));
			}else{
				List<ExtractedResult> resPref = FuzzySearch.extractTop(toParse, daysPrefList, new SimpleRatio(), 3, 70);
				System.out.println("On pref day: " + resPref + " from: " + toParse);
				if(resPref.size() > 0){
					String completeDayName = reverseDays().get(WordUtils.capitalize(resPref.get(0).getString()));
					if(!parsedDays.contains(completeDayName))
						parsedDays.add(completeDayName);
				}
			}
		}
		
		return parsedDays;
	}
	
	private static HashMap<String, String> reverseDays(){
		HashMap<String, String> reverse = new HashMap<String, String>();
		reverse.put(LUNEDI_PREF, LUNEDI);
		reverse.put(MARTEDI_PREF, MARTEDI);
		reverse.put(MERCOLEDI_PREF, MERCOLEDI);
		reverse.put(GIOVEDI_PREF, GIOVEDI);
		reverse.put(VENERDI_PREF, VENERDI);
		reverse.put(SABATO_PREF, SABATO);
		reverse.put(DOMENICA_PREF, DOMENICA);
		
		return reverse;
	}
	
	public static ArrayList<Integer> getDaysIdx(ArrayList<String> therapyDays){
		if(therapyDays.size() > 0){
			ArrayList<String> daysList = new ArrayList<String>(Arrays.asList(days));
			
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for(String day : therapyDays){
				ids.add(daysList.indexOf(day));
			}
			
			return ids;
		}
		
		return null;
	}
	
	public static ArrayList<Integer> getDaysIdxQuartzCron(ArrayList<String> therapyDays){
		if(therapyDays.size() > 0){
			ArrayList<String> daysList = new ArrayList<String>(Arrays.asList(days));
			
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for(String day : therapyDays){
				ids.add(daysList.indexOf(day)+1);
			}
			
			return ids;
		}
		
		return null;
	}
}
