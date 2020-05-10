package com.triage.logic.managers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.triage.logic.TherapyNotificationCreator;
import com.triage.rest.dao.TherapyDao;
import com.triage.rest.models.users.Therapy;
import com.triage.rest.models.users.TherapyDay;
import com.triage.rest.models.users.TherapyType;
import com.triage.rest.models.users.User;
import com.triage.utils.NLPUtils;

import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;

public class TherapyManager {
	private User user;
	private TherapyDao tdao;
	
	public TherapyManager(User user){
		this.user = user;
		this.tdao = new TherapyDao();
	}
	
	/**
	 * Restituisce tutte le terapie di uno specifico utente. Se non ce ne sono 
	 * restituisce null
	 */
	public ArrayList<Therapy> seeAllTherapies(){
		ArrayList<Therapy> therapies = this.tdao.getAllTherapies(this.user.getId());
		
		if(therapies.size() >0)
			return therapies;
		else
			return null;
	}
	
	/**
	 * Controlla se il nome è già in uso per una terapia
	 */
	public boolean repeatedTherapyName(String therapyName){
		return this.tdao.repeatedTherapyName(this.user.getId(), therapyName);
	}
	
	/**
	 * Aggiunge una nuova terapia. Inserisce solamente il nome.
	 */
	public void addNewTherapy(String therapyName){
		this.tdao.addNewTherapy(this.user.getId(), therapyName);
	}
	
	/**
	 * Aggiunge il valore del dosaggio di una terapia
	 */
	public void addDosage(String therapyDosage){
		int thID = this.tdao.getLastCreatedTherapyID(this.user.getId());
		this.tdao.updateTherapyDosage(thID, therapyDosage);
	}
	/**
	 * Aggiunge il nome del medicinale di una terapia
	 */
	public void addDrugName (String drugName) {
		int thID = this.tdao.getLastCreatedTherapyID(this.user.getId());
		this.tdao.updateTherapyDrugName(thID, drugName);
	}
	
	
	/**
	 * Aggiunge il tipo di una terapia
	 */
	public void addType(TherapyType type){
		int thID = this.tdao.getLastCreatedTherapyID(this.user.getId());
		this.tdao.updateTherapyType(thID, type);
	}
	
	/**
	 * aggiunge i giorni di intervallo tra due istanze di due terapie
	 *
	 */
	public void addIntervalDays(int interval) {
		int thID = this.tdao.getLastCreatedTherapyID(this.user.getId());
		this.tdao.updateTherapyIntervalDays(thID, interval);
	}
	
	/**
	 * Aggiunge il valore dell'ora di notifica di una terapia.
	*/
	public boolean addSingleHour(String time){
		int thID = this.tdao.getLastCreatedTherapyID(this.user.getId());
		Date hourDate = NLPUtils.parseHour(time);

		if(hourDate != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(hourDate);
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			String hourStr = Integer.toString(hour);
			if(hour == 0) //Transforma 0 in 00
				hourStr += "0";
			
			int minute = cal.get(Calendar.MINUTE);
			String minuteStr = Integer.toString(minute);
			if(minute == 0) //Transforma 0 in 00
				minuteStr += "0";
			
			String finalTime = hourStr + ":" + minuteStr;
			this.tdao.addTherapyTime(thID, finalTime);
			return true;
		}

		return false;
	}
	
	public void addStartDayOdd(Date date){
		int thID = this.tdao.getLastCreatedTherapyID(this.user.getId());
		this.tdao.updateTherapyStartDate(thID, date);
	}
	
	/**
	 * Aggiunge il valore dell'ora di notifica di una terapia.
	*/
	public ArrayList<String> addDays(String textDays){
		int thID = this.tdao.getLastCreatedTherapyID(this.user.getId());
		this.tdao.removeLastTherapyDays(thID);
		ArrayList<String> parsedDays = TherapyDay.parseDays(textDays);
		
		if(parsedDays.size() != 0){
			for(String day : parsedDays){
				this.tdao.addTherapyDate(thID, day);
			}
			return parsedDays;
		}
		return null;
	}
	
	/**
	 * Aggiunge la data di fine terapia se conosciuta dall'utente e crea 
	 * un nuovo trigger per il Quartz Job.
	 */
	public void addEndDate(Date endDate){
		Therapy therapy = this.tdao.getLastCreatedTherapy(this.user.getId(), true);
		therapy.setEndTime(endDate);
		this.tdao.updateTherapyEndDate(therapy.getId(), therapy.getEndTime());
		
		//Start quartz job
		TherapyNotificationCreator tnc = new TherapyNotificationCreator(therapy, this.user);
		tnc.createNotification();
	}
	
	/**
	 * Conclude la creazione di una terapia. Setta i campi done e visible a true
	 * e crea i Quartz trigger.
	 */
	public void endTherapyCreation(){
		Therapy therapy = this.tdao.getLastCreatedTherapy(this.user.getId(), true);
		System.out.println("---DEBUG th" + therapy);
		this.tdao.endTherapyNoEndDate(therapy.getId());
		
		TherapyNotificationCreator tnc = new TherapyNotificationCreator(therapy, this.user);
		tnc.createNotification();
	}
	
