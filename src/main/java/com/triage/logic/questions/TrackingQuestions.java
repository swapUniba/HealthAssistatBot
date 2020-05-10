package com.triage.logic.questions;

import java.util.ArrayList;
import java.util.Calendar;

import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.QuestionText;
import com.vdurmont.emoji.EmojiParser;

public class TrackingQuestions extends AbstractQuestions{
	public TrackingQuestions() {}
	
	public static final String MENU = "TRACKING_MENU";
	public static final String ADD_NAME = "ADD_NAME";
	public static final String ADD_IMAGE_OLD_MONITORING = "ADD_IMAGE_OLD_MONITORING";
	public static final String ADD_IMAGE_NEW_MONITORING = "ADD_IMAGE_NEW_MONITORING";
	public static final String UPDATE_NAME = "UPDATE_NAME";
	//public static final String DELETE_ONE = "DELETE_ONE";
	public static final String SEE_ALL = "SEE_ALL";
	public static final String SEE_ONE = "SEE_ONE";
	public static final String SELECT_TRACKING_IMAGE = "SELECT_TRACKING_IMAGE";
	public static final String SEE_IMAGE_DETAILS = "SELECT_IMAGE_DETAILS";
	public static final String SEE_IMAGE_DETAILS_ERROR = "SELECT_IMAGE_DETAILS_ERROR";
	public static final String SEE_EXAM_DETAILS = "SELECT_EXAM_DETAILS";
	public static final String SEE_EXAM_DETAILS_ERROR= "SELECT_EXAM_DETAILS_ERROR";
	public static final String UPDATE_EXAM_NAME = "UPDATE_EXAM_NAME";
	public static final String UPDATE_EXAM_RESULT = "UPDATE_EXAM_RESULT";
	public static final String UPDATE_EXAM_MIN = "UPDATE_EXAM_MIN";
	public static final String UPDATE_EXAM_MAX= "UPDATE_EXAM_MAX";
	public static final String UPDATE_EXAM_UNIT= "UPDATE_EXAM_UNIT";
	public static final String ADD_EXAM_NAME = "ADD_EXAM_NAME";
	public static final String ADD_EXAM_RESULT = "ADD_EXAM_RESULT";
	public static final String ADD_EXAM_RANGE = "ADD_EXAM_RANGE";
	//public static final String ADD_EXAM_MAX= "ADD_EXAM_MAX";
	public static final String ADD_EXAM_UNIT= "ADD_EXAM_UNIT";

	public static final String SEE_REMINDERS= "SEE_REMINDERS";
	public static final String SEE_REMINDER_DETAILS= "SEE_REMINDER_DETAILS";
	public static final String SEE_REMINDER_DETAILS_ERROR= "SEE_REMINDER_DETAILS_ERRO";
	public static final String UPDATE_REMINDER_HOUR= "UPDATE_REMINDER_HOUR";
	public static final String UPDATE_REMINDER_DATE= "UPDATE_REMINDER_DATE";
	public static final String NO_EXAM_REMINDERS = "NO_EXAM_REMINDERS";
	public static final String ADD_EXAM_REMINDER_NAME = "ADD_EXAM_REMINDER_NAME";
	public static final String ADD_EXAM_REMINDER_HOUR = "ADD_EXAM_REMINDER_HOUR";
	public static final String ADD_EXAM_REMINDER_DATE = "ADD_EXAM_REMINDER_DATE";

	public static final String SEARCH = "SEARCH";
	public static final String SEARCH_RESULT = "SEARCH_RESULT";

	public static final String ADD_DATE = "ADD_DATE";
	public static final String UPDATE_DATE = "UPDATE_DATE";
	public static final String CHART_REQUEST="CHART_REQUEST";
	public static final String CHART_REPLY="CHART_REPLY";
    public static final String CHART_RANGE="CHART_RANGE";
	public static final String CHART_PERIOD="CHART_PERIOD";
	//public static final String CHART_END_YEAR="CHART_END_YEAR";
	public static final String CHART_ALL_DATA="CHART_ALL_DATA";
    public static final String EXAM_DISAMBIGUATION="EXAM_DISAMBIGUATION";

	@Override
	public ArrayList<Question> initializeQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		Answer back = new Answer("Indietro");
		/* Tracking */
		String description = "Con questo servizio puoi memorizzare le tue analisi e successivamente ricercarle in base al loro contenuto. \n\n";
		Question menu = new QuestionText(MENU, "Scegli una sezione");
		menu.setPreText(description);
		Answer m2 = new Answer("Aggiungi monitoraggio");
		menu.addSingleAnswer(m2);
		Answer m1 = new Answer("Visualizza monitoraggi");
		menu.addSingleAnswer(m1);
		Answer m3 = new Answer("Ricerca nei monitoraggi");
		menu.addSingleAnswer(m3);
		Answer m4 = new Answer("Visualizza grafico esame");
		menu.addSingleAnswer(m4);
		Answer m5 = new Answer("Notifiche esami");
		menu.addSingleAnswer(m5);
		questions.add(menu);
		
