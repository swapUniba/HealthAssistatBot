package com.triage.logic.questions;

import java.util.ArrayList;

import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.QuestionText;

public class SymptomCheckerQuestions extends AbstractQuestions{

	public SymptomCheckerQuestions() {}

	public static final String DESCRIBE_SYMPTOM = "DESCRIBE_SYMPTOM";
	public static final String SYMPTOMCHECKER_RESULTS = "SYMPTOMCHECKER_RESULTS";
	public static final String RECOGNIZED_SYMPTOM = "RECOGNIZED_SYMPTOM";
	public static final String CLASSIFIER_RESULTS = "CLASSIFIER_RESULTS";
	
	
	@Override
	public ArrayList<Question> initializeQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		String description = "Con questo servizio puoi descrivere il tuo stato di salute e vedere quali sono le malattie potenzialmente correlate. \n\n";
		Question describeSymptom = new QuestionText(DESCRIBE_SYMPTOM, "Descrivi con una frase i tuoi sintomi:");
		describeSymptom.setPreText(description);
		questions.add(describeSymptom);
		
		Question symptomcheckerResults = new QuestionTemplate(SYMPTOMCHECKER_RESULTS, "com/triage/template/symptomChecker/symptomcheckerResults.vm");
		Answer scr1 = new Answer("Nuova consultazione");
		symptomcheckerResults.addSingleAnswer(scr1);
		questions.add(symptomcheckerResults);
		
		
		return questions;
	}

}
