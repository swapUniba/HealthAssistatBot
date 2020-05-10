package com.triage.logic.questions;

import java.util.ArrayList;

import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.QuestionText;

public class MedicalDictionaryQuestions extends AbstractQuestions{
	public MedicalDictionaryQuestions() {}
	
	public static final String SEARCH = "SEARCH";
	public static final String DO_YOU_MEAN = "DO_YOU_MEAN";
	public static final String RESULT = "RESULT";
	
	
	@Override
	public ArrayList<Question> initializeQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		/* Medical Dictionary */
		String description = "Con questo servizio puoi cercare un sintomo o una patologia di cui vuoi approfondire il significato. \n\n";
		Question search = new QuestionText(SEARCH, "Inserisci il termine che vorresti cercare");
		search.setPreText(description);
		questions.add(search);
		
		Question doYouMean = new QuestionText(DO_YOU_MEAN, "Forse intendevi:");
		questions.add(doYouMean);
		
		Question dict_descres = new QuestionTemplate(RESULT, "com/triage/template/medicalDictionary/result.vm");
		questions.add(dict_descres);
		
		//TODO check if is needed
		/*Question dict_nores = new QuestionText(QuestionName.DICT_NORESULTS, "Non è stato individuato nessun risultato. Digita una altro termine da ricercare");
		questions.add(dict_nores);*/
		
		return questions;
	}
}
