package com.triage.scheduler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.triage.push_notification.PushNotifications;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.triage.rest.models.messages.Response;

public class TherapyJob implements Job{

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getMergedJobDataMap();
		int userId = dataMap.getInt("userId");
		String therapyName = dataMap.getString("therapyName");
		String therapyDosage = dataMap.getString("therapyDosage");
		String therapyHour = dataMap.getString("therapyHour");
		String drugName = dataMap.getString("drugName");
		
		//Create response message
		String message = "Sono le " + therapyHour + ". "
				+ "Ricorda la tua terapia: " + therapyName + ". "
				+ "Dosaggio: " + therapyDosage+ ". "
				+ "Medicinale: " + drugName;
		Response resp = new Response(userId, message);
		
		System.out.println("[QuartzJob] User id: " + userId + " therapy: " + therapyName);

		new PushNotifications().sendResponseObject(resp);
	}


}
