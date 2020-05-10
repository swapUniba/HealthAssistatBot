package com.triage.rest;

import java.io.IOException;
import java.io.InputStream;
/*import java.time.LocalDate;
import java.time.Month;
import java.time.Period;*/
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.core.header.FormDataContentDisposition;

import com.sun.jersey.multipart.FormDataParam;

import com.triage.logic.OCRApi;
import com.triage.logic.OCRLucene;
import com.triage.logic.customexceptions.TrackingException;
import com.triage.logic.managers.TrackingManager;
import com.triage.logic.response_producer.ResponseProducerTracking;
import com.triage.push_notification.PushNotifications;
import com.triage.push_notification.TrackingPushNotifications;
import com.triage.rest.dao.CurrentQuestionDao;
import com.triage.rest.dao.TrackingDao;
import com.triage.rest.dao.TriageBotConnection;
import com.triage.rest.dao.UserDao;
import com.triage.rest.enummodels.RangeLimit;
import com.triage.rest.enummodels.Status;
import com.triage.rest.models.messages.*;
import com.triage.rest.models.users.Exam;
import com.triage.rest.models.users.ExamReminder;
import com.triage.rest.models.users.Tracking;
import com.triage.rest.models.users.User;
import com.triage.utils.NLPUtils;
import com.triage.utils.ResponseButtonUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.json.simple.JSONObject;
import org.quartz.SchedulerException;

