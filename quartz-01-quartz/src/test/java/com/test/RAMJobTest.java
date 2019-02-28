package com.test;

import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class RAMJobTest {

	@Test
	public void testScheduleJob() throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		JobDetail job = JobBuilder.newJob(RAMJob.class)
				.withDescription("description")
				.withIdentity("job", "jobGroup")
				.build();
		
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/1 * * * * ?");// 每隔1秒执行1次
		
		//long time = System.currentTimeMillis() + 2 * 1000L; // 3秒后启动任务
		//Date startTime = new Date(time);
		
		Trigger trigger = TriggerBuilder.newTrigger()
				.withDescription("")
				.withIdentity("trigger", "triggerGroup")
				.withSchedule(scheduleBuilder)
				//.startAt(startTime) // 默认立即启动
				.startNow()
				.build();
		
		scheduler.scheduleJob(job, trigger);
		
		scheduler.start();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("shutdown...");

		scheduler.shutdown();
	}

}
