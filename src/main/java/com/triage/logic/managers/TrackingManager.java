package com.triage.logic.managers;

import java.io.InputStream;
import java.util.*;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.triage.logic.*;
import com.triage.logic.customexceptions.TrackingException;
import com.triage.push_notification.PushNotifications;
import com.triage.rest.dao.TrackingDao;
import com.triage.rest.enummodels.Status;
import com.triage.rest.models.messages.OCRExam;
import com.triage.rest.models.messages.OCRText;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.users.*;
import com.triage.utils.NLPUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.joda.time.LocalDate;
import org.quartz.SchedulerException;

public class TrackingManager {
	private User user;
	private TrackingDao tdao;
	public TrackingManager(User user){
		this.user = user;
		this.tdao = new TrackingDao();
	}

	//REMINDERS
	/**
	 * leggo l'ultimo reminder visitato
	 */
	public ExamReminder getLastVisitedReminder(){
		return this.tdao.getLastVisitedReminder(this.user.getId());
	}
	/**
	 * Elimino l'ultima notifica selezionata
	 */
	public void deleteLastVisitedReminder(){
		ExamReminder examreminder=this.tdao.deleteLastVisitedReminder(this.user.getId());
		TrackingNotificationCreator tkc = new TrackingNotificationCreator(examreminder, this.user,new LocalDate(examreminder.getDate()));
		tkc.deleteNotification();
	}
	/**
	 * Aggiorna la data dell'ultimo reminder
	 */
	public ExamReminder updateLastVisitedReminderDate(Date date) throws SchedulerException{
		return this.tdao.updateLastVisitedReminderDate(this.user.getId(),date);
	}
	/**
	 * Aggiorna l'ora dell'ultimo reminder
	 */
	public void updateLastVisitedReminderHour(String hour) throws SchedulerException{
			this.tdao.updateLastVisitedReminderHour(this.user.getId(), hour);
	}
	/**
	 * recupera i dettagli di un reminder associato a un esame
	 */
	public ExamReminder getReminderDetails(String examname){
		return this.tdao.getReminderDetails(this.user.getId(),examname);
	}
    /**
     * Mostra tutti i reminders attivi
     */
    public ArrayList<ExamReminder> seeAllExamsReminders(){
        return this.tdao.seeAllExamsReminders(this.user.getId());
    }

	/**
	 * remove reminder
	 */
	public void removeReminder(ExamReminder examreminder) throws SchedulerException {
		//Start quartz job
		try {
			TrackingNotificationCreator tkc = new TrackingNotificationCreator(examreminder, this.user, new LocalDate(examreminder.getDate()));
			tkc.deleteNotification();
		}
		catch (Exception e){
		}
	}
	/**
	 * crea un nuovo trigger per il Quartz Job.
	 */
	public void addReminder(ExamReminder oldexamreminder,ExamReminder newexamreminder) throws SchedulerException {
		//Start quartz job
		removeReminder(oldexamreminder);
	 	TrackingNotificationCreator tkc = new TrackingNotificationCreator(newexamreminder, this.user,new LocalDate(newexamreminder.getDate()));
		tkc.createNotification();
	}
	/**
	 * crea un nuovo reminder nel db
	 */
	public boolean  newReminder(ExamReminder examReminder){
		return this.tdao.addReminder(examReminder);
	}
	//END REMINDERS

	//CHARTS

	/**
	 *  Salva la data di interesse da parte dell'utente per la generazione del grafico
	 */
	public void setInitialPeriod(Date initialPeriod){
		this.tdao.setInitialPeriod(this.user.getId(),initialPeriod);
	}
	/**
	 *  Salva la data di interesse da parte dell'utente per la generazione del grafico
	 */
	public void setFinalPeriod(Date finalPeriod){
		this.tdao.setFinalPeriod(this.user.getId(),finalPeriod);
	}
	/**
	 * ritrova il periodo iniziale di interess
	 * @return
	 */
	public Date getRequiredInitialPeriod(){
		return this.tdao.getRequiredInitialPeriod(this.user.getId());
	}
	/**
	 * ritrova il periodo finale  di interesse
	 * @return
	 */
	public Date getRequiredFinalPeriod(){
		return this.tdao.getRequiredFinalPeriod(this.user.getId());
	}
	/**
	 * Salva il nome l'esame di interesse da parte dell'utente per la generazione del grafico
	 */
	public void setRequiredExam(String exam){
		this.tdao.setRequiredExam(this.user.getId(),exam);
	}
	/**
	* Recupera il nome l'esame di interesse da parte dell'utente per la generazione del grafico
	 */
 	public String getRequiredExam(){
		return this.tdao.getRequiredExam(this.user.getId());
	}
	/**
	 * Recupera i nomi degli esami presenti nel database
	 */
	public Set<String> getExams(){
		Set<String> exams = this.tdao.getExams();
		if(exams.size() >0)
			return exams;
		else
			return null;
	}

