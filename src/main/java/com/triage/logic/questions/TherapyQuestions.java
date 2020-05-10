package com.triage.logic.questions;

import java.util.ArrayList;

import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.QuestionText;

public class TherapyQuestions extends AbstractQuestions{
	public TherapyQuestions() {}
	
	public static final String MENU = "THERAPY_MENU";
	
	public static final String ADD_NAME = "ADD_NAME";
	public static final String ADD_DOSAGE = "ADD_DOSAGE";
	public static final String ADD_TYPE = "ADD_TYPE"; //values: tutti i giorni/a giorni alterni/in giorni specifici 
	public static final String ADD_START_DAY_ODD = "ADD_START_DAY_ODD"; // solo se scelto giorni alterni
	public static final String ADD_DAYS = "ADD_DAYS"; // solo se scelto in giorni specifici
	public static final String ADD_DAYS_CHECK = "ADD_DAYS_CHECK";
	public static final String ADD_HOUR = "ADD_HOUR";
	public static final String ADD_END_DATE = "ADD_END_DATE";
	public static final String ADD_DRUG_NAME = "ADD_DRUG_NAME";
	public static final String ADD_INTERVAL = "ADD_INTERVAL";
	
	public static final String UPDATE_NAME = "UPDATE_NAME";
	public static final String UPDATE_DOSAGE = "UPDATE_DOSAGE";
	public static final String UPDATE_END_DATE = "UPDATE_END_DATE";
	
	//public static final String DELETE_ONE = "DELETE_ONE";
	
	public static final String SEE_ALL = "SEE_ALL";
	public static final String SEE_ONE = "SEE_ONE";
	
	@Override
	public ArrayList<Question> initializeQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		/* Therapy */
		String description = "Con questo servizio puoi gestire le tue terapie ricevendo un alert quando dovrai assumere un farmaco. \n\n";
		Question menu = new QuestionText(MENU, "Scegli una sezione");
		menu.setPreText(description);
		Answer m2 = new Answer("Aggiungi terapia");
		menu.addSingleAnswer(m2);
		Answer m1 = new Answer("Visualizza terapie");
		menu.addSingleAnswer(m1);
		questions.add(menu);
		
		Question addName = new QuestionText(ADD_NAME, "Digita un nome per la nuova terapia");
		questions.add(addName);
		
		Question addDosage = new QuestionText(ADD_DOSAGE, "Inserisci il dosaggio");
		questions.add(addDosage);
		
		Question addDrugName = new QuestionText(ADD_DRUG_NAME, "Inserisci il nome del medicinale");
		questions.add(addDrugName);
		
		Question addHour = new QuestionText(ADD_HOUR, "Inserisci l'orario (formato hh:mm)");
		questions.add(addHour);
		
		Question addType = new QuestionText(ADD_TYPE, "In quali giorni della settimana è prevista la terapia?");
		Answer at1 = new Answer("Tutti i giorni");
		addType.addSingleAnswer(at1);
		Answer at2 = new Answer("A giorni alterni");
		addType.addSingleAnswer(at2);
		Answer at3 = new Answer("In giorni specifici");
		addType.addSingleAnswer(at3);
		Answer at4 = new Answer("Ad intervallo di giorni");
		addType.addSingleAnswer(at4);
		questions.add(addType);
		
		Question addInterval = new QuestionText(ADD_INTERVAL, "Ogni quanti giorni è prevista l'assunzione della terapia");
		questions.add(addInterval);
		
		Question addStartDayOdd = new QuestionText(ADD_START_DAY_ODD, "A partire da quale giorno vuoi ricevere le notifiche?\n Premi 'Oggi' o 'Domani' o indica una data nel formato GG/MM/AA");
		addStartDayOdd.addSingleAnswer(new Answer("Oggi"));
		addStartDayOdd.addSingleAnswer(new Answer("Domani"));
		questions.add(addStartDayOdd);
		
		Question addDays = new QuestionText(ADD_DAYS, "Inserisci i nomi dei giorni della settimana, *separati da uno spazio*, in cui è prevista la terapia");
		questions.add(addDays);
		
		Question addDaysCheck = new QuestionTemplate(ADD_DAYS_CHECK, "com/triage/template/therapy/addDaysCheck.vm");
		addDaysCheck.addSingleAnswer(new Answer("Sì"));
		addDaysCheck.addSingleAnswer(new Answer("No"));
		questions.add(addDaysCheck);
		
		Question addEndDate = new QuestionText(ADD_END_DATE, "Inserisci la data di fine terapia (formato GG/MM/AA)");
		Answer aed = new Answer("Non conosco il termine della terapia");
		addEndDate.addSingleAnswer(aed);
		questions.add(addEndDate);
		
		Question updateName = new QuestionText(UPDATE_NAME, "Digita un nuovo nome per la terapia");
		questions.add(updateName);
		
		Question updateDosage = new QuestionText(UPDATE_DOSAGE, "Inserisci il nuovo dosaggio");
		questions.add(updateDosage);
		
		Question updateEndDate = new QuestionText(UPDATE_END_DATE, "Inserisci la nuova data di fine terapia (formato GG/MM/AA)");
		Answer ued = new Answer("Non conosco il termine della terapia");
		updateEndDate.addSingleAnswer(ued);
		questions.add(updateEndDate);
		
		/*Question deleteOne = new QuestionTemplate(DELETE_ONE, "com/triage/template/therapy/deleteOne.vm");
		Answer do1 = new Answer("Si");
		deleteOne.addSingleAnswer(do1);
		Answer do2 = new Answer("No");
		deleteOne.addSingleAnswer(do2);
		questions.add(deleteOne);*/
		
		Question seeAll = new QuestionTemplate(SEE_ALL, "com/triage/template/therapy/seeAll.vm");
		questions.add(seeAll);
		Question seeOne = new QuestionTemplate(SEE_ONE, "com/triage/template/therapy/seeOne.vm");
		Answer so1 = new Answer("Modifica nome terapia");
		seeOne.addSingleAnswer(so1);
		Answer so2 = new Answer("Modifica dosaggio terapia");
		seeOne.addSingleAnswer(so2);
		Answer so3 = new Answer("Modifica data fine terapia");
		seeOne.addSingleAnswer(so3);
		Answer so4 = new Answer("Elimina questa terapia");
		seeOne.addSingleAnswer(so4);
		questions.add(seeOne);
		
		return questions;
	}

}