@Path("/tracking")
public class TrackingResponseService {
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)//APPLICATION_FORM_URLENCODED)
	/*
	 * A seguito di un nuovo messaggio (richiesta POST) viene aggiornato il 
	 * database e generata una nuova risposta. 
	 */
	public Response tracking(@FormDataParam("chatid") String chatId,						
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
		restRequest.setPhotoId(photo_id);
		if(uploadedInputStream != null && fileDetail != null){
			System.out.println("Received also image: " + fileDetail.getFileName());
			
			//Send image to OCR response service. Response of OCR response service on
			// 'responseOCR' method of this class
			/*FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
			formDataMultiPart.field("file", uploadedInputStream, MediaType.MULTIPART_FORM_DATA_TYPE)
							.field("image_id", photo_id)
							.field("path", fileDetail.getFileName())
							.field("return_path", "http://localhost:8080/TriageBotRestServer/rest/tracking/receiveOCR");
							
			Client client = Client.create();
			WebResource service = client
					.resource(UriBuilder.fromUri(OCR_URL).build());
			ClientResponse clientResp = service.type(MediaType.MULTIPART_FORM_DATA_TYPE)
												.post(ClientResponse.class, formDataMultiPart);
			client.destroy();*/
		}
		
		UserDao userdao = new UserDao();
		CurrentQuestionDao cqdao = new CurrentQuestionDao();
		
		User user = userdao.getUserOrAdd(Integer.parseInt(chatId), firstname, lastname, username);
		Question lastq = cqdao.getLastQuestion(user.getId());
		
		System.out.println("Calling tracking response...");
		String fileName = null;
		if(fileDetail != null)
			fileName = fileDetail.getFileName();
		ResponseProducerTracking rp = new ResponseProducerTracking(restRequest, user, lastq, 
												uploadedInputStream, fileName);
		Response response = rp.produceResponse();
		return response;
		/*if(response == null){
			//Error response
			Response responseErr = new Response(Integer.parseInt(chatId), "Error");
			return responseErr;
		}*/
	}

	@POST
	@Path("/receiveOCR")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	/*
	 * Servizio che in maniera asincrona viene richiamato quando l'OCR è finito.
	 * Salva i risultati in Lucene e nel DB.
	 */
	public String responseOCR(@FormParam("photo_id") String photo_id,
							  @FormParam("tracking_id") String tracking_id,
							  @FormParam("user_id") String user_id,
								@Context HttpServletResponse servletResponse) throws IOException {
		System.out.println("Received a result about the photo: "+ photo_id);
		System.out.println("Received a result of user: "+ user_id);
		ArrayList<Response> pushNotifications_ranges = new ArrayList<>();
		ArrayList<Response> pushNotifications_periodicities= new ArrayList<>();
		TrackingPushNotifications tpn= new TrackingPushNotifications();
		if( photo_id != null && tracking_id != null && user_id!=null) {
			System.out.println("Server OCR completed the processing for the image: "+ photo_id);
			TrackingDao tdao = new TrackingDao();
			try {
				String results = new OCRApi().get_result(photo_id);
				if (results != null) {
					//processing the results from the OCR server
					JSONArray arr = new JSONArray(results);
					String error = arr.getJSONObject(0).getString("Error");
					//Create OCRText object
					if (arr.length() > 1) {
						String text = arr.getJSONObject(1).getString("Text");
						String info= arr.getJSONObject(2).getString("Info");
						//if (!info.isEmpty()) {
						//	Tracking t = tdao.getTrackingByImageID(Integer.valueOf(user_id), Integer.valueOf(photo_id));
							//String message = "Attenzione: Nessun esame individuato in una delle immagini inserite nel monitoraggio: *" + t.getName() + "*.\nVisualizza il monitoraggio per saperne di più.";

							//Response resp = new Response(Integer.valueOf(user_id), message);
							//new PushNotifications().sendResponseObject(resp);
						//}
						OCRText ocr = new OCRText(String.valueOf(photo_id), text);
						if (error.isEmpty()) {
							List<Exam> exams = new ArrayList<Exam>();
							for (int i = 3; i < arr.length(); i++) {
								org.codehaus.jettison.json.JSONObject l = arr.getJSONObject(i);
								Exam x = new Exam(l.getString("Exam"), l.getDouble("Result"), l.getDouble("Min"), l.getDouble("Max"), l.getString("Unit"),RangeLimit.valueOf(l.getString("OutOfRange")));
								exams.add(x);
								//se necessario, invia le dovute notifiche.
								Response res_rang = tpn.createNotificationsAboutRanges(Integer.valueOf(user_id),Integer.valueOf(photo_id),x);
								if (res_rang!=null) {
									pushNotifications_ranges.add(res_rang);
								}
								Response res_dp = tpn.createNotificationsAboutDatePattern(Integer.valueOf(user_id),x);
								if (res_dp!=null) {
									pushNotifications_periodicities.add(res_dp);
								}
							}
							OCRExam ocrexams = new OCRExam(Integer.valueOf(photo_id),Integer.valueOf(tracking_id), exams);
							tdao.saveOCRExamResult(ocrexams); //Save on db
							tdao.saveOCRResult(ocr); //Save on db (TODO evitate duplicates and see mapping image-tracking)
							tdao.updateTrackingImageStatus(Integer.valueOf(photo_id), Status.completato);
							//Create a new document on lucene index
							OCRLucene.getInstance().addOCRDocument(ocr,Integer.valueOf(user_id));
							//sendNotifications
							tpn.sendNotifications_examsoutofranges(pushNotifications_ranges);
							tpn.sendNotifications_examsperiodicities(pushNotifications_periodicities,user_id);
							Tracking t= tdao.getTrackingByImageID(Integer.valueOf(user_id),Integer.valueOf(photo_id));
							tpn.send_tracking_news_notification(Integer.valueOf(user_id),t);
						} else {
							throw new TrackingException(error);
						}
					} else {
						throw new TrackingException(error);
					}
				}
			} catch (JSONException | TrackingException e) {
				if (!e.getMessage().equals("Richiesta in elaborazione")){
					tdao.updateTrackingImageStatus(Integer.valueOf(photo_id), Status.fallito);
					//invia notifica push relativa all'elaborazione fallita
					Tracking t= tdao.getTrackingByImageID(Integer.valueOf(user_id),Integer.valueOf(photo_id));
					//String message = "Attenzione: C'è stato un problema con qualche immagine associata al monitoraggio: *" + t.getName()+ "*.\nVisualizza il monitoraggio per saperne di più.";
					tpn.send_tracking_news_notification(Integer.valueOf(user_id),t);
				}
				System.err.println("Si e' verificato un errore generico con l'immagine: " + photo_id + "\n" + e.getMessage() + "\n");
				e.printStackTrace();
			}
		}
		return "OK";
	}
}
