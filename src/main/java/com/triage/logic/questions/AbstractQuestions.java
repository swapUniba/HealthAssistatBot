package com.triage.logic.questions;

import java.util.ArrayList;

import com.triage.rest.models.messages.Question;

public abstract class AbstractQuestions {
	protected ArrayList<Question> questions;
	
	public AbstractQuestions(){
		questions = initializeQuestions();
	}

	//permette di leggere una delle question inizializzate
	public Question getQuestion(String questionName){
		for(Question q : this.questions)
			if(q.getQuestionName().equals(questionName))
				return q;
		return null;
	}

	public abstract ArrayList<Question> initializeQuestions();
}
