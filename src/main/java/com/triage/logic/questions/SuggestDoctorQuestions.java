package com.triage.logic.questions;

import java.util.ArrayList;

import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.QuestionText;

public class SuggestDoctorQuestions extends AbstractQuestions{
	public SuggestDoctorQuestions() {}
	
	public static final String DESCRIBE_HEALT_STATE = "DESCRIBE_HEALT_STATE";
	public static final String CLASSIFIER_RESULTS = "CLASSIFIER_RESULTS";
	public static final String WHERE_YOU_LIVE = "WHERE_YOU_LIVE";
	public static final String DYM_PROVINCE = "DYM_PROVINCE";
	public static final String SPECIALISTS = "SPECIALISTS";
	
	@Override
	public ArrayList<Question> initializeQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		/* Suggest Doctor */
		String description = "Con questo servizio puoi descrivere il tuo stato di salute ed individuare uno specialista che potrebbe aiutarti a risolverlo. \n\n";
		Question describeHealtState = new QuestionText(DESCRIBE_HEALT_STATE, "Descrivi con una frase il tuo stato di salute per cui stai cercando un medico:");
		describeHealtState.setPreText(description);
		questions.add(describeHealtState);
		
		Question classifierResults = new QuestionTemplate(CLASSIFIER_RESULTS, "com/triage/template/suggestDoctor/classifierResults.vm");
		questions.add(classifierResults);
		
		Question whereYouLive = new QuestionText(WHERE_YOU_LIVE, "Inserisci la provincia per la quale vuoi che ti siano suggeriti i medici");
		questions.add(whereYouLive);
		
		Question specialists = new QuestionTemplate(SPECIALISTS, "com/triage/template/suggestDoctor/specialists.vm");
		questions.add(specialists);
		
		return questions;
	}
}
