package com.triage.logic;

import com.triage.rest.models.users.*;
import com.triage.scheduler.TherapyJob;
import com.triage.scheduler.TrackingJob;
import com.triage.utils.NLPUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDate;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/*import java.time.LocalDate;
import java.time.Period;*/
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TrackingNotificationCreator {
    private final String JOB_NAME = "TrackingNotificationJob";
    private final String JOB_GROUP = "TrackingNotificationJob";

    private ExamReminder examreminder;
    private User user;
    private LocalDate dt;
    private Scheduler scheduler;
    private final String TRIGGER_NAME_ROOT = "notification-";
    private String TRIGGER_NAME;
    private final String TRIGGER_GROUP_ROOT = "tracking-";
    private String TRIGGER_GROUP;

    public TrackingNotificationCreator(ExamReminder examreminder, User user, LocalDate dt){
        this.examreminder = examreminder;
        this.user = user;
        this.dt= dt;
        try {
            this.scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void createNotification() throws SchedulerException{
        //Create Job (or replace if alredy exists)
        JobDetail job = JobBuilder.newJob(TrackingJob.class)
                .withIdentity(JOB_NAME, JOB_GROUP)
                .withDescription(Integer.toString(this.user.getId()))
                .storeDurably()
                .build();
        try {
            this.scheduler.addJob(job, true);
        } catch (SchedulerException e1) {
            e1.printStackTrace();
        }
        this.TRIGGER_NAME = TRIGGER_NAME_ROOT  + this.dt.getDayOfMonth()+"/"+this.dt.getMonthOfYear()+"/"+this.dt.getYear();
        this.TRIGGER_GROUP = TRIGGER_GROUP_ROOT + this.user.getId()+"-"+this.examreminder.getExam();
        //Triggers for tracking
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(TRIGGER_NAME, TRIGGER_GROUP)
                .withDescription("testing");
        builder.withSchedule(CronScheduleBuilder.cronSchedule("0 " + this.examreminder.getHour().split(":")[1]+ " " + this.examreminder.getHour().split(":")[0]+ " " + this.dt.getDayOfMonth() + " "+ this.dt.getMonthOfYear() + " ? " + this.dt.getYear()));
            builder.forJob(JOB_NAME, JOB_GROUP)
                    .usingJobData("userId", this.user.getId())
                    .usingJobData("examName", this.examreminder.getExam());
        Trigger trigger = builder.build();
        this.scheduler.scheduleJob(trigger); //Schedule trigger
    }

    public void deleteNotification() {
        ArrayList<TriggerKey> triggers = new ArrayList<TriggerKey>();
        this.TRIGGER_NAME = TRIGGER_NAME_ROOT  + this.dt.getDayOfMonth()+"/"+this.dt.getMonthOfYear()+"/"+this.dt.getYear();
        this.TRIGGER_GROUP = TRIGGER_GROUP_ROOT + this.user.getId()+"-"+this.examreminder.getExam();
        TriggerKey tk = new TriggerKey(TRIGGER_NAME, TRIGGER_GROUP);
        triggers.add(tk);

        try {
            this.scheduler.unscheduleJobs(triggers);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
