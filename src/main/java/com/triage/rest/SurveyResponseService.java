package com.triage.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.triage.logic.response_producer.ResponseProducerSurvey;
import com.triage.rest.dao.CurrentQuestionDao;
import com.triage.rest.dao.MessageDao;
import com.triage.rest.dao.UserDao;
import com.triage.rest.models.messages.Message;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.users.User;
import com.triage.utils.NLPUtils;

@Path("/surveyResponse")
public class SurveyResponseService {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	/*
	 * A seguito di un nuovo messaggio (richiesta POST) viene aggiornato il 
	 * database e generata una nuova risposta. 
	 */
	public Response survey(@FormParam("chatid") String chatId,						
								@FormParam("messageid") String messageId,
								@FormParam("date") String date,
								@FormParam("firstname") String firstname,
								@FormParam("lastname") String lastname,
								@FormParam("username") String username,
								@FormParam("text") String text,
								@Context HttpServletResponse servletResponse) 
										throws IOException {
		NLPUtils.cleanInputMessage(text);
		RestRequestInput restRequest = new RestRequestInput(chatId, messageId, date, firstname, lastname, username, text);
		System.out.println("Received input: " + restRequest);
		
		UserDao userdao = new UserDao();
		MessageDao msgdao = new MessageDao();
		CurrentQuestionDao cqdao = new CurrentQuestionDao();
		
		User user = userdao.getUserOrAdd(Integer.parseInt(chatId), firstname, lastname, username);
		Message mess = new Message(Integer.parseInt(chatId), Integer.parseInt(messageId),
				false, Long.parseLong(date), text);
		msgdao.saveNewMessage(mess);
		
		Question lastq = cqdao.getLastQuestion(user.getId());
		System.out.println("Last question: " + lastq.getQuestionName());
		
		System.out.println("Calling symptom checker response...");
		ResponseProducerSurvey rp = new ResponseProducerSurvey(restRequest, user, lastq);
		Response response = rp.produceResponse();
		return response;
		/*if(response == null){
			//Error response
			Response responseErr = new Response(Integer.parseInt(chatId), "Error");
			return responseErr;
		}*/
	}
}
