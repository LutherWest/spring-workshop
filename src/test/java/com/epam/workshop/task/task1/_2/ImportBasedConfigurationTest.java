package com.epam.workshop.task.task1._2;

import com.epam.workshop.task.task1.BaseTestCase;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.concurrent.CountDownLatch;

public class ImportBasedConfigurationTest extends BaseTestCase {

    @Override
    protected Class<?>[] getUserConfigurations() {
        return new Class[] { Config.class };
    }

    @TestConfiguration
    @EnableRepeatableQuartzJob
    public static class Config {

    }

    @RepeatableQuartzJob(repeatInterval = 1000)
    @DisallowConcurrentExecution
    public static class JobClass extends QuartzJobBean {
        @Autowired
        private CountDownLatch latch;

        @Override
        protected void executeInternal(JobExecutionContext context) {
            System.out.println("Import based Configuration job!");
            latch.countDown();
        }
    }
}
