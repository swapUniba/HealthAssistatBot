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

import com.triage.logic.response_producer.ResponseProducerSymptomChecker;
import com.triage.rest.dao.CurrentQuestionDao;
import com.triage.rest.dao.UserDao;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.users.User;
import com.triage.utils.NLPUtils;

@Path("/symptomCheckerResponse1")
public class SymptomCheckerResponseService {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	/*
	 * A seguito di un nuovo messaggio (richiesta POST) viene aggiornato il 
	 * database e generata una nuova risposta. 
	 */
	public Response symptomChecker(@FormParam("chatid") String chatId,						
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

		UserDao userdao = new UserDao();
		CurrentQuestionDao cqdao = new CurrentQuestionDao();
		
		User user = userdao.getUserOrAdd(Integer.parseInt(chatId), firstname, lastname, username);
		Question lastq = cqdao.getLastQuestion(user.getId());
		
		System.out.println("Calling symptom checker response...");
		ResponseProducerSymptomChecker rp = new ResponseProducerSymptomChecker(restRequest, user, lastq);
		Response response = rp.produceResponse();
		return response;
		/*if(response == null){
			//Error response
			Response responseErr = new Response(Integer.parseInt(chatId), "Error");
			return responseErr;
		}*/
	}
}
