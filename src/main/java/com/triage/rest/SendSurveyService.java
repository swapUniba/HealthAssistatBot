package com.triage.rest;

import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.triage.logic.questions.SurveyQuestions;
import com.triage.rest.dao.CurrentQuestionDao;
import com.triage.rest.dao.MessageDao;
import com.triage.rest.dao.SurveyDao;
import com.triage.rest.dao.UserDao;
import com.triage.rest.models.messages.Message;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.ReplayMarkup;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.users.User;
import com.triage.utils.ResponseButtonUtils;

/**
* Utility class to send survey to users.
*/
@Path("/survey")
public class SendSurveyService {
	@GET
	//@Produces(MediaType.APPLICATION_JSON)
	/*
	 * A seguito di un nuovo messaggio (richiesta POST) viene aggiornato il 
	 * database e generata una nuova risposta. 
	 */
	public String sendSurvey(@QueryParam("password") String password,
								@QueryParam("to") String to) throws IOException {
		if(password == null || to == null)
			return "No value for parameters 'to' or 'password'";
		if(!to.equalsIgnoreCase("all") && !to.equalsIgnoreCase("admin") && !to.equalsIgnoreCase("remaining"))
			return "Wrong value for parameter 'to'";
		if(!password.equals("663"))
			return "You have no access"; 

		UserDao udao = new UserDao();
		SurveyDao sdao = new SurveyDao();
		ArrayList<User> users = new ArrayList<User>();
		if(to.equals("all")){
			users = udao.getAllUsers();
			System.out.println(users);
			System.out.println("[Survey] Sending survey to all users...");
		}else if(to.equals("remaining")){
			ArrayList<Integer> usersId = sdao.getUserNotEndedSurvey();
			for(int uid : usersId){
				users.add(udao.getUser(uid));
			}
			System.out.println("[Survey] Sending survey to user that have not completed survey...");
		}else{
			users.add(udao.getUser(298694218));
			System.out.println("[Survey] Sending survey to admin users...");
		}		
		
		CurrentQuestionDao cqdao = new CurrentQuestionDao();
		MessageDao msgdao = new MessageDao();
		SurveyQuestions squestions = new SurveyQuestions();
		for(User u : users){
			Question nextq = squestions.getQuestion(SurveyQuestions.Q1_PRE);
			
			//Survey logic
			sdao.createNewSurvey(u.getId());
			
			//Create response message
			Message mresp = new Message(u.getId(), true, System.currentTimeMillis(),
					nextq.getQuestionText());
			Response resp = new Response(u.getId(), nextq.getQuestionText());//Change to u.getChatId()
			ReplayMarkup rm = new ReplayMarkup(ResponseButtonUtils.transformToLinearButton(nextq.getAnswers()), true);//ToGridButton(nextq.getAnswers(), 5), true);
			resp.setReplaymarkup(rm);
			
			//Bot logic
			cqdao.saveCurrentQuestion(u.getId(), nextq.getQuestionName());//Change to u.getChatId()
			cqdao.saveCurrentSectionMenu(u.getId(), "Survey");
			msgdao.saveNewMessage(mresp);
			
			//Send response message to client
			sendResponseObject(resp, "http://triagebot.herokuapp.com/push.php"); 
		}
		return "Survey sended to " + to;
	}
	
	private static boolean sendResponseObject(Response response, String clientPushPath){
		Client client = Client.create();
		WebResource webResource = client.resource(clientPushPath);

		Gson g = new Gson();
		String json = g.toJson(response);
		MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
		formData.add("response", json);
				
		ClientResponse clientResponse = webResource
		    .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
		    .accept("application/json")
	        .post(ClientResponse.class, formData);
		int status = clientResponse.getStatus();
		
		System.out.println(status);
		if(status == 200)
			return true;
		else
			return false;
	}
}
