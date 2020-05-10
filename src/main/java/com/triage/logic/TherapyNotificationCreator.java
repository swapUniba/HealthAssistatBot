package com.triage.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import com.triage.rest.models.users.Therapy;
import com.triage.rest.models.users.TherapyDay;
import com.triage.rest.models.users.User;
import com.triage.scheduler.TherapyJob;

public class TherapyNotificationCreator {
	private final String JOB_NAME = "TherapyNotificationJob";
	private final String JOB_GROUP = "TherapyNotificationJob";
	
	private Therapy therapy;
	private User user;
	private Scheduler scheduler;
	
	public TherapyNotificationCreator(Therapy therapy, User user){
		this.therapy = therapy;
		this.user = user;
		try {
			this.scheduler = new StdSchedulerFactory().getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	private final String TRIGGER_NAME_ROOT = "notification-";
	private String TRIGGER_NAME;
	private final String TRIGGER_GROUP_ROOT = "therapy-";
	private String TRIGGER_GROUP;
	
	public void createNotification(){
		//Create Job (or replace if alredy exists)
		JobDetail job = JobBuilder.newJob(TherapyJob.class)
			.withIdentity(JOB_NAME, JOB_GROUP)
			.withDescription(Integer.toString(this.user.getId()))
			.storeDurably()
			.build();
		try {
			this.scheduler.addJob(job, true);
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
		
		for(String timeStr : this.therapy.getHours()){
			this.TRIGGER_NAME = TRIGGER_NAME_ROOT  + timeStr;
			this.TRIGGER_GROUP = TRIGGER_GROUP_ROOT + therapy.getId();
			
			String[] time = timeStr.trim().split(":");
			int hour = Integer.parseInt(time[0]);
			int min = Integer.parseInt(time[1]);
			
			//One trigger one for each therapy time. Choose Schedule based on Therapy Type
			TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
				.withIdentity(TRIGGER_NAME, TRIGGER_GROUP)
				.withDescription(this.therapy.toString());
			switch(this.therapy.getType()){
				case EVERY_DAY:
					builder.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hour, min));
					break;
				case ODD_DAY:
					//Start date = start date + therapy date min
					Calendar cal = Calendar.getInstance();
					cal.setTime(this.therapy.getStartTime());
					cal.set(Calendar.HOUR_OF_DAY, hour);
					cal.set(Calendar.MINUTE, min);
					builder.startAt(cal.getTime());
					
					builder.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInHours(2 * 24) // interval is actually set at 48 hours' worth of milliseconds
				        	.repeatForever());
					/*builder.withSchedule(
						CronScheduleBuilder.cronSchedule("0 " + min+ " " + hour + " 1-31/2 * ?"));*/
					break;
				case INTERVAL:
					//Start date = start date + therapy date min
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(this.therapy.getStartTime());
					cal2.set(Calendar.HOUR_OF_DAY, hour);
					cal2.set(Calendar.MINUTE, min);
					builder.startAt(cal2.getTime());
					
					builder.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInHours(this.therapy.getIntervalDays() * 24) // interval is actually set at 48 hours' worth of milliseconds
				        	.repeatForever());
					/*builder.withSchedule(
						CronScheduleBuilder.cronSchedule("0 " + min+ " " + hour + " 1-31/2 * ?"));*/
					break;
				case SOME_DAY:
					System.out.println(this.therapy.getDays());
					ArrayList<Integer> daysId = TherapyDay.getDaysIdxQuartzCron(this.therapy.getDays());
					String daysIdStr = DaysIdsToString(daysId);
					
					builder.withSchedule(
						CronScheduleBuilder.cronSchedule("0 " + min+ " " + hour + " ? * " + daysIdStr));
			}
			if(this.therapy.getEndTime() != null){
				//Parsed data are: GG-MM-AAAA 00.00.00
				//Modify date so became: GG-MM-AAAA 24.00.00
				Date newEndDate = DateUtils.addHours(this.therapy.getEndTime(), 24);				
				//builder.endAt(this.therapy.getEndTime());
				builder.endAt(newEndDate);
			}
			
			builder.forJob(JOB_NAME, JOB_GROUP)
			.usingJobData("userId", this.user.getId())
			.usingJobData("therapyName", this.therapy.getName())
			.usingJobData("therapyDosage", this.therapy.getDosage())
			.usingJobData("therapyHour", timeStr)
			.usingJobData("drugName", this.therapy.getDrugName());
			Trigger trigger = builder.build();
			try {
				this.scheduler.scheduleJob(trigger); //Schedule trigger
			} catch (SchedulerException e) {
				//e.printStackTrace();
				System.out.println("Trigger " + TRIGGER_NAME + TRIGGER_GROUP + " will never fire. "
						+ "Notification time is lower than end time");
				System.out.println("Logs:" + therapy.toString() + 
						"Current time: " + System.currentTimeMillis());
			}
		}
	}
	
	public void updateNotification(Therapy newTherapy, boolean updatedEndDate){
		this.therapy = newTherapy;
		this.createNotification();
	}
	
	public void deleteNotification(){
		ArrayList<TriggerKey> triggers = new ArrayList<TriggerKey>();
		for(String timeStr : this.therapy.getHours()){
			this.TRIGGER_NAME = TRIGGER_NAME_ROOT  + timeStr;
			this.TRIGGER_GROUP = TRIGGER_GROUP_ROOT + therapy.getId();
			
			TriggerKey tk = new TriggerKey(TRIGGER_NAME, TRIGGER_GROUP);
			triggers.add(tk);
		}
		
		try {
			this.scheduler.unscheduleJobs(triggers);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	private String DaysIdsToString(ArrayList<Integer> daysIds){
		StringBuilder sb = new StringBuilder();
		for(int dayId : daysIds){
			sb.append(dayId + ",");
		}
		String str = sb.toString().trim();
		return str.substring(0, str.length()-1);
	}
}
