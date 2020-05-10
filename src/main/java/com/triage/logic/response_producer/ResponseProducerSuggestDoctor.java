package com.triage.logic.response_producer;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.MathTool;

import com.triage.logic.managers.SuggestDoctorManager;
import com.triage.logic.questions.SuggestDoctorQuestions;
import com.triage.rest.models.classifier.ClassifierResult;
import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.suggestDoctor.Doctor;
import com.triage.rest.models.users.User;
import com.vdurmont.emoji.EmojiParser;

public class ResponseProducerSuggestDoctor extends ResponseProducerAbstract{
	
	SuggestDoctorManager sdmanager;
	
	public ResponseProducerSuggestDoctor(RestRequestInput restRequest, User user, Question lastq) {
		super(restRequest, user, lastq);
		this.setQstdao(new SuggestDoctorQuestions());
		this.sdmanager = new SuggestDoctorManager(this.getUser());
	}
	
	@Override
	public Response produceResponse(){
		Question nextq = null;
		String lastQuestionName = this.getLastq().getQuestionName();
		//Test no error db! again
		
		//Bot: "descrivi il tuo stato di salute"
		if(lastQuestionName.equalsIgnoreCase(SuggestDoctorQuestions.DESCRIBE_HEALT_STATE) ||
			lastQuestionName.equalsIgnoreCase(SuggestDoctorQuestions.SPECIALISTS)){
			//this.text è la frase da analizzare
			ArrayList<ClassifierResult> ress = sdmanager.executeClassification(this.getText());
			HashMap<ClassifierResult, String> scoresEmoji = sdmanager.classificationResultsEmojiScore(ress);
			
			nextq = this.getQstdao().getQuestion(SuggestDoctorQuestions.CLASSIFIER_RESULTS);
			nextq = buildClassifierResults(nextq, ress, scoresEmoji);
		}
		
		//Bot: "<risultati>. Vuoi che ti suggerisca uno specialista nella tua zona?"
		if(lastQuestionName.equalsIgnoreCase(SuggestDoctorQuestions.CLASSIFIER_RESULTS)){
			//this.text è il nome di un risultato del classificatore o un nuovo stato di salute
			boolean isClassResult = sdmanager.isClassificationResults(this.getText());
			if(isClassResult){
				//this.text è il nome di un risultato del classificatore
				nextq = this.getQstdao().getQuestion(SuggestDoctorQuestions.WHERE_YOU_LIVE);
			}else{
				//this.text è una nuova frase da analizzare
				ArrayList<ClassifierResult> ress = sdmanager.executeClassification(this.getText());
				HashMap<ClassifierResult, String> scoresEmoji = sdmanager.classificationResultsEmojiScore(ress);
				
				nextq = this.getQstdao().getQuestion(SuggestDoctorQuestions.CLASSIFIER_RESULTS);
				nextq = buildClassifierResults(nextq, ress, scoresEmoji);
			}
		}
		
		//Bot: "Inserisci la provincia dove ricercare?"
		if(lastQuestionName.equalsIgnoreCase(SuggestDoctorQuestions.WHERE_YOU_LIVE)){
			//this.text è il nome di una provincia
			String prov = sdmanager.parseProvince(this.getText());
			if(prov != null){
				String classResName = sdmanager.getLastTypedClassificationResult();
				ArrayList<Doctor> doctors = sdmanager.getSpecilists(classResName, prov);
				
				if(doctors == null || doctors.size() == 0){
					nextq = this.getQstdao().getQuestion(SuggestDoctorQuestions.WHERE_YOU_LIVE);
					nextq.setPreText("Non ho trovato nessun specialista nell'area \"*" + 
							classResName.replace("-", " ") + "*\" che opera in provincia di *" + prov + "*. ");
				}else{
					nextq = this.getQstdao().getQuestion(SuggestDoctorQuestions.SPECIALISTS);
					nextq = buildSpecialists(nextq, classResName, prov, doctors);
				}
			}else{
				nextq = this.getQstdao().getQuestion(SuggestDoctorQuestions.WHERE_YOU_LIVE);
				nextq.setPreText("Non ho capito. ");
			}
		}
		
		
		if(nextq == null){
			nextq = this.getQstdao().getQuestion(SuggestDoctorQuestions.DESCRIBE_HEALT_STATE);
			nextq.setPreText("Si è verificato un errore! :( ");
			nextq.setPreText("");
		}
		this.setNextq(nextq);
		return createResponseObject();
	}
	
	/**
	 * Costruisce l'oggetto Question per la domanda CLASSIFIER_RESULTS.
	 */
	private static Question buildClassifierResults(Question nextq, ArrayList<ClassifierResult> ress, 
												HashMap<ClassifierResult, String> scoresEmoji){
		VelocityContext vc = new VelocityContext();
		if(ress.size() == 0 || ress == null)
			vc.put("results", false);
		else{
			vc.put("results", true);
			vc.put("medical_area", ress);
			vc.put("scoresEmoji", scoresEmoji);
			vc.put("math", new MathTool());
		}
		
		for(ClassifierResult res : ress){
			Answer as = new Answer(res.getName());
			nextq.addSingleAnswer(as);
		}
		
		((QuestionTemplate)nextq).setVelocityContex(vc);
		return nextq;
	}
	
	/**
	 * Costruisce l'oggetto Question per la domanda SPECIALISTS.
	 */
	private static Question buildSpecialists(Question nextq, String classResName, 
											String province, ArrayList<Doctor> doctors){
		VelocityContext vc = new VelocityContext();
		if(doctors == null || doctors.size() == 0){
			vc.put("results", false);
			vc.put("classRes", classResName);
			vc.put("province", province);
		}
		else{
			//Generate emojis for every doctor
			HashMap<Doctor,String> doctorsWithEmoji = new HashMap<Doctor, String>();
			for(Doctor doc : doctors){
				int nrStar = (int) Math.ceil(((double)doc.getActivityTotal() / 100) * 5);

				String emojiStars = "";
				for(int i=0; i<5; i++){
					if(i < nrStar)
						emojiStars += EmojiParser.parseToUnicode(":star2:");
					else
						break;
				}
				doctorsWithEmoji.put(doc, emojiStars);
			}
			
			vc.put("results", true);
			vc.put("classRes", classResName);
			vc.put("province", province);
			vc.put("doctors", doctors);
			vc.put("doctorsEmoji", doctorsWithEmoji);
		}
		
		((QuestionTemplate)nextq).setVelocityContex(vc);
		return nextq;
	}
}