		Question addName = new QuestionText(ADD_NAME, "Inserisci il nome del monitoraggio");
		questions.add(addName);
		Question addDate = new QuestionText(ADD_DATE, "Inserisci la data del referto");
		questions.add(addDate);

		Question addImageOm = new QuestionText(ADD_IMAGE_OLD_MONITORING, EmojiParser.parseToUnicode(
						"Premi :paperclip: e scatta una fotografia al tuo referto.\n\n"));
		addImageOm.addSingleAnswer(back);
		questions.add(addImageOm);
		Question addImageNm = new QuestionText(ADD_IMAGE_NEW_MONITORING, EmojiParser.parseToUnicode(
				"Premi :paperclip: e scatta una fotografia al tuo referto.\n\n"));
		questions.add(addImageNm);
		Question updateName = new QuestionText(UPDATE_NAME, "Inserisci il nuovo nome");
		updateName.addSingleAnswer(back);
		questions.add(updateName);
		Question updateDate = new QuestionText(UPDATE_DATE, "Inserisci la nuova data del referto");
		updateDate.addSingleAnswer(back);
		questions.add(updateDate);

		Question seeAll = new QuestionTemplate(SEE_ALL, "com/triage/template/tracking/seeAll.vm");
		questions.add(seeAll);
		Question seeOne = new QuestionTemplate(SEE_ONE, "com/triage/template/tracking/seeOne.vm");
		Answer so0 = new Answer("Aggiungi immagine/i");
		seeOne.addSingleAnswer(so0);
		Answer so1 = new Answer("Dettagli di un'immagine");
		seeOne.addSingleAnswer(so1);
		Answer so2 = new Answer("Modifica il nome di questo monitoraggio");
		seeOne.addSingleAnswer(so2);
		Answer so3 = new Answer("Modifica la data di questo monitoraggio");
		seeOne.addSingleAnswer(so3);
		Answer so4 = new Answer("Elimina questo monitoraggio");
		seeOne.addSingleAnswer(so4);
		seeOne.addSingleAnswer(back);
		questions.add(seeOne);
		Question tracking_image= new  QuestionText(SELECT_TRACKING_IMAGE,"Seleziona un'immagine.\n");
		questions.add(tracking_image);
		Question seeImageDetails = new QuestionTemplate(SEE_IMAGE_DETAILS, "com/triage/template/tracking/seeImageDetails.vm");
		questions.add(seeImageDetails);
		Question seeImageDetailsError = new QuestionText(SEE_IMAGE_DETAILS_ERROR, "Seleziona un esame.\n");
		questions.add(seeImageDetailsError);

		Question seeExamDetails = new QuestionTemplate(SEE_EXAM_DETAILS, "com/triage/template/tracking/seeExamDetails.vm");
		questions.add(seeExamDetails);
		Answer sed = new Answer("Mod. nome esame");
		seeExamDetails.addSingleAnswer(sed);
		Answer sed1 = new Answer("Mod. soglia minima");
		seeExamDetails.addSingleAnswer(sed1);
		Answer sed2 = new Answer("Mod. soglia massima");
		seeExamDetails.addSingleAnswer(sed2);
		Answer sed3 = new Answer("Mod. risultato");
		seeExamDetails.addSingleAnswer(sed3);
		Answer sed4 = new Answer("Mod. unità di misura");
		seeExamDetails.addSingleAnswer(sed4);
		seeExamDetails.addSingleAnswer(back);
		questions.add(seeExamDetails);
		Question seeExamDetailsError = new QuestionText(SEE_EXAM_DETAILS_ERROR, "Premi un pulsante.\n");
		seeExamDetailsError.addSingleAnswer(sed);
		seeExamDetailsError.addSingleAnswer(sed1);
		seeExamDetailsError.addSingleAnswer(sed2);
		seeExamDetailsError.addSingleAnswer(sed3);
		seeExamDetailsError.addSingleAnswer(sed4);
		seeExamDetailsError.addSingleAnswer(back);
		questions.add(seeExamDetailsError);


		Question updexamname= new QuestionText(UPDATE_EXAM_NAME, "Inserisci il nuovo nome\n");
		updexamname.addSingleAnswer(back);
		Question updexammin= new QuestionText(UPDATE_EXAM_MIN, "Inserisci la soglia minima\n");
		updexammin.addSingleAnswer(back);
		Question updexammax= new QuestionText(UPDATE_EXAM_MAX, "Inserisci la soglia massima\n");
		updexammax.addSingleAnswer(back);
		Question updexamunit= new QuestionText(UPDATE_EXAM_UNIT, "Inserisci la nuova unità di misura\n");
		updexamunit.addSingleAnswer(back);
		Question updexamresult= new QuestionText(UPDATE_EXAM_RESULT, "Inserisci il nuovo risultato\n");
		updexamresult.addSingleAnswer(back);
		questions.add(updexamname);
		questions.add(updexammax);
		questions.add(updexammin);
		questions.add(updexamresult);
		questions.add(updexamunit);