	/**
	 * Restituisce una terapia utilizzando il nome. Prima di aggiornare o eliminare una 
	 * terapia bisogna visualizzarla. Utilizzo di last_visited per individuare su quale
	 * terapia effettuare cambiamenti.
	 */
	/**
	 * Restituisce una terapia fra quelle inserite usando il nome
	 */
	public Therapy seeTherapyDetails(String therapyName){
		Therapy therapy = this.tdao.getTherapy(this.user.getId(), therapyName);
		
		if(therapy != null){
			return therapy;
		}else{
			return null;
		}
	}
	
	/**
	 * Aggiorna il nome di una terapia
	 */
	public void updateName(String therapyName){
		Therapy therapy = this.tdao.getLastVisitedTherapy(this.user.getId(), true);
		
		//Update quartz trigger / therapy notification
		TherapyNotificationCreator tnc = new TherapyNotificationCreator(therapy, this.user);
		tnc.deleteNotification();
		therapy.setName(therapyName);//Update therapy object
		tnc.updateNotification(therapy, false);
		
		//Update db
		this.tdao.updateTherapyName(therapy.getId(), therapyName);
	}
	
	/**
	 * Aggiorna il dosaggio di una terapia
	 */
	public void updateDosage(String therapyDosage){
		Therapy therapy = this.tdao.getLastVisitedTherapy(this.user.getId(), true);
		
		//Update quartz trigger / therapy notification
		TherapyNotificationCreator tnc = new TherapyNotificationCreator(therapy, this.user);
		tnc.deleteNotification();
		therapy.setDosage(therapyDosage); //Update therapy object
		tnc.updateNotification(therapy, false);
		
		//Update db
		this.tdao.updateTherapyDosage(therapy.getId(), therapyDosage);
	}
	
	/**
	 * Aggiorna la data finale di una terapia
	 */
	public void updateEndDate(Date endDate){
		Therapy therapy = this.tdao.getLastVisitedTherapy(this.user.getId(), true);
			
		//Update quartz trigger / therapy notification
		TherapyNotificationCreator tnc = new TherapyNotificationCreator(therapy, this.user);
		tnc.deleteNotification();
		therapy.setEndTime(endDate); //Update therapy object
		tnc.updateNotification(therapy, true);
		
		//Update db
		this.tdao.updateTherapyEndDate(therapy.getId(), therapy.getEndTime());
	}
	
	public Date parseEndDate(String endDateStr){
		Date endDate = NLPUtils.parseDate(endDateStr);
		if(endDate != null)
			return endDate;
		else
			return null;
	}
	
	/**
	 * Conclude la modifica di una terapia. Setta i campi done e visible a true
	 * e crea i Quartz trigger.
	 */
	public void endTherapyUpdate(){
		Therapy therapy = this.tdao.getLastVisitedTherapy(this.user.getId(), true);
		therapy.setEndTime(null);
		this.tdao.endTherapyNoEndDate(therapy.getId());
		
		TherapyNotificationCreator tnc = new TherapyNotificationCreator(therapy, this.user);
		tnc.deleteNotification();
		tnc.createNotification();
	}
	
	/**
	 * Elimina una terapia
	 */
	public void deleteTherapy(){
		Therapy therapy = this.tdao.getLastVisitedTherapy(this.user.getId(), true);
		this.tdao.deleteTherapy(therapy.getId());
		
		TherapyNotificationCreator tnc = new TherapyNotificationCreator(therapy, this.user);
		tnc.deleteNotification();
	}
	
	public ArrayList<String> getCronTherapyDescriptions(){
		Therapy therapy = this.tdao.getLastVisitedTherapy(this.user.getId(), true);
		System.out.println("---DEBUG:" + therapy);
		return getCronTherapyDescriptions(therapy);
	}
	
	public ArrayList<String> getCronTherapyDescriptions(Therapy therapy){
		ArrayList<String> italianCrons = new ArrayList<String>();
		for(String timeStr : therapy.getHours()){
			String[] time = timeStr.trim().split(":");
			int hour = Integer.parseInt(time[0]);
			int min = Integer.parseInt(time[1]);
			
			String cron = min + " " + hour + " ";
			switch(therapy.getType()){
				case EVERY_DAY:
					cron += "* * *";
					break;
				case ODD_DAY:
					cron += "1-31/2 * *";
					break;
				case INTERVAL:
					cron += "1-31/2 * *";
					break;
				case SOME_DAY:
					ArrayList<Integer> daysIds = TherapyDay.getDaysIdx(therapy.getDays());
					cron += "* * " + daysIdsToString(daysIds);
					break;
			}
			
			String cronDesc = null;
			try {
				Options opt = new Options();
				opt.setTwentyFourHourTime(true);
				cronDesc = CronExpressionDescriptor.getDescription(cron, opt, Locale.ITALIAN);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(therapy.getType() == TherapyType.EVERY_DAY)
				cronDesc += " ogni giorno";
			if(therapy.getType() == TherapyType.INTERVAL)
				cronDesc = cronDesc.replace("ogni 2 giorni, , compreso tra i giorni 1 e 31 del mese", "ogni " + therapy.getIntervalDays()+ " giorni");
			if(therapy.getType() == TherapyType.ODD_DAY)
				cronDesc = cronDesc.replace(", , compreso tra i giorni 1 e 31 del mese", "");
			italianCrons.add(cronDesc);
		}
			
		return italianCrons;
	}
	
	private String daysIdsToString(ArrayList<Integer> daysIds){
		StringBuilder sb = new StringBuilder();
		for(int dayId : daysIds){
			sb.append(dayId + ",");
		}
		String str = sb.toString().trim();
		return str.substring(0, str.length()-1);
	}
}
