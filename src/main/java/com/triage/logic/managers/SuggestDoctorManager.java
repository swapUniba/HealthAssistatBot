package com.triage.logic.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.text.similarity.LevenshteinDistance;

import com.triage.logic.MedicalClassifier;
import com.triage.rest.dao.SuggestDoctorDao;
import com.triage.rest.enummodels.ItalianProvince;
import com.triage.rest.models.classifier.ClassifierResult;
import com.triage.rest.models.suggestDoctor.Doctor;
import com.triage.rest.models.users.User;
import com.vdurmont.emoji.EmojiParser;

public class SuggestDoctorManager {
	private User user;
	private SuggestDoctorDao sddao;

	public SuggestDoctorManager(User user){
		this.user = user;
		this.sddao = new SuggestDoctorDao();
	}
	
	/**
	 * Esegue la classificazione del testo e restituise i risultati. 
	 * Prende in input il testo. Salva riusltati nel database
	 * 
	 * @param scText - il testo sui cui effettuare l'analisi.
	 * @return
	 */
	public ArrayList<ClassifierResult> executeClassification(String text){
		this.sddao.createNewSuggestDoctor(this.user.getId(), text);
		
		MedicalClassifier mc = new MedicalClassifier(text, 2);
		this.sddao.saveClassifierResults(this.user.getId(), mc.getResults());
		
		return mc.getResults();
	}
	
	/**
	 * Per ogni risultato del classificatore restituisce un nuovo score basato su emoji.
	 * 
	 * @param ress - risultati classificatore
	 * @return
	 */
	public HashMap<ClassifierResult, String> classificationResultsEmojiScore(
														ArrayList<ClassifierResult> ress){
		HashMap<ClassifierResult, String> scoresEmoji = new HashMap<ClassifierResult, String>();
		for(ClassifierResult res : ress){
			String scoreEmoji = "";
			int score = (int)(res.getScore() * 5);
			for(int i=0; i<5; i++){
				if(i>score)
					scoreEmoji += EmojiParser.parseToUnicode(":white_circle:");
				else
					scoreEmoji += EmojiParser.parseToUnicode(":radio_button:");
			}
			scoresEmoji.put(res, scoreEmoji);
		}
		
		return scoresEmoji;
	}
	

	private final int MAXIMUM_LEV_DISTANCE = 3;
	public boolean isClassificationResults(String text){
		LevenshteinDistance ld = new LevenshteinDistance();
		ArrayList<ClassifierResult> ress = this.sddao.getClassifierResults(this.user.getId());
		for(ClassifierResult res: ress){
			int dist = ld.apply(text, res.getName());
			if(text.equalsIgnoreCase(res.getName()) || dist <= MAXIMUM_LEV_DISTANCE){
				//save on db
				this.sddao.saveClassifierResultSearch(this.user.getId(), text);
				return true;
			}
		}
		
		return false;
	}
	
	public String getLastTypedClassificationResult(){
		return this.sddao.getLastClassifierResultSearch(this.user.getId());
	}
	
	/**
	 * Restituisce i medici specialisti di una specifica specialità medica prendendo in input una 
	 * provincia.
	 */
	public ArrayList<Doctor> getSpecilists(String classifierResultName, String province){
		ArrayList<Doctor> doctors = sddao.getSpecilists(province, classifierResultName);
		
		if(doctors.size() > 0 )
			return doctors;
		else
			return null;
	}
	
	public String parseProvince(String text){
		String prov = ItalianProvince.getProvinceExact(text);
		if(prov != null)
			return prov;
		else{
			prov = ItalianProvince.getProvinceFuzzy(text);
			if(prov != null)
				return prov;
		}
		
		return null;
	}
	

}
