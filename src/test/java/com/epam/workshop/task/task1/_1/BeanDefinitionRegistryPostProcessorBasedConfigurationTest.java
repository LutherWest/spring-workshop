package com.epam.workshop.task.task1._1;

import com.epam.workshop.task.task1.BaseTestCase;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.concurrent.CountDownLatch;

public class BeanDefinitionRegistryPostProcessorBasedConfigurationTest extends BaseTestCase {

    @Override
    protected Class<?>[] getUserConfigurations() {
        return new Class[] { Config.class };
    }

    @TestConfiguration
    @ComponentScan
    public static class Config {

        @Bean
        public static BeanDefinitionRegistryPostProcessor repeatableQuartzJobBeanDefinitionRegistryPostProcessor() {
            return new RepeatableQuartzJobBeanDefinitionRegistryPostProcessor();
        }
    }

    @RepeatableQuartzJob(repeatInterval = 2000)
    @DisallowConcurrentExecution
    public static class JobClass extends QuartzJobBean {
        @Autowired
        private CountDownLatch latch;

        @Override
        protected void executeInternal(JobExecutionContext context) {
            System.out.println("BeanDefinitionRegistryBeanPostProcessor based Config job!");
            latch.countDown();
        }
    }
}
