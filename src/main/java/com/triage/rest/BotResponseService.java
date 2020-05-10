package com.triage.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.triage.logic.questions.BaseBotQuestions;
import com.triage.logic.questions.SurveyQuestions;
import com.triage.logic.response_producer.ResponseProducerBot;
import com.triage.rest.dao.CurrentQuestionDao;
import com.triage.rest.dao.MessageDao;
import com.triage.rest.dao.UserDao;
import com.triage.rest.models.messages.Message;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.ReplayMarkup;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.users.User;
import com.triage.utils.NLPUtils;
import com.triage.utils.ResponseButtonUtils;

@Path("/botResponse")
public class BotResponseService {
	
	@Context private ResourceContext rc;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)//APPLICATION_FORM_URLENCODED)
	/*
	 * A seguito di un nuovo messaggio (richiesta POST) viene aggiornato il 
	 * database e generata una nuova risposta. 
	 */
	public Response addUser(@FormDataParam("chatid") String chatId,						
							@FormDataParam("messageid") String messageId,
							@FormDataParam("date") String date,
							@FormDataParam("firstname") String firstname,
							@FormDataParam("lastname") String lastname,
							@FormDataParam("username") String username,
							@FormDataParam("text") String text,
							@FormDataParam("photo_id") String photo_id,
							@FormDataParam("photo") InputStream uploadedInputStream,
							@FormDataParam("photo") FormDataContentDisposition fileDetail,
								@Context HttpServletResponse servletResponse) 
										throws IOException {
		NLPUtils.cleanInputMessage(text);
		RestRequestInput restRequest = new RestRequestInput(chatId, messageId, date, firstname, lastname, username, text);
		System.out.println("Received input: " + restRequest);
		
		UserDao userdao = new UserDao();
		MessageDao msgdao = new MessageDao();
		CurrentQuestionDao cqdao = new CurrentQuestionDao();

		//se l'utente già esiste lo recupera dal db, altrimenti ne memorizza uno nuovo
		User user = userdao.getUserOrAdd(Integer.parseInt(chatId), firstname, lastname, username);
		//crea una nuova istanza di messaggio
		Message mess = new Message(Integer.parseInt(chatId), Integer.parseInt(messageId),
													false, Long.parseLong(date), text);
		//memorizza il messaggio ricevuto dall'utente, nel database.
		msgdao.saveNewMessage(mess);

		//recupera ultima domanda posta dall'utente durante la conversazione
		Question lastq = cqdao.getLastQuestion(user.getId());
		//recupero ultima sezione (Tracking, menu ecc)
		String menuSection = cqdao.getLastSectionMenu(user.getId());
		System.out.println("Last question: " + lastq.getQuestionName() + " " + menuSection);
		System.out.println("Calling base bot response...");

		//ora dobbiamo formulare una risposta da inviare al bot a partire da ultima domanda posta, user e tutto ciò che sta nella restRequest
		ResponseProducerBot rpb = new ResponseProducerBot(restRequest, user, lastq);
		//genera la risposta
		Response response = rpb.produceResponse();

		if(response != null){
			return response;
		}else{
			//String menuSection = cqdao.getLastSectionMenu(user.getId());
			try{
				if(menuSection.equals("SymptomChecker")){
					SymptomCheckerResponseService resSC = rc.getResource(SymptomCheckerResponseService.class);
					return resSC.symptomChecker(chatId, messageId, date, firstname, lastname, username,
												text, servletResponse);
				}
				if(menuSection.equals("SuggestDoctor")){
					SuggestDoctorResponseService resSC = rc.getResource(SuggestDoctorResponseService.class);
					return resSC.suggestDoctor(chatId, messageId, date, firstname, lastname, username,
												text, servletResponse);
				}
				if(menuSection.equals("Dictionary")){
					MedicalDictionaryResponseService resDICT = rc.getResource(MedicalDictionaryResponseService.class);
					return resDICT.dictionary(chatId, messageId, date, firstname, lastname, username,
												text, servletResponse);
				}
				if(menuSection.equalsIgnoreCase("Therapy")){
					TherapyResponseService resTH = rc.getResource(TherapyResponseService.class);
					return resTH.therapy(chatId, messageId, date, firstname, lastname, username, 
										text, servletResponse);
				}
				if(menuSection.equals("Tracking")){
					/*String ocrScanText = null;
					if(photo_id != null){
						OCRResponseService resOCR = rc.getResource(OCRResponseService.class);
						ocrScanText = resOCR.makeOCR("", servletResponse);
					}*/
					TrackingResponseService resTR = rc.getResource(TrackingResponseService.class);
					return resTR.tracking(chatId, messageId, date, firstname, lastname, username, text, 
											photo_id, uploadedInputStream, fileDetail, servletResponse);
				}
				if(menuSection.equals("Survey")){
					/*String ocrScanText = null;
					if(photo_id != null){
						OCRResponseService resOCR = rc.getResource(OCRResponseService.class);
						ocrScanText = resOCR.makeOCR("", servletResponse);
					}*/
					SurveyResponseService resSU = rc.getResource(SurveyResponseService.class);
					return resSU.survey(chatId, messageId, date, firstname, lastname, username, 
											text, servletResponse);
				}
			}catch (Exception e) {
				System.out.println("[Catched customexceptions]");
				e.printStackTrace();
				
				//Send user to menu
				BaseBotQuestions bbq = new BaseBotQuestions();
				Question nextq = bbq.getQuestion("MENU");
				nextq.setText("Scegli una funzionalità premendo il corrispondente bottone");
				nextq.setPreText("C'è stato un errore :(. ");
				Message mresp = new Message(user.getId(), true, System.currentTimeMillis(),
						nextq.getQuestionText());
				
				Response resp = new Response(user.getId(), nextq.getQuestionText());//Change to u.getChatId()
				ReplayMarkup rm = new ReplayMarkup(ResponseButtonUtils.transformToGridButton(nextq.getAnswers(), 2), true);//ToGridButton(nextq.getAnswers(), 5), true);
				resp.setReplaymarkup(rm);
				
				cqdao.saveCurrentQuestion(user.getId(), nextq.getQuestionName());
				cqdao.saveCurrentSectionMenu(user.getId(), "Menu");
				msgdao.saveNewMessage(mresp);
				
				return resp;
			}
		}
		
		//Error response
		Response responseErr = new Response(Integer.parseInt(chatId), "Error");
		return responseErr;
	}
}
