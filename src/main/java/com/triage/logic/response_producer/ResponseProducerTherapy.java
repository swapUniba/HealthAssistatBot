package com.triage.logic.response_producer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;

import com.triage.logic.managers.TherapyManager;
import com.triage.logic.questions.TherapyQuestions;
import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.users.Therapy;
import com.triage.rest.models.users.TherapyType;
import com.triage.rest.models.users.User;
import com.vdurmont.emoji.EmojiParser;

public class ResponseProducerTherapy extends ResponseProducerAbstract{

	private TherapyManager tmanager;
	
	public ResponseProducerTherapy(RestRequestInput restRequest, User user, Question lastq) {
		super(restRequest, user, lastq);
		this.setQstdao(new TherapyQuestions());
		this.tmanager = new TherapyManager(this.getUser());
	}

	@Override
	public Response produceResponse() {
		Question nextq = null;
		String lastQuestionName = this.getLastq().getQuestionName();
		
		//Bot: mostra il menu
		if(lastQuestionName.equals(TherapyQuestions.MENU)){
			//this.text è una sezione del menu
			if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())){
				//this.text è 'Visualizza terapie'
				ArrayList<Therapy> therapies = this.tmanager.seeAllTherapies();
				
				if(therapies != null){
					nextq = this.getQstdao().getQuestion(TherapyQuestions.SEE_ALL);
					nextq = buildSeeAllQuestion(nextq, therapies);
				}else{ //Nessuna terapia inserita
					nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
					nextq.setPreText("Nessuna terapia inserita.");
				}
			}
			else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
				//this.text è 'Aggiungi terapia'
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_NAME);
			}else{
				nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
				nextq.setPreText("Voce del menu non esistente. ");
			}
		}
		
		//Bot: aggiungi il nome della nuova terapia
		if(lastQuestionName.equals(TherapyQuestions.ADD_NAME)){
			boolean repeated = this.tmanager.repeatedTherapyName(this.getText());
			
			if(!repeated){
				//this.text è il nome della nuova terapia
				this.tmanager.addNewTherapy(this.getText());	
				
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_DRUG_NAME);
			}else{
				//this.text è il nome già utilizzato da un altra terapia
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_NAME);
				nextq.setPreText("Il nome inserito è già utilizzato da un'altra terapia. ");
			}
		}
		
		//Bot: aggiungi il nome del medicinale della nuova terapia
		if(lastQuestionName.equals(TherapyQuestions.ADD_DRUG_NAME)){
			//this.text è il nome del medicinale della nuova terapia
			this.tmanager.addDrugName(this.getText());
			
			nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_DOSAGE);
		}
		
		
		
		//Bot: aggiungi il dosaggio della nuova terapia
		if(lastQuestionName.equals(TherapyQuestions.ADD_DOSAGE)){
			//this.text è il dosaggio della nuova terapia
			this.tmanager.addDosage(this.getText());
			
			nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_HOUR);
		}
		
		
				
		
		
		//Bot: aggiungi un orario per la nuova terapia
		if(lastQuestionName.equals(TherapyQuestions.ADD_HOUR)){
			//this.text è il dosaggio della nuova terapia o No (se non si vuole aggiungere un nuovo orario)
			if(!this.getText().equalsIgnoreCase("No")){
				boolean done = this.tmanager.addSingleHour(this.getText());
				
				if(done){ //Parsing ora riuscito
					nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_HOUR);
					nextq.addSingleAnswer(new Answer("No"));
					nextq.setPreText("Se la terapia è effettuata più volte nell'arco di una giornata puoi inserire un nuovo orario, altrimenti premi No.");
				}else{ //Parsing ora non riuscito
					nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_HOUR);
					nextq.setPreText("Non ho capito. ");
				}
			}else{
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_TYPE);
			}
		}
		
		//Bot: In quali giorni della settimana è prevista la terapia?
		if(lastQuestionName.equals(TherapyQuestions.ADD_TYPE)){
			TherapyType ttype = null;
			if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())){
				//A giorni alterni
				ttype = TherapyType.ODD_DAY;
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_START_DAY_ODD);
			}else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(2).getText())){
				//Solo alcuni giorni
				ttype = TherapyType.SOME_DAY;
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_DAYS);
			}else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
				//Tutti i giorni
				ttype = TherapyType.EVERY_DAY;
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
			}else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(3).getText())) {
				ttype = TherapyType.INTERVAL;
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_INTERVAL);
							}
			
			if(nextq == null){
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_TYPE);
				nextq.setPreText("Non ho capito :( ");
			}else{
				this.tmanager.addType(ttype);
			}
		}
		
		if(lastQuestionName.equals(TherapyQuestions.ADD_INTERVAL)) {
			int interval;
			
			try {
				interval = Integer.parseInt(this.getText());
				if (interval > 0) {
					nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_START_DAY_ODD);
					this.tmanager.addIntervalDays(interval);
				} else {
					nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_INTERVAL);
					nextq.setPreText("Inserire un numero maggiore di 0");
				}
				
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_INTERVAL);
				nextq.setPreText("Non ho capito :( ");
			}
		}
		
		
		
		//Bot: Inserisci la data di inizio per la terapia a giorni alterni
		if(lastQuestionName.equals(TherapyQuestions.ADD_START_DAY_ODD)){
			if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
				//Oggi
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				this.tmanager.addStartDayOdd(today.getTime());
				
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
			}else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())){
				//Domani
				Calendar tomorrow = Calendar.getInstance();
				tomorrow.set(Calendar.HOUR_OF_DAY, 0);
				tomorrow.set(Calendar.MINUTE, 0);
				tomorrow.set(Calendar.SECOND, 0);
				tomorrow.add(Calendar.HOUR, 24);
				this.tmanager.addStartDayOdd(tomorrow.getTime());
				
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
			}else{
				//Custom data
				Date parsedDate = this.tmanager.parseEndDate(this.getText());
				
				if(parsedDate != null){ //Parsing della data correttamente effettuato
					this.tmanager.addStartDayOdd(parsedDate);
					
					nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
				}else{
					nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_START_DAY_ODD);
					nextq.setPreText("Non ho capito. ");
				}
			}
		}
		
		//Bot: Inserisci i nomi dei giorni della settimana in cui è prevista la terapia:
		if(lastQuestionName.equals(TherapyQuestions.ADD_DAYS)){
			//Lista di giorni
			ArrayList<String> parsedDays = this.tmanager.addDays(this.getText());
			if(parsedDays != null){
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_DAYS_CHECK);
				nextq = buildAddDaysCheck(nextq, parsedDays);
			}else{
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_DAYS);
				nextq.setPreText("Non sono riuscito a capire. ");
			}
		}
		
		//Bot: Confermi che la terapia è eseguita nei seguenti giorni?
		if(lastQuestionName.equals(TherapyQuestions.ADD_DAYS_CHECK)){
			if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
				//Si
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
			}else{
				//No
				nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_DAYS);
			}
		}
		
		//Bot: aggiungi il giorno di fine terapia
		if(lastQuestionName.equals(TherapyQuestions.ADD_END_DATE)){
			if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
				//L'utente non conosce la fine della terapia
				this.tmanager.endTherapyCreation();
				
				nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
				nextq = buildPreTextAddEndDate(nextq, this.tmanager.getCronTherapyDescriptions());
			}else{
				Date parsedDate = this.tmanager.parseEndDate(this.getText());
				
				if(parsedDate != null){ //Parsing della data correttamente effettuato
					if(parsedDate.after(getCurrentDay()) || parsedDate.equals(getCurrentDay())){
						//Valid date
						this.tmanager.addEndDate(parsedDate);
						
						nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
						nextq = buildPreTextAddEndDate(nextq, this.tmanager.getCronTherapyDescriptions());
					}else{
						nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
						nextq.setPreText("Data non corretta. La data è precedente alla data attuale.");
					}
				}else{
					nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
					nextq.setPreText("Non ho capito. ");
				}
			}
		}
		
		//Bot: Terapie inserite: .... Scegli una terapia per osservarne i dettagli
		if(lastQuestionName.equals(TherapyQuestions.SEE_ALL)){
			//this.text è il nome di una terapia da visualizzare
			Therapy therapy = this.tmanager.seeTherapyDetails(this.getText());
						
			if(therapy == null){
				ArrayList<Therapy> therapies = this.tmanager.seeAllTherapies();
				
				if(therapies != null){
					nextq = this.getQstdao().getQuestion(TherapyQuestions.SEE_ALL);
					nextq = buildSeeAllQuestion(nextq, therapies);
					nextq.setPreText("Nessuna terapia col nome \"*" + this.getText() + "*\". ");
				}else{ //Nessuna terapia inserita
					nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
					nextq.setPreText("Nessuna terapia inserita.");
				}
			}else{
				ArrayList<String> italianCrons = this.tmanager.getCronTherapyDescriptions(therapy);
				nextq = this.getQstdao().getQuestion(TherapyQuestions.SEE_ONE);
				nextq = buildSeeOneQuestion(nextq, therapy, italianCrons);
			}
		}
		
		//Bot: Dettaglio terapia .... Scegli un azione da eseguire su questa terapia
		if(lastQuestionName.equals(TherapyQuestions.SEE_ONE)){
			//this.text è un operazione di modifica/eliminazione terapia
			//o il nome di una nuova terapia da osservare
			if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
				//Modifica nome terapia
				nextq = this.getQstdao().getQuestion(TherapyQuestions.UPDATE_NAME);
			}
			else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())){
				//Modifica dosaggio terapia
				nextq = this.getQstdao().getQuestion(TherapyQuestions.UPDATE_DOSAGE);
			}else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(2).getText())){
				//Modifica data fine terapia
				nextq = this.getQstdao().getQuestion(TherapyQuestions.UPDATE_END_DATE);
			}else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(3).getText())){
				//Elimina terapia
				this.tmanager.deleteTherapy();
				ArrayList<Therapy> therapies = this.tmanager.seeAllTherapies();
				
				if(therapies != null){
					nextq = this.getQstdao().getQuestion(TherapyQuestions.SEE_ALL);
					nextq = buildSeeAllQuestion(nextq, therapies);
				}else{ //Nessuna terapia inserita
					nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
					nextq.setPreText("");
				}
			}else{
				//Il nome di una nuova terapia da visualizzare
				Therapy therapy = this.tmanager.seeTherapyDetails(this.getText());
								
				if(therapy == null){
					ArrayList<Therapy> therapies = this.tmanager.seeAllTherapies();
					nextq = this.getQstdao().getQuestion(TherapyQuestions.SEE_ALL);
					nextq = buildSeeAllQuestion(nextq, therapies);
					nextq.setPreText("Nessuna terapia col nome \"*" + this.getText() + "*\". ");
				}else{
					ArrayList<String> italianCrons = this.tmanager.getCronTherapyDescriptions(therapy);
					nextq = this.getQstdao().getQuestion(TherapyQuestions.SEE_ONE);
					nextq = buildSeeOneQuestion(nextq, therapy, italianCrons);
				}
			}
		}
		
		//Bot: inserisci il nuovo nome per la terapia
		if(lastQuestionName.equals(TherapyQuestions.UPDATE_NAME)){
			//this.text è il nuovo nome della terapia
			boolean repeated = this.tmanager.repeatedTherapyName(this.getText());
			
			if(!repeated){
				this.tmanager.updateName(this.getText());
				
				nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
				nextq.setPreText("Aggiornamento effettuato.");
			}else{
				//this.text è il nome già utilizzato da un altra terapia
				nextq = this.getQstdao().getQuestion(TherapyQuestions.UPDATE_NAME);
				nextq.setPreText("Il nome inserito è già utilizzato da un'altra terapia. ");
			}
		}
		
		//Bot: inserisci il nuovo dosaggio per la terapia
		if(lastQuestionName.equals(TherapyQuestions.UPDATE_DOSAGE)){
			//this.text è il nuovo dosaggio della terapia
			this.tmanager.updateDosage(this.getText());
			
			nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
			nextq.setPreText("Aggiornamento effettuato.");
		}
		
		//Bot: inserisci il nuovo termine per la terapia
		if(lastQuestionName.equals(TherapyQuestions.UPDATE_END_DATE)){
			if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
				//L'utente non conosce la fine della terapia
				this.tmanager.endTherapyUpdate();
				
				nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
				nextq.setPreText("Aggiornamento effettuato.");
			}else{
				Date parsedDate = this.tmanager.parseEndDate(this.getText());
				
				if(parsedDate != null){ //Parsing della data correttamente effettuato
					if(parsedDate.after(getCurrentDay()) || parsedDate.equals(getCurrentDay())){
						//Valid date
						this.tmanager.updateEndDate(parsedDate);
						
						nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
						nextq.setPreText("Aggiornamento effettuato.");
					}else{
						nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
						nextq.setPreText("Data non corretta. La data è precedente alla data attuale.");
					}
				}else{
					nextq = this.getQstdao().getQuestion(TherapyQuestions.ADD_END_DATE);
					nextq.setPreText("Non ho capito. ");
				}
			}
		}
		
		/*if(nextq == null){
			//some error occurred: send user to menu
			ArrayList<Therapy> therapies = this.tmanager.seeAllTherapies();
			nextq = this.getQstdao().getQuestion(TherapyQuestions.MENU);
			if(therapies == null){
				ArrayList<Answer> answs = new ArrayList<Answer>();
				answs.add(nextq.getAnswers().get(0));
				nextq.setAnswers(answs);
			}
			nextq.setPreText("C'è stato un problema! :( ");
		}*/
		this.setNextq(nextq);
		return createResponseObject();
	}
	
	private static Question buildAddDaysCheck(Question nextq, ArrayList<String> parsedDays){	
		StringBuilder sb = new StringBuilder();
		for(String ps : parsedDays){
			sb.append(ps.toLowerCase());
			sb.append(",");
		}
		String daysString = sb.toString();
		daysString = daysString.substring(0, daysString.length()-1);
		
		VelocityContext vc = new VelocityContext();
		vc.put("daysString", daysString);
		((QuestionTemplate)nextq).setVelocityContex(vc);
		
		return nextq;
	}
	
	private static Question buildSeeAllQuestion(Question nextq, ArrayList<Therapy> therapies){
		ArrayList<String> therapiesEmoji = new ArrayList<String>();
		Date currentDate = getCurrentDate();
		for(Therapy therapy: therapies){
			Answer as = new Answer(therapy.getName());
			nextq.addSingleAnswer(as);
			
			System.out.println("---DEBUG:" + therapy);
			if(therapy.getEndTime() == null){
				//Active therapy
				therapiesEmoji.add(EmojiParser.parseToUnicode(":heavy_check_mark:"));
				continue;
			}else{
				Date lastNotificationDate = getMaxNotificationDate(therapy);
				if(lastNotificationDate.after(currentDate)){
					//Active therapy
					therapiesEmoji.add(EmojiParser.parseToUnicode(":heavy_check_mark:"));
				}else{
					//Inactive therapy
					therapiesEmoji.add(EmojiParser.parseToUnicode(":heavy_multiplication_x:"));
				}
			}
		}
		
		VelocityContext vc = new VelocityContext();
		vc.put("therapies", therapies);
		vc.put("therapiesEmoji", therapiesEmoji);
		vc.put("active", EmojiParser.parseToUnicode(":heavy_check_mark:"));
		vc.put("inactive", EmojiParser.parseToUnicode(":heavy_multiplication_x:"));
		((QuestionTemplate)nextq).setVelocityContex(vc);
		
		return nextq;
	}

	private static Question buildSeeOneQuestion(Question nextq, Therapy therapy, ArrayList<String> italianCrons){	
		VelocityContext vc = new VelocityContext();
		vc.put("therapy", therapy);
		vc.put("italianCrons", italianCrons);
		vc.put("dateTool", new DateTool());
		((QuestionTemplate)nextq).setVelocityContex(vc);
		
		return nextq;
	}
	
	private static Question buildPreTextAddEndDate(Question nextq, ArrayList<String> italianCrons){
		StringBuilder sb = new StringBuilder();
		sb.append("Terapia inserita correttamente riceverai un messaggio nelle "
					+ "seguenti date:\n");
		
		for(String ic : italianCrons){
			sb.append(" - " + ic +";\n");
		}
		
		sb.append("\n");
		nextq.setPreText(sb.toString());		
		return nextq;
	}
	
	private static Date getCurrentDate(){
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}
	
	private static Date getCurrentDay(){
		Calendar calendar = Calendar.getInstance();
		int year  = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date  = calendar.get(Calendar.DATE);
		calendar.clear();
		calendar.set(year, month, date);
		
		return calendar.getTime();
	}
	
	private static Date getMaxNotificationDate(Therapy therapy){
		SortedSet<Date> dates = new TreeSet<Date>();
		for(String timeStr : therapy.getHours()){
			String[] time = timeStr.trim().split(":");
			int hour = Integer.parseInt(time[0]);
			int min = Integer.parseInt(time[1]);
			
			Date notificationDate = DateUtils.setHours(therapy.getEndTime(), hour);
			notificationDate = DateUtils.setMinutes(notificationDate, min);
			dates.add(notificationDate);
		}
		
		return dates.last();
	}

}