	/**
	 * l'utente aggiunge un nuovo esame riferita a un referto
	 */
	public void addNewExam(String name){
		this.tdao.addNewExam(user.getId(),name);
	}

	/**
	 * recuperto tutti gli esami attivi di un utente
	 */
	public Set<String> getExamsByUser(){
		Set<String> exams = this.tdao.getExamsByUser(this.user.getId());
		if(exams.size() >0)
			return exams;
		else
			return null;
	}
	/**
	 * Recupera gli esami in base al nome specificato dall'utente e in base all'anno.
	 */
	public HashMap<Integer,ArrayList<Exam>> getExamsByName(String name,Date initial_period,Date final_period){
		HashMap<Integer,ArrayList<Exam>> exams = this.tdao.getExamsByName(this.user.getId(),name,initial_period,final_period);
		if(exams.size() >0)
			return exams;
		else
			return null;
	}

	/**
	 * recupero ultimo esame con cui l'utente stava interagendo
	 */
	public Exam getLastVisitedExamByLastVisitedImage(){
		return this.tdao.getLastVisitedExamByLastVisitedImage(this.user.getId());
	}
	/**
	 * Crea un line chart per mostrare i dati relativi gli esami ricevuti come input
	 */
	public void generateChart(int chatid,ArrayList<Exam> exams){
		new OCRChart(chatid,exams);
	}
	//END CHARTS

	//ESAMI ESTRATTI DAI REFERTI


	/**
	 * Aggiorna il nome dell'ultimo esame visitato dall'utente
	 */
	public boolean updateLastVisitedExamByImage(Exam exam) {
		return this.tdao.updateLastVisitedExamByImage(exam);
	}
	/**
	 * Aggiorna il nome dell'ultimo esame visitato dall'utente
	 */
	public boolean updateLastViewedExamName(String name) {
		return this.tdao.updateLastViewedExamName(this.user.getId(),name);
	}
	/**
	 * Aggiorna l'unità dell'ultimo esame visitato dall'utente
	 */
	public boolean updateLastViewedExamUnit(String unit) {
		return this.tdao.updateLastViewedExamUnit(this.user.getId(),unit);
	}
	/**
	 * Aggiorna il minimo dell'ultimo esame visitato dall'utente
	 */
	public String updateLastViewedExamMin(double min) {
		try {
			Exam exam = this.tdao.getLastVisitedExamByLastVisitedImage(this.user.getId());
			if (exam.getMax()==0.0 || min < exam.getMax()) {
				if (this.tdao.updateLastViewedExamMin(this.user.getId(), min))
					return "";
				else
					return "Esiste già un esame con questo nome,range e unità di misura.\n";
			} else
				return "Il minimo è superiore al massimo.\n";
		}catch(NullPointerException np){
			this.tdao.updateLastViewedExamMin(this.user.getId(), min);
			return "";
		}
	}
	/**
	 * Aggiorna il minimo dell'ultimo esame visitato dall'utente
	 */
	public String updateLastViewedExamMax(double max) {
		Exam exam = this.tdao.getLastVisitedExamByLastVisitedImage(this.user.getId());
		if(max==0.0 || max> exam.getMin()){
			if(this.tdao.updateLastViewedExamMax(this.user.getId(), max))
				return "";
			else
				return "Esiste già un esame con questo nome,range e unità di misura.\n";
		}
		else
			return "Il massimo è inferiore al minimo.\n";
	}
	/**
	 * Aggiorna il risultato dell'ultimo esame visitato dall'utente
	 */
	public String updateLastViewedExamResult(double result) {
		if(this.tdao.updateLastViewedExamResult(this.user.getId(),result))
			return "";
		else
			return "Esiste già un esame con questo nome,range e unità di misura.\n";
	}
	//END ESAMI ESTRATTI DAI REFERTI

	//TRACKING
	/**
	 * Aggiorna la data dell'ultimo monitoraggio inserito
	 */
	public void updateLastTrackingDate(Date date){
		this.tdao.updateLastTrackingDate(this.user.getId(), date);
	}
	/**
	 * gets the last visited tracking
	 */
	public Tracking_image getLastVisitedTrackingImage(){
		return tdao.getLastVisitedTrackingImage(this.user.getId());
	}
	/**
	 * gets the last visited tracking*
	 */
	public Tracking getLastVisitedTracking(){
		return tdao.getLastVisitedTracking(this.user.getId(),true);
	}
	/**
	 *
	 */
	public ArrayList<Exam> showImageDetails(int num_image){
		Tracking t = getLastVisitedTracking();
		Tracking_image timg= t.getImages().get(num_image-1);
		tdao.updateLastVisitedImage(timg.getId());
		//query su tutti gli esami associati all'immagine per quel tracking
		return getExamsByImageId(timg.getId());
	}

