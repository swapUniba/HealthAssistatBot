package com.triage.logic.managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.triage.logic.BingTranslate;
import com.triage.logic.DiagnosisApi;
import com.triage.logic.Language;
import com.triage.logic.SymcatConditionsReader;
import com.triage.logic.SymcatSymptomsReader;
import com.triage.logic.TagMeApi;
import com.triage.rest.dao.SymptomCheckerDao;
import com.triage.rest.models.diagnosisApi.DiagnosisResult;
import com.triage.rest.models.diagnosisApi.DiseaseProbability;
import com.triage.rest.models.symptomChecker.SymcatCondition;
import com.triage.rest.models.symptomChecker.SymcatSymptom;
import com.triage.rest.models.tagmeApi.TagMeResult;
import com.triage.rest.models.users.User;
import com.triage.utils.PrintUtils;

public class SymptomCheckerManager {
	private User user;
	private SymptomCheckerDao scdao;

	public SymptomCheckerManager(User user){
		this.user = user;
		this.scdao = new SymptomCheckerDao();
	}
	
	/**
	 * Inizializza gli oggetti db del symptom checker.
	 */
	public void startSymptomChecker(){
		this.scdao.createNewSymptomChecker(this.user.getId());
		//TODO
	}
	
	/**
	 * Esegue la ricerca all'interno dell'indice Lucene di un sintomo.
	 */
	public ArrayList<SymcatSymptom> parseSymptoms(String text, boolean firstInteraction){
		ArrayList<SymcatSymptom> symptoms = new ArrayList<SymcatSymptom>();
		symptoms = parseItalianSymptoms(text);
		symptoms.addAll(parseEnglishSymptoms(text));
		PrintUtils.printSymcatSymptoms(symptoms, "ParsedSymptoms");
		
		//Attention arraylist symptoms can contain duplicates. But this do not affect diagnose api.
		if(firstInteraction){
			if(symptoms.size() > 0){
				ArrayList<String> symptomsUrls = this.scdao.getInquiredSymptoms(this.user.getId());
				return SymcatSymptomsReader.getInstance().searchByUrls(symptomsUrls); 
			}else{
				return null;
			}
		}else{
			ArrayList<String> symptomsUrls = this.scdao.getInquiredSymptoms(this.user.getId());
			return SymcatSymptomsReader.getInstance().searchByUrls(symptomsUrls);
		}
	}
	
	private ArrayList<SymcatSymptom> parseItalianSymptoms(String text){
		//Parse symptom / symptoms (using TagMeApi)
		TagMeApi tma = new TagMeApi(text, "it");
		TagMeResult apiRes = tma.getResult();
		ArrayList<String> parsedLinks = apiRes.getWikipediaLinks();
		
		ArrayList<SymcatSymptom> symptoms = new ArrayList<SymcatSymptom>();
		for(String pl : parsedLinks){
			SymcatSymptom ss = SymcatSymptomsReader.getInstance().searchByWikiLink(pl, Language.ITA);
			if(ss != null){ //Retrived by wikipedia link
				if(!symptoms.contains(ss)){
					symptoms.add(ss);
					this.scdao.addNewSymptom(this.user.getId(), text, ss.getUrl());
				}
				continue;
			}
			
			//Retrive entity by name or synonyms
			String linkToWord = pl.replace("https://it.wikipedia.org/wiki/", "").replace("_", " ");
			ss = SymcatSymptomsReader.getInstance().searchBySynonyms(linkToWord, Language.ITA);
			if(ss != null){
				if(!symptoms.contains(ss)){
					symptoms.add(ss);
					this.scdao.addNewSymptom(this.user.getId(), text, ss.getUrl());
				}
			}
		}
		
		PrintUtils.printSymcatSymptoms(symptoms, "TagMeApiITA");
		return symptoms;
	}
	
