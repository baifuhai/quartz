package com.test;

import java.util.List;

import org.junit.Test;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class JDBCJobTest {

	@Test
	public void testScheduleJob() throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		JobDetail job = JobBuilder.newJob(JDBCJob.class)
				.withDescription("description")
				.withIdentity("job", "jobGroup")
				.build();

		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(3);//执行3次，每隔1秒
		scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();//只执行1次
		
		Trigger trigger = TriggerBuilder.newTrigger()
				.withDescription("")
				.withIdentity("trigger", "triggerGroup")
				.withSchedule(scheduleBuilder)
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

	/**
	 * 从数据库中找到已经存在的job，并重新开户调度
	 */
	@Test
	public void testResumeJob() throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		JobKey jobKey = new JobKey("name", "group");
		
		// SELECT TRIGGER_NAME, TRIGGER_GROUP FROM {0}TRIGGERS WHERE SCHED_NAME = {1} AND JOB_NAME = ? AND JOB_GROUP = ?
		// 重新恢复在jGroup1组中，名为job1_1的 job的触发器运行
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
		if (triggers.size() > 0) {
			for (Trigger trigger : triggers) {
				// 根据类型判断
				if (trigger instanceof CronTrigger || trigger instanceof SimpleTrigger) {
					// 恢复job运行
					scheduler.resumeJob(jobKey);
				}
			}
			scheduler.start();
		}
	}

}