	/**
	 * recupera tutti gli esami di un referto
	 * @param imageid
	 * @return
	 */
	public ArrayList<Exam> getExamsByImageId(int imageid){
		return tdao.getExamsByImageId(imageid);
	}
	/**
	 * Aggiunge una nuova immagine associata al Tracking.
	 * Inoltre, invia l'immagine al server ocr per farla processare ed estrarre le informazioni
	 */
	public void addSingleImage(final String imagelink,final InputStream inputStream, final String fileName,final int userid,final boolean last_created){
		final TrackingDao tdao= new TrackingDao();
		final int trID;
		if (last_created)
			trID=tdao.getLastCreatedTrackingID(userid);
		else
			trID = tdao.getLastVisitedTrackingID(userid);
		final int photo_id= tdao.addTrackingImage(trID, imagelink);
		final OCRApi ocra = new OCRApi();
		ocra.writeToFile(inputStream, ocra.getPath_image() + fileName);

		new Thread(new Runnable() {
			public void run() {
				if(inputStream != null && fileName != null) {
					System.out.println("Sending image to OCR: " + fileName);
					try {
						ClientResponse clientResponse = ocra.send_file(fileName, photo_id, userid, trID);
						if(clientResponse.getStatus() != 200){
							//String message = "";
							tdao.updateTrackingImageStatus(photo_id, Status.fallito);
							/*Response resp = new Response(userid, message);
							new PushNotifications().sendResponseObject(resp);*/
						}
					}
					catch(ClientHandlerException e){
						String message = "Attenzione: c'è stato un problema con l'immagine inviata. ";
						tdao.updateTrackingImageStatus(photo_id, Status.fallito);
						Response resp = new Response(userid, message);
						new PushNotifications().sendResponseObject(resp);
					}

				}
			}
		}).start();
		//return true;
	}

	/** ultimo tracking creato*/
	 public Tracking getLastCreatedTracking(){
		return tdao.getLastCreatedTracking(this.user.getId(),true);
	 }

	/**
	 * Restituisce tutte i monitoraggi di uno specifico utente. 
	 * Se non ce ne sono restituisce null.
	 */
	public ArrayList<Tracking> seeAllTrackings(){
		ArrayList<Tracking> trackings = this.tdao.getAllTrackings(this.user.getId());
		
		if(trackings.size() >0)
			return trackings;
		else
			return null;
	}
	/**
	 * Controlla se il nome è già in uso per un monitoraggio
	 */
	public boolean repeatedTrackingName(String trackingName){
		return this.tdao.repeatedTrackingName(this.user.getId(), trackingName);
	}
	/**
	 * Aggiunge un nuovo monitoraggio. Inserisce solamente il nome.
	 */
	public void addNewTracking(String trackingName){
		this.tdao.addNewTracking(this.user.getId(), trackingName);
	}
	/**
	 * Restituisce un monitoraggio utilizzando il nome. Prima di aggiornare o eliminare un 
	 * monitoraggio bisogna visualizzarla. Utilizzo di last_visited per individuare su quale
	 * terapia effettuare cambiamenti.
	 */
	public Tracking seeTrackingDetails(String trackingName){
		Tracking tracking = this.tdao.getTracking(this.user.getId(), trackingName);
		
		if(tracking != null){
			return tracking;
		}else{
			return null;
		}
	}
	/**
	 * Aggiorna il nome di un monitoraggio
	 */
	public void updateName(String trackingName){
		int trID = this.tdao.getLastVisitedTrackingID(this.user.getId());
		this.tdao.updateTrackingName(trID, trackingName);
	}
	/**
	 * Aggiorna il nome di un monitoraggio
	 */
	public void updateDate(Date trackingDate){
		int trID = this.tdao.getLastVisitedTrackingID(this.user.getId());
		this.tdao.updateTrackingDate(trID, trackingDate);
	}
	/**
	 * Elimina un monitoraggio
	 */
	public void deleteTracking(){
		int trID = this.tdao.getLastVisitedTrackingID(this.user.getId());
		this.tdao.deleteTracking(trID);
	}
	//END TRACKING

	//RICERCA TESTO NEI TRACKING
	/**
	 * Cerca nell'indice Lucene in cui è presente l'OCR delle immagini
	 */
	public HashMap<Tracking, String> searchOCRImage(String text) {
		ArrayList<String> imagesID = OCRLucene.getInstance().search(text, this.user.getId());

		HashMap<Tracking, String> trackingImage = new HashMap<Tracking, String>();
		if (imagesID != null) {
			for (String imageID : imagesID) {
				Tracking t = this.tdao.getTrackingByImageID(this.user.getId(), Integer.parseInt(imageID));
				if (t != null) {
					String imageLink = this.tdao.getImageByImageId(this.user.getId(), Integer.parseInt(imageID));
					trackingImage.put(t, imageLink);
				}
			}
			return trackingImage;
		}

		return null;
	}
	//END RICERCA TESTO NEI TRACKING
}
