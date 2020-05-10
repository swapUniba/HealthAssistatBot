package com.triage.logic.response_producer;

import java.util.ArrayList;

import org.apache.velocity.VelocityContext;

import com.triage.logic.managers.MedicalDictionaryManager;
import com.triage.logic.questions.MedicalDictionaryQuestions;
import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.users.SearchResult;
import com.triage.rest.models.users.User;

public class ResponseProducerMedicalDictionary extends ResponseProducerAbstract{
	
	private MedicalDictionaryManager mdmanager;
	
	public ResponseProducerMedicalDictionary(RestRequestInput restRequest, User user, Question lastq) {
		super(restRequest, user, lastq);
		this.setQstdao(new MedicalDictionaryQuestions());
		this.mdmanager = new MedicalDictionaryManager(this.getUser());
	}

	@Override
	public Response produceResponse() {
		Question nextq = null;
		String lastQuestionName = this.getLastq().getQuestionName();
		
		//Bot: "Inserisci il termine che vorresti cercare"
		if(lastQuestionName.equals(MedicalDictionaryQuestions.SEARCH) ||
			lastQuestionName.equals(MedicalDictionaryQuestions.RESULT)){
			//this.text è un nuovo termine da ricercare
			nextq = search();
		}
		
		//Bot: "Forse intendevi"
		if(lastQuestionName.equals(MedicalDictionaryQuestions.DO_YOU_MEAN)){
			//this.text è uno dei termini fra le risposete di do_you_mean 
			// o un nuovo termine da ricercare
			SearchResult res = this.mdmanager.doYouMean(this.getText());
			
			if(res != null){
				nextq = this.getQstdao().getQuestion(MedicalDictionaryQuestions.RESULT);
				nextq = buildDescriptionResult(nextq, res);
			}else{ //this.text è un nuovo termine da ricercare
				nextq = search();
			}
		}
		
		if(nextq == null){
			//some error occurred: send user to menu
			nextq = this.getQstdao().getQuestion(MedicalDictionaryQuestions.SEARCH);
			nextq.setPreText("Si è verificato un errore! :( ");
			nextq.setPreText("");
		}
		this.setNextq(nextq);
		return createResponseObject();
	}

	/**
	 * Riceve i risultati della ricerca e sceglie la prossima domanda da restituire
	 * @return la domanda da chiedere
	 */
	private Question search(){
		Question nextq = null;
		ArrayList<SearchResult> results = this.mdmanager.searchTerm(this.getText());
		
		if(results.size() > 1){
			nextq = this.getQstdao().getQuestion(MedicalDictionaryQuestions.DO_YOU_MEAN);
			nextq = buildDidYouMean(nextq, results);
		}else{
			nextq = this.getQstdao().getQuestion(MedicalDictionaryQuestions.RESULT);
			
			if(results.size() == 0) //Nessun risultato
				nextq = buildDescriptionResult(nextq, null);
			else //Solo un risultato
				nextq = buildDescriptionResult(nextq, results.get(0));
		}
		return nextq;
	}
	
	/**
	 * Costruisce l'oggetto 'nextq'; inserendo le risposte standard.
	 * @param nextq - l'oggetto da costruire
	 * @param results - i risultati della ricerca
	 * @return
	 */
	private static Question buildDidYouMean(Question nextq, ArrayList<SearchResult> results){
		for(SearchResult sr: results){
			Answer as = new Answer(sr.getName());
			nextq.addSingleAnswer(as);
		}
		
		return nextq;
	}
	
	/**
	 * Costruisce l'oggetto 'nextq'; generando la risposta.
	 * @param nextq - l'oggetto da costruire
	 * @param results - il risultato della ricerca
	 * @return
	 */
	private static Question buildDescriptionResult(Question nextq, SearchResult result){
		VelocityContext vc = new VelocityContext();
		if(result == null){
			vc.put("results", false);
		}else{
			vc.put("results", true);
			vc.put("result", result);
		}
		
		((QuestionTemplate)nextq).setVelocityContex(vc);
		
		return nextq;
	}
}
