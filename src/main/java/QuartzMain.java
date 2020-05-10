import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.triage.logic.TherapyNotificationCreator;
import com.triage.rest.dao.TherapyDao;
import com.triage.rest.dao.UserDao;
import com.triage.rest.models.users.Therapy;
import com.triage.rest.models.users.User;
import com.triage.scheduler.TherapyJob;

public class QuartzMain {

	public static void main(String[] args) throws SchedulerException {
		TherapyDao tdao = new TherapyDao();
		Therapy th = tdao.getLastCreatedTherapy(298694218, true);
		System.out.println(th.toString());
		String newHour = "18:05";
		
		TherapyNotificationCreator tnc = new TherapyNotificationCreator(th, new User());
		tnc.deleteNotification();
		
		
		/*String jobGroupRoot = "Therapy:";
		int userId = 345699;
		String therapyName = "insulina sera";
		String therapyDosage = "1ml";
		
		final String JOB_THERAPY_NAME = "TherapyNotificationJob";
		final String JOB_THERAPY_GROUP = jobGroupRoot + userId;
		
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		//Create Job
		JobDetail job = JobBuilder.newJob(TherapyJob.class)
			.withIdentity(JOB_THERAPY_NAME, JOB_THERAPY_GROUP)
			.storeDurably()
			.build();
		scheduler.addJob(job, true);
		
		//Trigger the job to run on the next round minute
		Trigger trigger = TriggerBuilder
			.newTrigger()
			.withDescription("id:" + userId + "-therapyName:" + therapyName)
			.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(19, 0))
			.forJob(JOB_THERAPY_NAME, JOB_THERAPY_GROUP)
			.usingJobData("therapyName", therapyName)
			.usingJobData("dosage", therapyDosage)
			.build();

		//Schedule it
		scheduler.start();
		scheduler.scheduleJob(trigger);*/
	}
}
