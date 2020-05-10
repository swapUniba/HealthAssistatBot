package com.triage.logic.response_producer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.MathTool;

import com.triage.logic.managers.SymptomCheckerManager;
import com.triage.logic.questions.SymptomCheckerQuestions;
import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.symptomChecker.SymcatCondition;
import com.triage.rest.models.symptomChecker.SymcatSymptom;
import com.triage.rest.models.users.User;

public class ResponseProducerSymptomChecker extends ResponseProducerAbstract{

	SymptomCheckerManager scmanager;
	
	public ResponseProducerSymptomChecker(RestRequestInput restRequest, User user, Question lastq) {
		super(restRequest, user, lastq);
		this.setQstdao(new SymptomCheckerQuestions());
		this.scmanager = new SymptomCheckerManager(this.getUser());
	}
	
	@Override
	public Response produceResponse() {
		Question nextq = null;
		String lastQuestionName = this.getLastq().getQuestionName();
		
		//Bot: "inserisci un sintomo" (prima interazione)
		if(lastQuestionName.equalsIgnoreCase(SymptomCheckerQuestions.DESCRIBE_SYMPTOM)){
			//this.text è la frase da cui estrarre un sintomo
			scmanager.startSymptomChecker();
			ArrayList<SymcatSymptom> symptoms = scmanager.parseSymptoms(this.getText(), true);
			
			if(symptoms != null){
				LinkedHashMap<SymcatCondition, Double> diseases = scmanager.makeDiagnosis(symptoms);
				ArrayList<SymcatCondition> diseasesKeys = new ArrayList<SymcatCondition>(diseases.keySet());
				ArrayList<SymcatSymptom> recommendedSymptoms = scmanager.recommendNextSymptom(diseasesKeys);
				
				nextq = this.getQstdao().getQuestion(SymptomCheckerQuestions.SYMPTOMCHECKER_RESULTS);
				nextq = buildSymptomcheckerResults(nextq, symptoms, diseases, recommendedSymptoms);
			}else{
				nextq = this.getQstdao().getQuestion(SymptomCheckerQuestions.DESCRIBE_SYMPTOM);
				nextq.setPreText("Non sono riuscito a individuare alcun sintomo. Prova a riformulare la  frase in maniera differente. Sto ancora imparando! :)");
			}
		}
		
		//Bot: "Risultati <results>. Scegli fra i sintomi raccomandati o premi nuova consultazione
		// per iniziare da capo."
		if(lastQuestionName.equalsIgnoreCase(SymptomCheckerQuestions.SYMPTOMCHECKER_RESULTS)){
			if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
				//this.text = Nuova consultazione
				nextq = this.getQstdao().getQuestion(SymptomCheckerQuestions.DESCRIBE_SYMPTOM);
				nextq.setPreText("");
			}else{
				//this.text è un sintomo raccomandato (o una nuova frase TODO)
				ArrayList<SymcatSymptom> symptoms = scmanager.addNewSymptom(this.getText());
				
				if(symptoms != null){
					LinkedHashMap<SymcatCondition, Double> diseases = scmanager.makeDiagnosis(symptoms);
					ArrayList<SymcatCondition> diseasesKeys = new ArrayList<SymcatCondition>(diseases.keySet());
					ArrayList<SymcatSymptom> recommendedSymptoms = scmanager.recommendNextSymptom(diseasesKeys);
					
					nextq = this.getQstdao().getQuestion(SymptomCheckerQuestions.SYMPTOMCHECKER_RESULTS);
					nextq = buildSymptomcheckerResults(nextq, symptoms, diseases, recommendedSymptoms);
				}else{
					// è una nuova frase
					symptoms = scmanager.parseSymptoms(this.getText(), false);
					
					LinkedHashMap<SymcatCondition, Double> diseases = scmanager.makeDiagnosis(symptoms);
					ArrayList<SymcatCondition> diseasesKeys = new ArrayList<SymcatCondition>(diseases.keySet());
					ArrayList<SymcatSymptom> recommendedSymptoms = scmanager.recommendNextSymptom(diseasesKeys);
					
					nextq = this.getQstdao().getQuestion(SymptomCheckerQuestions.SYMPTOMCHECKER_RESULTS);
					nextq = buildSymptomcheckerResults(nextq, symptoms, diseases, recommendedSymptoms);
				}
			}
		}
		
		if(nextq == null){
			nextq = this.getQstdao().getQuestion(SymptomCheckerQuestions.DESCRIBE_SYMPTOM);
			nextq.setPreText("Si è verificato un errore! :( ");
			nextq.setPreText("");
		}
		this.setNextq(nextq);
		return createResponseObject();
	}
	
	/**
	 * Costruisce l'oggetto Question per la domanda SYMPTOMCHECKER_RESULTS.
	 */
	private static Question buildSymptomcheckerResults(Question nextq, 
			ArrayList<SymcatSymptom> parsedSymptoms, 
			LinkedHashMap<SymcatCondition, Double> diseases, 
			ArrayList<SymcatSymptom> recommendedSymptoms){
		//System.out.println("---DEBUG sc" + parsedSymptoms);
		//System.out.println("---DEBUG sc" + diseases);
		//System.out.println("---DEBUG sc" + recommendedSymptoms);
		LinkedHashMap<SymcatCondition, Integer> cleanedDiseases = new LinkedHashMap<SymcatCondition, Integer>();
		for(SymcatCondition sc : diseases.keySet()){
			double score = diseases.get(sc);
			score = score * 100 * 0.95;
			int rounded = (int) Math.round(score);
			if(rounded <= 0)
				rounded = 1;
			cleanedDiseases.put(sc, rounded);
		}
		
		
		VelocityContext vc = new VelocityContext();
		
		if(cleanedDiseases.size() == 0 || cleanedDiseases == null){
			vc.put("diseasesResults", false);
		}else{
			vc.put("diseasesResults", true);
			vc.put("diseases", cleanedDiseases);
			vc.put("parsedSymptoms", parsedSymptoms);
		}
		System.out.println("DEBUG ---: " + cleanedDiseases);
		if(recommendedSymptoms.size() == 0 || recommendedSymptoms == null){
			vc.put("recommendation", false);
		}else{
			vc.put("recommendation", true);
			if(recommendedSymptoms.size() > 3)
				vc.put("recommendedSymptoms", recommendedSymptoms.subList(0, 3)); //Put only first 3
			else
				vc.put("recommendedSymptoms", recommendedSymptoms);
		}
		vc.put("math", new MathTool());
		
		int maxRecommendation = 3;
		if(recommendedSymptoms.size() >= 3){
			maxRecommendation = 3;
		}else{
			maxRecommendation = recommendedSymptoms.size();
		}
		for(SymcatSymptom rs : recommendedSymptoms.subList(0, maxRecommendation)){
			Answer as = new Answer(StringUtils.capitalize(rs.getName()));
			nextq.addSingleAnswer(as);
		}
		
		((QuestionTemplate)nextq).setVelocityContex(vc);
		return nextq;
	}

}
