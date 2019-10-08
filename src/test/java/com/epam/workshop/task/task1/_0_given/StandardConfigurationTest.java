package com.epam.workshop.task.task1._0_given;

import com.epam.workshop.task.task1.BaseTestCase;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.util.concurrent.CountDownLatch;

public class StandardConfigurationTest extends BaseTestCase {

    @Override
    protected Class<?>[] getUserConfigurations() {
        return new Class[] { Config.class };
    }

    @TestConfiguration
    public static class Config {

        @Bean
        public SimpleTriggerFactoryBean trigger(@Qualifier("jobDetail") JobDetail jobDetail) {
            SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
            trigger.setName("printerJobTrigger");
            trigger.setMisfireInstruction(Trigger.MISFIRE_INSTRUCTION_SMART_POLICY);
            trigger.setRepeatCount(-1);
            trigger.setRepeatInterval(1000L);
            trigger.setJobDetail(jobDetail);
            return trigger;
        }

        @Bean(name = "jobDetail")
        public JobDetailFactoryBean jobDetail() {
            JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
            jobDetail.setName("printerJobDetail");
            jobDetail.setDurability(true);
            jobDetail.setRequestsRecovery(false);
            jobDetail.setJobClass(JobClass.class);
            return jobDetail;
        }
    }

    @DisallowConcurrentExecution
    public static class JobClass extends QuartzJobBean {
        @Autowired
        private CountDownLatch latch;

        @Override
        protected void executeInternal(JobExecutionContext context) {
            System.out.println("Standard Configuration job!");
            latch.countDown();
        }
    }

}
