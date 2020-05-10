package com.triage.scheduler;

import com.triage.push_notification.PushNotifications;
import com.triage.rest.models.messages.Response;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TrackingJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        int userId = dataMap.getInt("userId");
        String examName = dataMap.getString("examName");

        //Create response message
        String message = "Ricorda di rifare l'esame *" + examName + "*. \n";
        Response resp = new Response(userId, message);

        System.out.println("[QuartzJob] User id: " + userId + " exam: " + examName);

        new PushNotifications().sendResponseObject(resp);
    }
}
