package com.triage.logic.response_producer;

import java.io.InputStream;
import java.util.*;

import com.triage.rest.dao.MessageDao;
import com.triage.rest.dao.UserDao;
import com.triage.rest.enummodels.RangeLimit;
import com.triage.rest.enummodels.Status;
import com.triage.rest.models.users.*;
import com.triage.utils.JsonUtils;
import com.triage.utils.NLPUtils;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang.time.DateUtils;
import org.apache.velocity.VelocityContext;

import com.triage.logic.managers.TrackingManager;
import com.triage.logic.questions.TrackingQuestions;
import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import org.quartz.SchedulerException;

public class ResponseProducerTracking extends ResponseProducerAbstract {

	private TrackingManager tmanager;
	private InputStream uploadedInputStream;
	private String fileName;

	public ResponseProducerTracking(RestRequestInput restRequest, User user, Question lastq,
									InputStream uploadedInputStream, String fileName) {
		super(restRequest, user, lastq);
		this.uploadedInputStream = uploadedInputStream;
		this.fileName = fileName;
		this.setQstdao(new TrackingQuestions());
		this.tmanager = new TrackingManager(this.getUser());
	}

	@Override
	public Response produceResponse() {
		Question nextq = null;
		String lastQuestionName = this.getLastq().getQuestionName();

		//Bot: mostra il menu
		if (lastQuestionName.equals(TrackingQuestions.MENU)) {
			//this.text è una sezione del menu
			if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())) {
				nextq=viewMonitorings(nextq);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				//this.text è 'Aggiungi monitoraggio'
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_NAME);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(2).getText())) {
				//this.text è 'Ricerca nei monitoraggi'
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SEARCH);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(3).getText())) {
				//this.text è 'Visualizza grafico'
				ArrayList<Tracking> trackings = this.tmanager.seeAllTrackings();
				if (trackings != null) {
					nextq = chart_request();
				} else { //Nessun monitoraggio inserito
					nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
					nextq.setPreText("Inserisci prima un monitoraggio.\n");
				}
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(4).getText())) {
				//this.text è 'Visualizza reminders'
				nextq = viewReminders(nextq);
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
				nextq.setPreText("Voce del menu non esistente.\n");
			}
		}


		//Bot: Visualizza grafico: selezionare il range di interesse
		if (lastQuestionName.equals(TrackingQuestions.CHART_RANGE)) {
			String exam = this.tmanager.getRequiredExam();
			Date initial_period = this.tmanager.getRequiredInitialPeriod();
			Date final_period = this.tmanager.getRequiredFinalPeriod();
			HashMap<Integer, ArrayList<Exam>> exams = this.tmanager.getExamsByName(exam, initial_period, final_period);
			try {
				nextq = generateChart(exams.get(Integer.parseInt(this.getText()) - 1));
			} catch (NumberFormatException e) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_RANGE);
				nextq = buildSeeAllQuestion_ranges(nextq, exams);
				nextq.setPreText("Non hai selezionato un range valido.\n\n");
			}
		}
		//Bot: Visualizza grafico: selezionare l'esame ricercato
		if (lastQuestionName.equals(TrackingQuestions.EXAM_DISAMBIGUATION)) {
			String text = new JsonUtils().get_semantic(this.getText());
			this.tmanager.setRequiredExam(text);
			nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_ALL_DATA);
		}
		//Bot: vuoi visualizzare tutti i dati sul grafico?
		if (lastQuestionName.equals(TrackingQuestions.CHART_ALL_DATA)) {
			if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				//tutti i dati
				this.tmanager.setInitialPeriod(null);
				String exam = this.tmanager.getRequiredExam();
				nextq = PregenerateChart(exam, null, null);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())) {
				//specifica intervallo temporale
				nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_PERIOD);
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_ALL_DATA);
				nextq.setPreText("Non ho capito.\n");
			}
		}
		//Bot: vuoi visualizzare altro grafico
		if (lastQuestionName.equals(TrackingQuestions.CHART_REPLY)) {
			if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				nextq = chart_request();
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_REPLY);
				nextq.setPreText("non ho capito.\n");
			}
		}
		//Bot: Visualizza grafico - crea il grafico per il periodo di interesse
		if (lastQuestionName.equals(TrackingQuestions.CHART_PERIOD)) {
			try {
				Date initial_period = NLPUtils.parseDate(this.getText().split("-")[0]);
				Date final_period = new Date(); //today
				try {
					final_period = NLPUtils.parseDate(this.getText().split("-")[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
				}
				if (initial_period != null && final_period != null && !final_period.before(initial_period)) {
					this.tmanager.setInitialPeriod(initial_period);
					this.tmanager.setFinalPeriod(final_period);
					String exam = this.tmanager.getRequiredExam();
					nextq = PregenerateChart(exam, initial_period, final_period);
				} else if (initial_period != null && final_period != null && final_period.before(initial_period)) {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_PERIOD);
					nextq.setPreText("La data finale è precedente alla iniziale.\n");
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_PERIOD);
					nextq.setPreText("Non ho capito.\n");
				}
			} catch (Exception e) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_PERIOD);
				nextq.setPreText("Non ho capito.\n");
			}
		}

		//Bot: Visualizza grafico: Funzione principale
		if (lastQuestionName.equals(TrackingQuestions.CHART_REQUEST)) {
			/*ArrayList<JSONExam> exams_json= new JsonUtils().read_json_exams();
			ArrayList<String> exams_levenshtein = NLPUtils.filterByLevenshteinDistance_exam(exams_json, this.getText() );
			if (exams_levenshtein!=null && !exams_levenshtein.isEmpty()) {
				if(exams_levenshtein.size()>1) {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.EXAM_DISAMBIGUATION);
					nextq = buildSeeAllQuestion_exams(nextq, exams_levenshtein);
				}
				else{
					//non c'è ambiguità
					this.tmanager.setRequiredExam(new JsonUtils().get_semantic(exams_levenshtein.get(0)));
					nextq= this.getQstdao().getQuestion(TrackingQuestions.CHART_ALL_DATA);
				}
			} else {
				nextq=chart_request();
				if (nextq.getPreText()==null)
					nextq.setPreText("Il nome inserito non si riferisce ad alcun esame.");
			}*/
			Set<String> exams = this.tmanager.getExamsByUser();
			if (exams.contains(this.getText())) {
				this.tmanager.setRequiredExam(this.getText());
				nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_ALL_DATA);
			} else {
				nextq = chart_request();
				if (nextq.getPreText() == null)
					nextq.setPreText("Il nome inserito non si riferisce ad alcun esame.\n");
			}
		}


		//Bot: aggiungi il nome del nuovo monitoraggio
		if (lastQuestionName.equals(TrackingQuestions.ADD_NAME)) {
			if(!this.getText().isEmpty()) {
				boolean repeated = this.tmanager.repeatedTrackingName(this.getText());
				if (!repeated) {
					this.tmanager.addNewTracking(this.getText());
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_DATE);
				} else {
					//this.text è il nome già utilizzato da un altra terapia
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_NAME);
					nextq.setPreText("Esiste già un monitoraggio con questo nome.\n");
				}
			}
			else{
				//this.text è il nome già utilizzato da un altra terapia
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_NAME);
				nextq.setPreText("Non ho capito.\n");
			}
		}
		//bot: aggiungi la data del nuovo monitoraggio
		if (lastQuestionName.equals(TrackingQuestions.ADD_DATE)) {
			Date date = NLPUtils.parseDate(this.getText().trim().replace(" ", ""));
			if (date != null) {
				this.tmanager.updateLastTrackingDate(NLPUtils.parseDate(NLPUtils.adjustDateFormat(date)));
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_IMAGE_NEW_MONITORING);
				nextq.setText("");
				nextq.setPreText(EmojiParser.parseToUnicode("Premi :paperclip: e scatta una fotografia al tuo referto.\n\nLa qualita' dell'OCR dipende dalla qualita' dell'immagine; \n\n" +
						"Ecco alcuni suggerimenti per ottenere buoni risultati:\n" +
						"*Inquadratura*: Inquadrare il documento in modo che riempia tutto lo schermo\n" +
						"*Messa a fuoco*: Se l'immagine ti sembra sfocata, tocca lo schermo per la messa a fuoco.\n" +
						"*Luminosita*': più l'ambiente in cui ti torvi è luminoso, più accurata sarà l'estrazione delle informazioni.\n" +
						"*Contrasto*: un forte contrasto tra il testo e lo sfondo potrebbe aiutare nel riconoscimento.\n" +
						"*Nota importante:\nNon è possibile elaborare testi scritti a mano.*\n"));
				/*nextq.setPreText("Per procedere, è necessario scansionare tutte le immagini del referto utilizzando l'applicazione della fotocamera del tuo dispositivo, quindi uscendo temporaneamente da Telegram.\n\n" +
						"Suggerimenti per garantire un buon riconoscimento del contenuto delle immagini:\n" +
						"- Assicurarsi che il referto abbia le colonne identificate da un nome (e.g Nome esame, Unità di misura)\n" +
						"- Appoggiare il referto su un piano in modo da *non creare delle ondulazioni* del foglio\n" +
						"- Fotografare il referto in *forma cartacea* (*non fotografare un monitor*)\n" +
						"- Evitare di creare delle *ombre* sul referto (*Non utilizzare il flash*)\n" +
						"- Assicurarsi che la fotografia che si sta inviando *non risulti ruotata*\n\n" +
						"Successivamente, torna su Telegram,  ");*/
			} else {
				//this.text non è valida come data
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_DATE);
				nextq.setPreText("Il formato della data non è valido. \n(Suggerimento: gg/mm/yyyy).\n");
			}
		}

		//Bot: aggiungi un immagine per un  monitoraggio già esistente
		if (lastQuestionName.equals(TrackingQuestions.ADD_IMAGE_OLD_MONITORING)) {
			nextq = addImage(nextq, false);
		}
		//Bot: aggiungi un immagine per il nuovo monitoraggio
		if (lastQuestionName.equals(TrackingQuestions.ADD_IMAGE_NEW_MONITORING)) {
			//String msgusr= new MessageDao().getLastKUserMessage(this.getUser().getId(),2);
			nextq = addImage(nextq, true);
		}
		//Bot: Monitoraggi inseriti: .... Scegli una monitoraggio per osservarne i dettagli
		if (lastQuestionName.equals(TrackingQuestions.SEE_ALL)) {
			//this.tmanager.checkNewResults();
			//this.text è il nome di un monitoraggio da visualizzare
			//if(!this.getText().equalsIgnoreCase("Indietro")) {
				Tracking tracking = this.tmanager.seeTrackingDetails(this.getText());

				if (tracking == null) {
					ArrayList<Tracking> trackings = this.tmanager.seeAllTrackings();

					if (trackings != null) {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_ALL);
						nextq = buildSeeAllQuestion(nextq, trackings);
						nextq.setPreText("Nessun monitoraggio col nome \"*" + this.getText() + "*\".\n");
					} else { //Nessun monitoraggio inserito
						nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
						nextq.setPreText("Nessun monitoraggio inserito.");
					}
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_ONE);
					nextq = buildSeeOneQuestion(nextq, tracking, this.getUser().getId());
				}
			/*}
			else{
				nextq=viewMonitorings(nextq);
			}*/
		}

		//bot: aggiorna data reminder
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_REMINDER_DATE)) {
			Date date = NLPUtils.parseDate(this.getText().trim().replace(" ", ""));
			if (date!=null)
				date=NLPUtils.parseDate(NLPUtils.adjustDateFormat(date));
			if (date != null && !DateUtils.isSameDay(date, new Date()) && !date.before(new Date())) {
				try {
					ExamReminder oldexamReminder = this.tmanager.getLastVisitedReminder();
					ExamReminder newexamReminder = this.tmanager.getLastVisitedReminder();
					newexamReminder.setDate(date);
					this.tmanager.addReminder(oldexamReminder, newexamReminder);
					this.tmanager.updateLastVisitedReminderDate(date);
					nextq = viewReminders(nextq);
					nextq.setPreText("Aggiornamento effettuato.\n");
				} catch (SchedulerException e) {
					/*nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_REMINDER_DATE);
					if (DateUtils.isSameDay(date, new Date())) {
						nextq.setPreText("La data inserita coincide con quella odierna.\n");
					} else {
						nextq.setPreText("La data inserita è passata.\n");
					}*/
				}
			} else if (date != null && DateUtils.isSameDay(date, new Date())) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_REMINDER_DATE);
				nextq.setPreText("La data inserita coincide con quella odierna.\n");
			} else if (date != null && date.before(new Date())) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_REMINDER_DATE);
				nextq.setPreText("La data inserita è passata.\n");
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_REMINDER_DATE);
				nextq.setPreText("Il formato della data non è valido.\n(Suggerimento: gg/mm/yyyy).\n");
			}
		}
		//bot: aggiorna ora reminder
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_REMINDER_HOUR)) {
			Date hourDate = NLPUtils.parseHour(this.getText());
			if (hourDate != null) {
				try {
					ExamReminder oldexamReminder = this.tmanager.getLastVisitedReminder();
					if(oldexamReminder.getisPassed()){
						nextq = viewReminders(nextq);
						nextq.setPreText("non puoi modificare l'ora di una notifica scaduta. Modifica prima la data.\n");
					}
					else {
						ExamReminder newexamReminder = this.tmanager.getLastVisitedReminder();
						newexamReminder.setHour(this.getText());
						this.tmanager.addReminder(oldexamReminder, newexamReminder);
						this.tmanager.updateLastVisitedReminderHour(this.getText());
						nextq = viewReminders(nextq);
						nextq.setPreText("Aggiornamento effettuato.\n");
					}
				} catch (SchedulerException e) {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_REMINDER_HOUR);
					nextq.setPreText("L'orario è superato oppure è troppo vicino a quello attuale.\n");
				}
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_REMINDER_HOUR);
				nextq.setPreText("Il formato dell'orario non è valido.\n");
			}
		}

		//bot: richiesta aggiornamento dettagli reminder
		if (lastQuestionName.equals(TrackingQuestions.SEE_REMINDER_DETAILS) || lastQuestionName.equals(TrackingQuestions.SEE_REMINDER_DETAILS_ERROR)) {
			//todo: il text dovrebbe coincidere col nome di uno dei possibili esami come al solito
			if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				//aggiorna data
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_REMINDER_DATE);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())) {
				//aggiorna hour
				ExamReminder oldexamReminder = this.tmanager.getLastVisitedReminder();
				if(oldexamReminder.getisPassed()){
					nextq = viewReminders(nextq);
					nextq.setPreText("Non puoi modificare l'ora di una notifica scaduta. Aggiorna la data prima.\n");
				}
				else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_REMINDER_HOUR);
				}
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(2).getText())) {
				//elimina notifica
				this.tmanager.deleteLastVisitedReminder();
				nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
				nextq.setPreText("Notifica eliminata.\n");
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(3).getText())) {
				//back
				nextq = viewReminders(nextq);
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_REMINDER_DETAILS_ERROR);
				nextq.setPreText("Non ho capito.\n");
			}
		}
		//bot: seleziona reminder
		if (lastQuestionName.equals(TrackingQuestions.SEE_REMINDERS)) {
			ExamReminder examreminder = this.tmanager.getReminderDetails(this.getText());
			if (examreminder != null) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_REMINDER_DETAILS);
				nextq = buildSeeOneReminderQuestion(nextq, examreminder);
			} else if (this.getText().equalsIgnoreCase(this.getQstdao().getQuestion(TrackingQuestions.NO_EXAM_REMINDERS).getAnswers().get(0).getText())) {
				//aggiungi notifica
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_NAME);
			} else {
				nextq = viewReminders(nextq);
				nextq.setPreText("Il nome inserito non corrisponde ad alcun esame nell'elenco.\n");
			}
		}
		//bot: aggiungi data reminder
		if (lastQuestionName.equals(TrackingQuestions.ADD_EXAM_REMINDER_HOUR)) {
			Date hourDate = NLPUtils.parseHour(this.getText());
			if (hourDate != null) {
				try {
					ExamReminder oldexamReminder = this.tmanager.getLastVisitedReminder();
					ExamReminder newexamReminder = this.tmanager.getLastVisitedReminder();
					newexamReminder.setHour(this.getText());
					this.tmanager.addReminder(oldexamReminder, newexamReminder);
					this.tmanager.updateLastVisitedReminderHour(this.getText());
					nextq = viewReminders(nextq);
					nextq.setPreText("Notifica aggiunta.\n");
				} catch (SchedulerException e) {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_HOUR);
					nextq.setPreText("L'orario è superato oppure è troppo vicino a quello attuale.\n");
				}
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_HOUR);
				nextq.setPreText("Il formato dell'orario non è valido.\n");
			}
		}
		//bot: aggiungi data reminder
		if (lastQuestionName.equals(TrackingQuestions.ADD_EXAM_REMINDER_DATE)) {
			//aggiungi nuovo reminder
			Date date = NLPUtils.parseDate(this.getText().trim().replace(" ", ""));
			if (date!=null)
				date=NLPUtils.parseDate(NLPUtils.adjustDateFormat(date));
			if (date != null && !DateUtils.isSameDay(date, new Date()) && !date.before(new Date())) {
				try {
					ExamReminder examReminder = this.tmanager.getLastVisitedReminder();
					examReminder.setDate(date);
					this.tmanager.updateLastVisitedReminderDate(date);
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_HOUR);
				} catch (SchedulerException e) {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_DATE);
					nextq.setPreText("C'e' stato qualche problema con la data specificata.(Suggerimento: gg/mm/yyyy). \n");
				}
			} else if (date != null && DateUtils.isSameDay(date, new Date())) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_DATE);
				nextq.setPreText("La data inserita coincide con quella odierna.\n");
			} else if (date != null && date.before(new Date())) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_DATE);
				nextq.setPreText("La data inserita è passata.\n");
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_DATE);
				nextq.setPreText("Il formato della data non è valido.\n(Suggerimento: gg/mm/yyyy).\n");
			}
		}
		//bot: aggiungi nome reminder
		if (lastQuestionName.equals(TrackingQuestions.ADD_EXAM_REMINDER_NAME)) {
			//aggiungi nuovo reminder
			if (this.tmanager.newReminder(new ExamReminder(this.getUser().getId(), this.getText()))) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_DATE);
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_NAME);
				nextq.setPreText("Esiste già una notifica con questo nome.\n");
			}
		}
		//bot: aggiungi reminder
		if (lastQuestionName.equals(TrackingQuestions.NO_EXAM_REMINDERS)) {
			if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				//aggiungi nuovo reminder
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_REMINDER_NAME);
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.NO_EXAM_REMINDERS);
				nextq.setPreText("Non ho capito.\n");
			}
		}

		//bot: aggiornamento nome esame
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_EXAM_NAME)) {
			if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				if (!this.getText().isEmpty()) {
					if (tmanager.updateLastViewedExamName(this.getText())) {
						nextq = examUpdatedMessage(nextq, true);
					} else {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_NAME);
						nextq.setPreText("Esiste già un esame con questo nome,range e unità di misura, per questo monitoraggio.\n");
					}
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_NAME);
					nextq.setPreText("Non ho capito.\n");
				}
			} else {
				nextq = examUpdatedMessage(nextq, false);
			}
		}
		//bot: aggiornamento soglia minima esame
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_EXAM_MIN)) {
			if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				if (!this.getText().isEmpty()) {
					try {
						String text = this.getText().replace(",", ".");
						double value = Double.parseDouble(text);
						if (value >= 0) {
							String result = tmanager.updateLastViewedExamMin(value);
							if (result.isEmpty()) {
								nextq = examUpdatedMessage(nextq, true);
							} else {
								nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MIN);
								nextq.setPreText(result);
							}
						} else {
							nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MIN);
							nextq.setPreText("Il minimo non può essere negativo.\n");
						}
					} catch (NumberFormatException e) {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MIN);
						nextq.setPreText("Il valore inserito non è di tipo numerico.\n");
					}
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MIN);
					nextq.setPreText("Non ho capito.\n");
				}
			} else {
				nextq = examUpdatedMessage(nextq, false);
			}
		}

		//bot: aggiornamento soglia massima esame
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_EXAM_MAX)) {
			if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				if (!this.getText().isEmpty()) {
					try {
						String text = this.getText().replace(",", ".");
						double value = Double.parseDouble(text);
						String result = tmanager.updateLastViewedExamMax(value);
						if (value >= 0) {
							if (result.isEmpty()) {
								nextq = examUpdatedMessage(nextq, true);
							} else {
								nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MAX);
								nextq.setPreText(result);
							}
						} else {
							nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MAX);
							nextq.setPreText("Il massimo non può essere negativo.\n");
						}
					} catch (NumberFormatException e) {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MAX);
						nextq.setPreText("Il valore inserito non è di tipo numerico.\n");
					}
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MAX);
					nextq.setPreText("non ho capito.\n");
				}
			} else {
				nextq = examUpdatedMessage(nextq, false);
			}
		}
		//bot: aggiornamento risultato esame
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_EXAM_RESULT)) {
			if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				if (!this.getText().isEmpty()) {
					try {
						String text = this.getText().replace(",", ".");
						double value = Double.parseDouble(text);
						if (value >= 0) {
							String result = tmanager.updateLastViewedExamResult(value);
							if (result.isEmpty()) {
								nextq = examUpdatedMessage(nextq, true);
							} else {
								nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_RESULT);
								nextq.setPreText(result);
							}
						} else {
							nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_RESULT);
							nextq.setPreText("Il risultato non può essere negativo.\n");
						}
					} catch (NumberFormatException e) {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_RESULT);
						nextq.setPreText("Il valore inserito non è di tipo numerico.\n");
					}
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_RESULT);
					nextq.setPreText("Non ho capito.\n");
				}
			} else {
				nextq = examUpdatedMessage(nextq, false);
			}
		}
		//bot: aggiornamento unità di misura esame
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_EXAM_UNIT)) {
			if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				if (!this.getText().isEmpty()) {
					if (tmanager.updateLastViewedExamUnit(this.getText())) {
						nextq = examUpdatedMessage(nextq, true);
					} else {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_UNIT);
						nextq.setPreText("Esiste già un esame con questo nome,range e unità di misura, per questo monitoraggio.\n");
					}
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_UNIT);
					nextq.setPreText("Non ho capito.\n");
				}
			} else {
				nextq = examUpdatedMessage(nextq, false);
			}
		}

		//bot: aggiunge range all'esame
		if (lastQuestionName.equals(TrackingQuestions.ADD_EXAM_RANGE)) {
			if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				if (!this.getText().isEmpty()) {
					try {
						String text = this.getText().replace(",", ".");
						String[] words= text.split("-");
						double min = Double.parseDouble(words[0]);
						double max= Double.parseDouble(words[1]);

						if (min >= 0 && max >=0 && min<max) {
							tmanager.updateLastViewedExamMin(min);
							tmanager.updateLastViewedExamMax(max);
							nextq=backImageDetails(nextq,null);
							nextq.setPreText("Esame aggiunto.\n");
						} else {
							if(min<0){
								nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RANGE);
								nextq.setPreText("Il minimo non può essere negativo.\n");
							}
							else if(max<0){
								nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RANGE);
								nextq.setPreText("Il massimo non può essere negativo.\n");
							}
							else if(min> max){
								nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RANGE);
								nextq.setPreText("Il minimo non può essere maggiore del massimo.\n");
							}
							else if(min==max){
								nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RANGE);
								nextq.setPreText("Il minimo e il massimo coincidono.\n");
							}
						}
					} catch (NumberFormatException e) {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RANGE);
						nextq.setPreText("Alcuni valori inseriti non sono di tipo numerico.\n");
					}
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RANGE);
					nextq.setPreText("non ho capito.\n");
				}
			} else {
				nextq=backImageDetails(nextq,null);
			}
		}

		//bot: aggiungi unità di misura al nuovo esame
		if (lastQuestionName.equals(TrackingQuestions.ADD_EXAM_UNIT)) {
			if (!this.getText().isEmpty()) {
				if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {

						if (tmanager.updateLastViewedExamUnit(this.getText())) {
							nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RANGE);
						} else {
							nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_UNIT);
							nextq.setPreText("Esiste già un esame con questo nome,range e unità di misura, per questo monitoraggio.\n");
						}
				} else {
					//back
					nextq=backImageDetails(nextq,null);
				}
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_UNIT);
				nextq.setPreText("Non ho capito.\n");
			}
		}

		//bot: aggiungi risultato al nuovo esame
		if (lastQuestionName.equals(TrackingQuestions.ADD_EXAM_RESULT)) {
			if (!this.getText().isEmpty()) {
				if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
					try {
						String text = this.getText().replace(",", ".");
						double value = Double.parseDouble(text);
						if (value >= 0) {
							String result = tmanager.updateLastViewedExamResult(value);
							if (result.isEmpty()) {
								nextq= this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_UNIT);
							} else {
								nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RESULT);
								nextq.setPreText(result);
							}
						} else {
							nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RESULT);
							nextq.setPreText("Il risultato non può essere negativo.\n");
						}
					} catch (NumberFormatException e) {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RESULT);
						nextq.setPreText("Il valore inserito non è di tipo numerico.\n");
					}
				} else {
					//back
					nextq=backImageDetails(nextq,null);
				}
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RESULT);
				nextq.setPreText("Non ho capito.\n");
			}
		}

		//bot:aggiungi nome esame
		if (lastQuestionName.equals(TrackingQuestions.ADD_EXAM_NAME)) {
			if (!this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				if (!this.getText().isEmpty()) {
					this.tmanager.addNewExam(this.getText());
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_RESULT);
				} else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_NAME);
					nextq.setPreText("Non ho capito.\n");
				}
			} else {
				//back
				nextq=backImageDetails(nextq,null);
			}
		}

		//Bot: Modificare un esame
		if (lastQuestionName.equals(TrackingQuestions.SEE_EXAM_DETAILS) || lastQuestionName.equals(TrackingQuestions.SEE_EXAM_DETAILS_ERROR)) {
			if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				//update_exam_name
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_NAME);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())) {
				//update_exam_min
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MIN);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(2).getText())) {
				//update_exam_max
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_MAX);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(3).getText())) {
				//update_exam_result
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_RESULT);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(4).getText())) {
				//update_exam_unit
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_EXAM_UNIT);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(5).getText())) {
				//back
				nextq=backImageDetails(nextq,null);
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_EXAM_DETAILS_ERROR);
				nextq.setPreText("Non ho capito.\n");
			}
		}

		//Bot: Visualizzare dettagli di un esame estratto da un immagine
		if (lastQuestionName.equals(TrackingQuestions.SEE_IMAGE_DETAILS) || lastQuestionName.equals(TrackingQuestions.SEE_IMAGE_DETAILS_ERROR)) {
			if (!this.getText().equalsIgnoreCase("Indietro")) {
				if(!this.getText().equalsIgnoreCase("Aggiungi esame")) {
					try {
						Tracking_image trimage = this.tmanager.getLastVisitedTrackingImage();
						Tracking t = this.tmanager.getLastVisitedTracking();
						int i = 0;
						while (i < t.getImages().size()) {
							if (t.getImages().get(i).getId() == trimage.getId())
								break;
							i++;
						}
						ArrayList<Exam> exams = this.tmanager.showImageDetails(i + 1);
						Exam exam = exams.get(Integer.valueOf(this.getText()) - 1);
						this.tmanager.updateLastVisitedExamByImage(exam);
						//Exam exam=this.tmanager.getImageExam(last_visitedimage,exams.get(Integer.valueOf(this.getText())).getId());
						nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_EXAM_DETAILS);
						nextq = buildSeeExamDetailsQuestion(nextq, exam);
					} catch (Exception e) {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_IMAGE_DETAILS_ERROR);
						nextq.setPreText("Non ho capito.\n");
					}
				}
				else{
					nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_EXAM_NAME);
					//nextq.setPreText("Non ho capito.\n");
				}
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SELECT_TRACKING_IMAGE);
				Tracking t = this.tmanager.getLastVisitedTracking();
				int i = 1;
				for (Tracking_image tt : t.getImages()) {
					nextq.addSingleAnswer(new Answer(String.valueOf(i)));
					i++;
				}
				nextq.addSingleAnswer(new Answer("Indietro"));
			}
		}
		//Bot: Visualizzare dettagli di un immagine
		if (lastQuestionName.equals(TrackingQuestions.SELECT_TRACKING_IMAGE)) {
			if (!this.getText().equalsIgnoreCase("Indietro")){
				try {
					Tracking t = this.tmanager.getLastVisitedTracking();
					int i = 1;
					for (Tracking_image trimage : t.getImages()) {
						if (i==Integer.valueOf(this.getText())){
							nextq=backImageDetails(nextq,trimage);
							break;
						}
						i++;
					}

				} catch (Exception e) {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.SELECT_TRACKING_IMAGE);
					Tracking t = this.tmanager.getLastVisitedTracking();
					int i = 1;
					for (Tracking_image tt : t.getImages()) {
						nextq.addSingleAnswer(new Answer(String.valueOf(i)));
						i++;
					}
					nextq.addSingleAnswer(new Answer("Indietro"));
					nextq.setPreText("Non ho capito.\n");
				}
			}
			else{
				//back
				nextq= backTrackingDetails(nextq);

			}
		}
		//Bot: Dettaglio monitoraggio .... Scegli un azione da eseguire su questo monitoraggio
		if (lastQuestionName.equals(TrackingQuestions.SEE_ONE)) {
			//this.text è un operazione di modifica/eliminazione monitoraggio
			//o il nome di un nuovo monitoraggio da osservare
			if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())) {
				//aggiungi nuova immagine al monitoraggio
				nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_IMAGE_OLD_MONITORING);
				nextq.setText("");
				nextq.setPreText(EmojiParser.parseToUnicode("Premi :paperclip: e scatta una fotografia al tuo referto.\n\nLa qualita' dell'OCR dipende dalla qualita' dell'immagine; \n\n" +
						"Ecco alcuni suggerimenti per ottenere buoni risultati:\n" +
						"*Inquadratura*: Inquadrare il documento in modo che riempia tutto lo schermo\n" +
						"*Messa a fuoco*: Se l'immagine ti sembra sfocata, tocca lo schermo per la messa a fuoco.\n" +
						"*Luminosita*': più l'ambiente in cui ti torvi è luminoso, più accurata sarà l'estrazione delle informazioni.\n" +
						"*Contrasto*: un forte contrasto tra il testo e lo sfondo potrebbe aiutare nel riconoscimento.\n" +
						"**Nota importante:\nNon è possibile elaborare testi scritti a mano."));
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())) {
				//seleziona immagine di cui visualizzarne i dettagli
				//seleziona immagine
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SELECT_TRACKING_IMAGE);
				Tracking t = this.tmanager.getLastVisitedTracking();
				int i = 1;
				for (Tracking_image tt : t.getImages()) {
					nextq.addSingleAnswer(new Answer(String.valueOf(i)));
					i++;
				}
				nextq.addSingleAnswer(new Answer("Indietro"));
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(2).getText())) {
				//Modifica nome monitoraggio
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_NAME);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(3).getText())) {
				//modifica data monitoraggio
				nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_DATE);
			} else if (this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(4).getText())) {
				//Elimina monitoraggio
				this.tmanager.deleteTracking();
				ArrayList<Tracking> trackings = this.tmanager.seeAllTrackings();
				if (trackings != null) {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_ALL);
					nextq = buildSeeAllQuestion(nextq, trackings);
				} else { //Nessuna tracking inserito
					nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
				}
				nextq.setPreText("Monitoraggio eliminato con successo.\n");
			}else if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(5).getText())) {
				//back
				nextq=viewMonitorings(nextq);
			}
			else {
				//Il nome di una nuovo tracking da visualizzare
				Tracking tracking = this.tmanager.seeTrackingDetails(this.getText());
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_ONE);
				nextq = buildSeeOneQuestion(nextq, tracking, this.getUser().getId());
			}
		}

		//Bot: inserisci il nuovo nome per il monitoraggio
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_NAME)) {
			//this.text è il nuovo nome del monitoraggio
			boolean repeated = this.tmanager.repeatedTrackingName(this.getText());
			if(!this.getText().equalsIgnoreCase("Indietro")){
				if (!repeated) {
					this.tmanager.updateName(this.getText());
					nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
					nextq.setPreText("Aggiornamento effettuato.");
				} else {
					//this.text è il nome già utilizzato da un altro monitoraggio
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_NAME);
					nextq.setPreText("Il nome inserito è associato ad un altro monitoraggio.\n");
				}
			}
			else{
				//back
				nextq= backTrackingDetails(nextq);
			}
		}

		//Bot: inserisci la nuova data per il monitoraggio
		if (lastQuestionName.equals(TrackingQuestions.UPDATE_DATE)) {
			if(!this.getText().equalsIgnoreCase("Indietro")){
				Date date = NLPUtils.parseDate(this.getText().trim().replace(" ", ""));
				if (date != null) {
					this.tmanager.updateDate(NLPUtils.parseDate(NLPUtils.adjustDateFormat(date)));
					nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
					nextq.setPreText("Aggiornamento effettuato.\n");
				} else {
					//this.text non è valida come data
					nextq = this.getQstdao().getQuestion(TrackingQuestions.UPDATE_DATE);
					nextq.setPreText("Il formato della data non è valido.\n(Suggerimento: gg/mm/yyyy).\n");
				}
			}
			else{
				//back
				nextq= backTrackingDetails(nextq);
			}
		}

		//Bot: digita la parola da ricercare
		if (lastQuestionName.equals(TrackingQuestions.SEARCH) ||
				lastQuestionName.equals(TrackingQuestions.SEARCH_RESULT)) {
			//this.tmanager.checkNewResults();
			//this.text è il testo da ricercare nei risultati OCR
			HashMap<Tracking, String> trackingImage = this.tmanager.searchOCRImage(this.getText());

			if (trackingImage != null) {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SEARCH_RESULT);
				nextq = buildSearchResultQuestion(nextq, trackingImage);
				//nextq.setPreText("Risultato indidividuato in questo monitoraggio.");
			} else {
				nextq = this.getQstdao().getQuestion(TrackingQuestions.SEARCH);
				nextq.setPreText("Nessun risultato individuato.\n");
			}

		}

		if (nextq == null) {
			ArrayList<Tracking> trackings = this.tmanager.seeAllTrackings();
			nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
			if (trackings == null) {
				ArrayList<Answer> answs = new ArrayList<Answer>();
				answs.add(nextq.getAnswers().get(0));
				nextq.setAnswers(answs);
			}
		}
		this.setNextq(nextq);
		return createResponseObject();
	}


	/**
	 * Aggiung un'immagine a un monitoraggio già esistente oppure a uno nuovo
	 */
	private Question addImage(Question nextq, boolean last_created) {
		String answ="procedi";
		if (this.getLastq().getAnswers().size()>0)
			answ= this.getLastq().getAnswers().get(0).getText();
		if (!this.getText().equalsIgnoreCase(answ)) {
			if (!this.getText().toLowerCase().equalsIgnoreCase("fine invio immagini")) {
				if (this.getRestRequest().getPhotoId() != null) {
					this.tmanager.addSingleImage(this.getRestRequest().getPhotoId(), this.uploadedInputStream, this.fileName, this.getUser().getId(), last_created);
					//String msg = new MessageDao().getLastBotMessage(this.getUser().getId());
					Tracking tr = this.tmanager.getLastVisitedTracking();
					if (last_created)
						nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_IMAGE_NEW_MONITORING);
					else
						nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_IMAGE_OLD_MONITORING);
					int n_igs=tr.getImages().size();
					String imgs= "Un'immagine è stata ricevuta correttamente. Il monitoraggio adesso contiene "+ n_igs + " immagini di referti."+"\n";
					if (n_igs==1){
						imgs="Un'immagine è stata ricevuta correttamente. Il monitoraggio adesso contiene " + n_igs + " immagine di un referto.\n";
					}
					nextq.setPreText(imgs);
					nextq.addSingleAnswer(new Answer("fine invio immagini"));
					//nextq.setPreText("\nImmagine ricevuta correttamente.\nVuoi inviare altri allegati?\nSe si, ");
				} else {
					if (last_created)
						nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_IMAGE_NEW_MONITORING);
					else
						nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_IMAGE_OLD_MONITORING);
					nextq.setText(" ");
					nextq.setPreText(EmojiParser.parseToUnicode("Nessuna immagine ricevuta.\n Per inviare delle foto, premi :paperclip: e scatta la fotografia al tuo referto oppure seleziona delle foto scattate in precedenza.\n"));
					//nextq.addSingleAnswer(new Answer("fine invio immagini"));

				}
			} else {
				//controlla se non è un nuovo tracking altrimenti è obbligatorio inserire delle immagini
				if(last_created) {
					Tracking tr = this.tmanager.getLastCreatedTracking();
					if (tr.getImages().size() == 0) {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.ADD_IMAGE_NEW_MONITORING);
						nextq.setText(" ");
						nextq.setPreText(EmojiParser.parseToUnicode("Per procedere, è necessario inviare almeno un'immagine.\n Premi :paperclip: e scatta la fotografia al tuo referto oppure seleziona delle foto scattate in precedenza.\n"));
					} else {
						nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
						nextq.setPreText("" +
								"Stiamo analizzando le immagini inviate. Quando avremo finito, riceverai una notifica.\n");
					}
						//"Nella sezione <Visualizza monitoraggi>, dopo aver selezionato il monitoraggio, è possibile visionare lo stato di elaborazione di ogni immagine ad esso associata.\n");							}
				}
				else {
					nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
					nextq.setPreText("" +
						"Le foto inviate verranno presto elaborate al fine di estrarre le informazioni contenute.\n" +
						"Nella sezione <Visualizza monitoraggi>, dopo aver selezionato il monitoraggio, è possibile visionare lo stato di elaborazione di ogni immagine ad esso associata.\n");
				}
			}
			return nextq;
		}
		else{
			//back
			return backTrackingDetails(nextq);
		}
	}

	/**
	 * aggiornamenti esame
	 **/
	private Question examUpdatedMessage(Question nextq, boolean status) {
		Exam em = this.tmanager.getLastVisitedExamByLastVisitedImage();
		nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_EXAM_DETAILS);
		nextq = buildSeeExamDetailsQuestion(nextq, em);
		if (status)
			nextq.setPreText("Aggiornamento effettuato.\n");
		return nextq;
	}

	/**
	 * Richiesta di visualizzare un grafico di un esame: mostra la lista degli esami
	 *
	 * @return
	 */
	private Question chart_request() {
		Question nextq = null;
		Set<String> allExamsInDB = this.tmanager.getExamsByUser();
		if (allExamsInDB != null) {
			nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_REQUEST);
			for (String anw : allExamsInDB) {
				nextq.addSingleAnswer(new Answer(anw));
			}
		} else {
			nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
			nextq.setPreText("Non è stato estratto ancora alcun esame dai referti.\n");
		}
		return nextq;
	}

	/**passaggio intermedio per la generazione del grafico*/
	private Question PregenerateChart(String examname, Date initial_period, Date final_period) {
		//this.tmanager.checkNewResults();
		Question nextq = null;
		HashMap<Integer, ArrayList<Exam>> exams = this.tmanager.getExamsByName(examname, initial_period, final_period);
		if (exams != null) {
			if (exams.keySet().size() == 1) {
				nextq = generateChart(exams.get(0));
			} else if (exams.keySet().size() > 1) {
				//chiedere all'utente quale intervallo visualizzare
				nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_RANGE);
				nextq = buildSeeAllQuestion_ranges(nextq, exams);
			}
		} else {
			nextq = chart_request();
			nextq.setShowChart(false);
			if (nextq.getPreText() == null)
				nextq.setPreText("Non esistono valori estratti dai referti per questo esame,nel periodo specificato.\n ");
		}
		return nextq;
	}

	/**costruisce il grafico da visualizzare**/
	private Question generateChart(ArrayList<Exam> exams) {
		Question nextq = null;
		this.tmanager.generateChart(this.getChatId(), exams);
		nextq = this.getQstdao().getQuestion(TrackingQuestions.CHART_REPLY);
		nextq.setShowChart(true);
		return nextq;
	}

	//visualizza la lista dei monitoraggi
	private static Question buildSeeAllQuestion_exams(Question nextq, ArrayList<String> exams) {
		for (String s : exams) {
			//pulsanti
			Answer as = new Answer(s);
			nextq.addSingleAnswer(as);
		}
		VelocityContext vc = new VelocityContext();
		vc.put("OCRexamsData", exams);
		((QuestionTemplate) nextq).setVelocityContex(vc);
		return nextq;
	}

	//visualizza i ranges per un certo esame
	private static Question buildSeeAllQuestion_ranges(Question nextq, HashMap<Integer, ArrayList<Exam>> exams) {
		ArrayList<String> ranges = new ArrayList<String>();
		for (int range : exams.keySet()) {
			//recupero i tracking_names in modo univoco
			Set<String> tracking_names = new HashSet<String>();
			for (Exam ex : exams.get(range)) {
				tracking_names.add("    - " + ex.getTracking_name());
			}

			//pulsanti per le possibili risposte
			Answer as = new Answer(Integer.toString(range + 1));
			nextq.addSingleAnswer(as);

			//testo che mostra quali solo le possibili risposte
			int rangeView = range + 1;
			String trackings = "";
			for (String tr : tracking_names) {
				trackings += tr + "\n";
			}
			String max= "Infinito";
			if(exams.get(range).get(0).getMax()!=0.0){
				max=String.valueOf(exams.get(range).get(0).getMax());
			}
			ranges.add("*Intervallo*: " + exams.get(range).get(0).getMin()
					+ "-" + max + "\n    *Unità di misura*: "
					+ exams.get(range).get(0).getUnit() + "\n" +
					"    *Rilevato nei monitoraggi*: \n" + trackings);

		}

		VelocityContext vc = new VelocityContext();
		vc.put("ranges", ranges);
		((QuestionTemplate) nextq).setVelocityContex(vc);
		return nextq;
	}

	//visualizza la lista dei monitoraggi
	private static Question buildSeeAllQuestion(Question nextq, ArrayList<Tracking> trackings) {
		for (Tracking tracking : trackings) {
			Answer as = new Answer(tracking.getName());
			nextq.addSingleAnswer(as);
		}
		//nextq.addSingleAnswer(new Answer("Indietro"));
		VelocityContext vc = new VelocityContext();
		vc.put("trackings", trackings);
		((QuestionTemplate) nextq).setVelocityContex(vc);

		return nextq;
	}

	//visualizza un monitoraggio => con tutte le immagini allegate ad esso
	private static Question buildSeeOneQuestion(Question nextq, Tracking tracking, int userid) {
		VelocityContext vc = new VelocityContext();
		vc.put("tracking", tracking);
		vc.put("imageIds", tracking.getImages());
		((QuestionTemplate) nextq).setVelocityContex(vc);
		//this.tmanager.
		//Set images
		int i = 1;
		for (Tracking_image image : tracking.getImages()) {
			int numdetected = -1;
			String examDetected = "";
			String extractionStatus = "";
			if (image.getStatus().equals(Status.completato)) {
				extractionStatus = "E' stato possibile elaborarla.\n";
				ArrayList<Exam> exms = new TrackingManager(new UserDao().getUser(userid)).getExamsByImageId(image.getId());
				numdetected = exms.size();
			} else if (image.getStatus().equals(Status.fallito)) {
				extractionStatus = "*Non è stato possibile elaborarla.*\nRiprova, inviandone un'altra.\n";
			} else if (image.getStatus().equals(Status.elaborazione)) {
				extractionStatus = "Stiamo individuando le informazioni dall'immagine inviata.\n";
			}
			if (numdetected == 0) {
				examDetected = "La foto contiene del testo. Tuttavia, non siamo riusciti ad individuare nessun esame correttamente.\nRiprova, inviandone un'altra.\n";
			} else if (numdetected > 0) {
				String exam = "esami";
				if (numdetected == 1) {
					exam = "esame";
				}
				examDetected = "Abbiamo individuato " + numdetected + " " + exam + ".\n";
			}
			String sub = "Monitoraggio: *" + tracking.getName() + "*.\n"
					+ "Immagine nr: " + i + ".\n\n"
					+ "Informazioni:\n"
					+ extractionStatus
					+ examDetected + "\n";
			nextq.addSingleImage(image.getLink(), sub);
			i++;
		}
		return nextq;
	}

	//visualizza dettagli su un'immagine del monitoraggio
	private static Question buildSeeImageDetailsQuestion(Question nextq, ArrayList<Exam> exams, Tracking_image tracking_image) {
		int i = 1;
		for (Exam exam : exams) {
			//Answer as = new Answer(exam.getName());
			nextq.addSingleAnswer(new Answer(String.valueOf(i)));
			i++;
		}
		if (!tracking_image.getStatus().equals(Status.elaborazione))
			nextq.addSingleAnswer(new Answer("Aggiungi esame"));
		nextq.addSingleAnswer(new Answer("Indietro"));
		VelocityContext vc = new VelocityContext();
		vc.put("exams", exams);
		vc.put("trimage", tracking_image);

		((QuestionTemplate) nextq).setVelocityContex(vc);
		return nextq;
	}

	//visualizza i dettagli di un esame estratto da un'immagine associata a un tracking
	private static Question buildSeeExamDetailsQuestion(Question nextq, Exam exam) {
		String diff = "";
		double differ = 0.0;
		if (exam.getOutofrange() == RangeLimit.superior) {
			diff = "massimo";
			differ = (double) Math.round((exam.getResult() - exam.getMax()) * 100) / 100;
		} else if (exam.getOutofrange() == RangeLimit.inferior) {
			diff = "minimo";
			differ = (double) Math.round((exam.getMin() - exam.getResult()) * 100) / 100;
		}
		VelocityContext vc = new VelocityContext();
		vc.put("exam", exam);
		vc.put("diff", diff);
		vc.put("difference", differ);
		((QuestionTemplate) nextq).setVelocityContex(vc);
		return nextq;
	}

	private static Question buildSearchResultQuestion(Question nextq, HashMap<Tracking, String> trackingImage) {
		String prefixSub = "Risultato individuato nel monitoraggio ";
		String suffixSub = ". Risultati terminati. Inserisci una nuova parola per effettuare"
				+ " una nuova ricerca o premi Menu:";

		int i = 0;
		for (Tracking t : trackingImage.keySet()) {
			if (i++ == trackingImage.size() - 1) {
				nextq.addSingleImage(trackingImage.get(t),
						prefixSub + "*" + t.getName() + "*" + suffixSub);
			} else {
				nextq.addSingleImage(trackingImage.get(t),
						prefixSub + "*" + t.getName() + "*");
			}
		}

		return nextq;
	}

	//visualizza tutti i reminders attivi
	private static Question buildSeeAllRemindersQuestion(Question nextq, ArrayList<ExamReminder> examsReminders, String firstAnswer) {
		for (ExamReminder examRem : examsReminders) {
			Answer as = new Answer(examRem.getExam());
			nextq.addSingleAnswer(as);
		}
		nextq.addSingleAnswer(new Answer(firstAnswer));
		VelocityContext vc = new VelocityContext();
		vc.put("reminders", examsReminders);
		((QuestionTemplate) nextq).setVelocityContex(vc);
		return nextq;
	}

	//visualizza i dettagli di un reminder
	private static Question buildSeeOneReminderQuestion(Question nextq, ExamReminder examreminder) {
		VelocityContext vc = new VelocityContext();
		vc.put("reminder", examreminder);
		((QuestionTemplate) nextq).setVelocityContex(vc);
		return nextq;
	}

	//this.text è 'Visualizza reminders'
	private Question viewReminders(Question nextq) {
		//this.text è 'Visualizza reminders'
		ArrayList<ExamReminder> examsReminders = this.tmanager.seeAllExamsReminders();
		if (!examsReminders.isEmpty()) {
			nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_REMINDERS);
			nextq = buildSeeAllRemindersQuestion(nextq, examsReminders, this.getQstdao().getQuestion(TrackingQuestions.NO_EXAM_REMINDERS).getAnswers().get(0).getText());
		} else { //Nessun reminder
			nextq = this.getQstdao().getQuestion(TrackingQuestions.NO_EXAM_REMINDERS);
			nextq.setPreText("Non esiste alcuna notifica attiva.\n" +
					"(Le notifiche vengono attivate in automatico quando vengono rilevate delle periodicità.\n" +
					"E' possibile modificarle in seguito.)\n");
		}
		return nextq;
	}

	//this.text è "visualizza monitoraggi"
	private Question viewMonitorings(Question nextq) {
		ArrayList<Tracking> trackings = this.tmanager.seeAllTrackings();
		if (trackings != null) {
			nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_ALL);
			nextq = buildSeeAllQuestion(nextq, trackings);
		} else { //Nessun monitoraggio inserito
			nextq = this.getQstdao().getQuestion(TrackingQuestions.MENU);
			nextq.setPreText("Nessun monitoraggio inserito.");
		}
		return nextq;
	}

	//dettagli immagine
	private Question backImageDetails(Question nextq,Tracking_image trimage){
		if(trimage==null) {
			trimage = this.tmanager.getLastVisitedTrackingImage();
		}
		ArrayList<Exam> exams = this.tmanager.getExamsByImageId(trimage.getId());
		nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_IMAGE_DETAILS);
		nextq = buildSeeImageDetailsQuestion(nextq, exams, trimage);
		return nextq;
	}

	/*torna indietro dopo essere entrato nelle sezioni:
		-Aggiungi un immagine al monitoraggio
		-Dettagli di un immagine
		- Modifica nome monitoraggio
		- modifica data
		...ecc
	*/
	private Question backTrackingDetails(Question nextq) {
		Tracking tr = this.tmanager.getLastVisitedTracking();
		nextq = this.getQstdao().getQuestion(TrackingQuestions.SEE_ONE);
		nextq = buildSeeOneQuestion(nextq, tr, this.getUser().getId());
		return nextq;
	}
}