	private ArrayList<SymcatSymptom> parseEnglishSymptoms(String text){
		String searchFrom = BingTranslate.translateItaEng(text);
		System.out.println("[SymptomChecker] from \"" + text + "\" to \"" + searchFrom + "\"");
		
		TagMeApi tma = new TagMeApi(searchFrom, "en");
		TagMeResult apiRes = tma.getResult();
		ArrayList<String> parsedLinks = apiRes.getWikipediaLinks();
		
		ArrayList<SymcatSymptom> symptoms = new ArrayList<SymcatSymptom>();
		for(String pl : parsedLinks){
			SymcatSymptom ss = SymcatSymptomsReader.getInstance().searchByWikiLink(pl, Language.ENG);
			if(ss != null){ //Retrived by wikipedia link
				if(!symptoms.contains(ss)){
					symptoms.add(ss);
					this.scdao.addNewSymptom(this.user.getId(), text, ss.getUrl());
				}
				continue;
			}
			
			//Retrive entity by name or synonyms
			String linkToWord = pl.replace("https://it.wikipedia.org/wiki/", "").replace("_", " ");
			ss = SymcatSymptomsReader.getInstance().searchBySynonyms(linkToWord, Language.ENG);
			if(ss != null){
				if(!symptoms.contains(ss)){
					symptoms.add(ss);
					this.scdao.addNewSymptom(this.user.getId(), text, ss.getUrl());
				}
			}
		}
		
		PrintUtils.printSymcatSymptoms(symptoms, "TagMeApiENG");
		return symptoms;
	}
	
	/**
	 * Esegue la ricerca all'interno dei dati SymCat e attraverso le api TagMe
	 */
	/*public ArrayList<SymcatSymptom> parseSymptoms1(String text, boolean firstInteraction){
		//Parse symptom / symptoms (for now using TagMeApi)
		TagMeApi tma = new TagMeApi(text);
		TagMeResult apiRes = tma.getResult();
		ArrayList<String> parsedLinks = apiRes.getWikipediaLinks();
		
		ArrayList<SymcatSymptom> symptoms = new ArrayList<SymcatSymptom>();
		for(String pl : parsedLinks){
			SymcatSymptom ss = SymcatSymptomsReader.getInstance().searchByWikiLink(pl);
			if(ss != null){ //Retrived by wikipedia link
				if(!symptoms.contains(ss)){
					symptoms.add(ss);
					this.scdao.addNewSymptom(this.user.getId(), text, ss.getUrl());
				}
				continue;
			}
			
			//Retrive entity by name or symptoms
			String linkToWord = pl.replace("https://it.wikipedia.org/wiki/", "").replace("_", " ");
			ss = SymcatSymptomsReader.getInstance().searchBySynonyms(linkToWord);
			if(ss != null){
				if(!symptoms.contains(ss)){
					symptoms.add(ss);
					this.scdao.addNewSymptom(this.user.getId(), text, ss.getUrl());
				}
			}
		}
		
		if(firstInteraction){
			if(symptoms.size() > 0){
				ArrayList<String> symptomsUrls = this.scdao.getInquiredSymptoms(this.user.getId());
				return SymcatSymptomsReader.getInstance().searchByUrls(symptomsUrls);
			}else{
				return null;
			}
		}else{
			ArrayList<String> symptomsUrls = this.scdao.getInquiredSymptoms(this.user.getId());
			return SymcatSymptomsReader.getInstance().searchByUrls(symptomsUrls);
		}
	}*/
	
	public LinkedHashMap<SymcatCondition, Double> makeDiagnosis(ArrayList<SymcatSymptom> symptoms){
		ArrayList<String> symptomsLinks = new ArrayList<String>();
		for(SymcatSymptom ss : symptoms){
			symptomsLinks.add(ss.getUrl());
		}

		DiagnosisApi dcapi = new DiagnosisApi(symptomsLinks);
		DiagnosisResult result = dcapi.getResult();
		this.scdao.removeDiagnosisResults(this.user.getId());
		this.scdao.saveDiagnosisResults(this.user.getId(), result.getDiseases());
		
		//Return complete SymcatDiagnosis objects
		LinkedHashMap<SymcatCondition, Double> completeDisease = new LinkedHashMap<SymcatCondition, Double>();
		for(DiseaseProbability dp : result.getDiseases()){
			completeDisease.put(SymcatConditionsReader.getInstance().searchByUrl(dp.getName()), dp.getProbability());
		}
		
		//return result.getDiseases();
		return completeDisease;
	}
	