		Question addexamname= new QuestionText(ADD_EXAM_NAME, "Inserisci il nome\n");
		addexamname.addSingleAnswer(back);
		Question addexamrange= new QuestionText(ADD_EXAM_RANGE, "Inserisci il range dell'esame nel formato: 'minimo-massimo'\n");
		addexamrange.addSingleAnswer(back);
		Question addexamunit= new QuestionText(ADD_EXAM_UNIT, "Inserisci l'unità di misura\n");
		addexamunit.addSingleAnswer(back);
		Question addexamresult= new QuestionText(ADD_EXAM_RESULT, "Inserisci il risultato\n");
		addexamresult.addSingleAnswer(back);
		questions.add(addexamname);
		questions.add(addexamrange);
		questions.add(addexamresult);
		questions.add(addexamunit);

		Question seeReminders = new QuestionTemplate(SEE_REMINDERS, "com/triage/template/tracking/seeExamsReminders.vm");
		questions.add(seeReminders);
		Question seeReminderDetails = new QuestionTemplate(SEE_REMINDER_DETAILS, "com/triage/template/tracking/seeOneExamReminder.vm");
		Answer sr = new Answer("Mod. data");
		seeReminderDetails.addSingleAnswer(sr);
		Answer sr1 = new Answer("Mod. ora");
		seeReminderDetails.addSingleAnswer(sr1);
		Answer sr2 = new Answer("Elimina notifica");
		seeReminderDetails.addSingleAnswer(sr2);
		seeReminderDetails.addSingleAnswer(back);
		questions.add(seeReminderDetails);

		Question seeReminderDetailsError= new QuestionText(SEE_REMINDER_DETAILS_ERROR, "Per procedere, premi il corrispondente bottone.\n");
		seeReminderDetailsError.addSingleAnswer(sr);
		seeReminderDetailsError.addSingleAnswer(sr1);
		seeReminderDetailsError.addSingleAnswer(sr2);
		seeReminderDetailsError.addSingleAnswer(back);
		questions.add(seeReminderDetailsError);

		Question updremdate= new QuestionText(UPDATE_REMINDER_DATE, "Inserisci la nuova data.\n");
		Question updremhour= new QuestionText(UPDATE_REMINDER_HOUR, "Inserisci l'orario (formato hh:mm).\n");
		questions.add(updremdate);
		questions.add(updremhour);

		Question noExamRem= new QuestionText(NO_EXAM_REMINDERS, " ");
		noExamRem.addSingleAnswer(new Answer("Aggiungi notifica"));
		Question examReminderName= new QuestionText(ADD_EXAM_REMINDER_NAME, "Inserisci il nome.\n");
		Question examReminderDate= new QuestionText(ADD_EXAM_REMINDER_DATE, "Inserisci la data.\n");
		Question examReminderHour= new QuestionText(ADD_EXAM_REMINDER_HOUR, "Inserisci l'orario (formato hh:mm).\n");
		questions.add(noExamRem);
		questions.add(examReminderDate);
		questions.add(examReminderHour);
		questions.add(examReminderName);

		Question search = new QuestionText(SEARCH, "Digita la parola da ricercare");
		questions.add(search);
		Question searchResult = new QuestionText(SEARCH_RESULT, "");
		questions.add(searchResult);

		//grafico
		Question chart_req= new QuestionText(CHART_REQUEST, "Inserisci il nome dell'esame a cui sei interessato.");
		questions.add(chart_req);

		Question chart_resp= new  QuestionText(CHART_REPLY,"Ecco il grafico richiesto.");
		chart_resp.addSingleAnswer(new Answer("Visualizza nuovo grafico"));
		questions.add(chart_resp);

        Question chart_range= new  QuestionTemplate(CHART_RANGE,"com/triage/template/tracking/seeRanges.vm");
        questions.add(chart_range);

		//grafico: ambiguità esame ricercato
        Question exam_disambiguation=  new  QuestionTemplate(EXAM_DISAMBIGUATION,"com/triage/template/tracking/seeExams.vm");
        questions.add(exam_disambiguation);

		//grafico: d
		Question chart_all_data= new QuestionText(CHART_ALL_DATA,"Vuoi visualizzare tutti i dati a disposizione?\n");
		chart_all_data.addSingleAnswer(new Answer("Si"));
		chart_all_data.addSingleAnswer(new Answer("No (specifico l'intervallo)"));
		questions.add(chart_all_data);

		//grafico: arco temporale
		Question chart_period= new QuestionText(CHART_PERIOD,"Specificare l'arco temporale.\n" +
				"Formato riconoscibile:" + "\n -Data iniziale e finale \n     (mm/aa - mm/aa) "+ "\n -Solo data iniziale \n     (mm/aa)\n");
		questions.add(chart_period);

		return questions;

	}
}
