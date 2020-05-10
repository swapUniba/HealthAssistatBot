package com.triage.logic.response_producer;

import java.util.ArrayList;

import com.triage.logic.questions.SurveyQuestions;
import com.triage.rest.dao.SurveyDao;
import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.users.User;

public class ResponseProducerSurvey extends ResponseProducerAbstract{
	private SurveyDao sdao;
	
	public ResponseProducerSurvey(RestRequestInput restRequest, User user, Question lastq) {
		super(restRequest, user, lastq);
		this.setQstdao(new SurveyQuestions());
		sdao = new SurveyDao();
		//this.smanager = new SymptomCheckerManager(this.getUser());
	}
	
	@Override
	public Response produceResponse() {
		Question nextq = null;
		String lastQuestionName = this.getLastq().getQuestionName();
		//if(this.getText().equals("/start") || this.getText().equalsIgnoreCase("menu")
		//		|| this.getLastq() == null){
		
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.MENU) ||
				this.getLastq() == null){
			//this.sdao.createNewSurvey(this.getUser().getId());
			nextq = this.getQstdao().getQuestion(SurveyQuestions.Q1_ALL);
		}
		
		//Questionario (Domande pre questionario + domande symptom checker)
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q1_PRE)){
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q1_PRE);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q2_PRE);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q1_PRE);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q2_PRE)){
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q2_PRE);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q3_PRE);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q2_PRE);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q3_PRE)){
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q3_PRE);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q1_ALL);
				this.setNcol(5);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q3_PRE);
				nextq.setPreText("Non ho capito. ");
			}
		}
		
		
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q1_ALL)){
			this.setNcol(5);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q1_ALL);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q2_ALL);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q1_ALL);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q2_ALL)){
			this.setNcol(5);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q2_ALL);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q3_ALL);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q2_ALL);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q3_ALL)){
			this.setNcol(5);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q3_ALL);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q4_ALL);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q3_ALL);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q4_ALL)){
			this.setNcol(5);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q4_ALL);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q5_ALL);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q4_ALL);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q5_ALL)){
			this.setNcol(5);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q5_ALL);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q6_ALL);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q5_ALL);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q6_ALL)){
			this.setNcol(5);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q6_ALL);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q7_ALL);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q6_ALL);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q7_ALL)){
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q7_ALL);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q1_FIN);
				this.setNcol(2);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q7_ALL);
				this.setNcol(5);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q1_FIN)){
			this.setNcol(2);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q1_FIN);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q2_FIN);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q1_FIN);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q2_FIN)){
			this.setNcol(2);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q2_FIN);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q3_FIN);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q2_FIN);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q3_FIN)){
			this.setNcol(2);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q3_FIN);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q4_FIN);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q3_FIN);
				nextq.setPreText("Non ho capito. ");
			}
		}if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q4_FIN)){
			this.setNcol(2);
			if(containendInAnswers(this.getText(), this.getLastq().getAnswers())){
				this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q4_FIN);
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q5_FIN);
			}else{
				nextq = this.getQstdao().getQuestion(SurveyQuestions.Q4_FIN);
				nextq.setPreText("Non ho capito. ");
			}
		}
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.Q5_FIN)){
			this.sdao.updateQuestionValue(this.getUser().getId(), this.getText(), SurveyQuestions.Q5_FIN);
			nextq = this.getQstdao().getQuestion(SurveyQuestions.END);
		}
		
		//Grazie per aver partecipato alla nostra sperimentazione.
		if(lastQuestionName.equalsIgnoreCase(SurveyQuestions.END)){
			nextq = this.getQstdao().getQuestion(SurveyQuestions.END);
		}
		
		nextq.setMenuButton(false);
		this.setNextq(nextq);
		return createResponseObject();
	}
	
	public static boolean containendInAnswers(String text, ArrayList<Answer> answers){
		for(Answer a : answers){
			if(a.getText().equalsIgnoreCase(text))
				return true;
		}
		
		return false;
	}

}