	/**
	 * Effettua l'intersezione fra l'insieme dei sintomi delle malattie predette 
	 * (tramite DiagnosisAPI) e l'insieme dei sintomi correlati ai sintomi inseriti 
	 * dall'utente.
	 */
	public ArrayList<SymcatSymptom> recommendNextSymptom(ArrayList<SymcatCondition> userDiseases){
		ArrayList<String> userSymptoms = this.scdao.getInquiredSymptoms(this.user.getId());
		ArrayList<SymcatSymptom> symptoms = SymcatSymptomsReader.getInstance().searchByUrls(userSymptoms);
		
		ArrayList<String> userDiseasesUrl = new ArrayList<String>();
		for(SymcatCondition dp : userDiseases){
			userDiseasesUrl.add(dp.getUrl());
		}
		ArrayList<SymcatCondition> conditions = SymcatConditionsReader.getInstance().searchByUrls(userDiseasesUrl);
		
		//Intersection between multiple diaseases and multiple inserted symptoms
		ArrayList<String> recommendedSymptomsUrl = new ArrayList<String>();
		ArrayList<ArrayList<String>> recSymptomsPerCondition = giveSymptPerDiasease(conditions, symptoms, userSymptoms);
		int numRec = getNumbOfReccomendableSymptoms(conditions, symptoms, userSymptoms);
		
		if(numRec >= 3){
			while(recommendedSymptomsUrl.size() < 3){
				for(ArrayList<String> symPerCond : recSymptomsPerCondition){
					//Take the first symptom per condition (limit 3)
					for(String spc : symPerCond){
						if(!recommendedSymptomsUrl.contains(spc)){
							recommendedSymptomsUrl.add(spc);
							break;
						}
					}
					if(recommendedSymptomsUrl.size() >= 3)
						break;
				}
			}
		}else{
			for(ArrayList<String> symPerCond : recSymptomsPerCondition){
				for(String spc : symPerCond){
					if(!recommendedSymptomsUrl.contains(spc)){
						recommendedSymptomsUrl.add(spc);
					}
				}
			}
		}
		
		//Intersection between multiple diaseases and multiple inserted symptoms
		/*ArrayList<String> recommendedSymptomsUrl = new ArrayList<String>();
        intersection: for(SymcatCondition cond : conditions){
        	for(SymcatSymptom sym : symptoms){
        		for(String s : cond.getSymptomsUrls()){
        			if(sym.getCorrelatedSymptomsUrls().contains(s)){
        				if(!recommendedSymptomsUrl.contains(s) && !userSymptoms.contains(s))
        					recommendedSymptomsUrl.add(s);
        				
        				if(recommendedSymptomsUrl.size() >= 3)
        					break intersection;
        			}
        		}
        	}
        }*/
        
        //Retrive complete symptoms object
        return SymcatSymptomsReader.getInstance().searchByUrls(recommendedSymptomsUrl);
	}
	
	private ArrayList<ArrayList<String>> giveSymptPerDiasease(ArrayList<SymcatCondition> conditions, 
																ArrayList<SymcatSymptom> symptoms,
																ArrayList<String> excluded){
		ArrayList<ArrayList<String>> allSymp = new ArrayList<ArrayList<String>>();
		for(SymcatCondition cond : conditions){
			ArrayList<String> condSymp = new ArrayList<String>();
			for(SymcatSymptom sym : symptoms){
				for(String s : cond.getSymptomsUrls()){
					if(sym.getCorrelatedSymptomsUrls().contains(s)){
						if(!excluded.contains(s))
							condSymp.add(s);
					}
				}
			}
			allSymp.add(condSymp);
		}
		
		return allSymp;
	}
	
	private int getNumbOfReccomendableSymptoms(ArrayList<SymcatCondition> conditions, 
													ArrayList<SymcatSymptom> symptoms,
													ArrayList<String> excluded){
		ArrayList<String> recSymp = new ArrayList<String>();
		for(SymcatCondition cond : conditions){
			for(SymcatSymptom sym : symptoms){
				for(String s : cond.getSymptomsUrls()){
					if(sym.getCorrelatedSymptomsUrls().contains(s)){
						if(!recSymp.contains(s) && !excluded.contains(s))
							recSymp.add(s);
					}
				}
			}
		}
		
		return recSymp.size();
	}
	
	public ArrayList<SymcatSymptom> addNewSymptom(String text){
		//Parse symptom choosen between a set of symptoms
		//TODO fuzzy search
		SymcatSymptom parsedSymptom = SymcatSymptomsReader.getInstance().searchByName(text);
		
		if(parsedSymptom != null){
			this.scdao.addNewSymptom(this.user.getId(), text, parsedSymptom.getUrl());
		}else{
			return null;
		}
		
		ArrayList<String> symptomsUrls = this.scdao.getInquiredSymptoms(this.user.getId());
		return SymcatSymptomsReader.getInstance().searchByUrls(symptomsUrls);
	}
}
