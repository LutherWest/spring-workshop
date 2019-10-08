package com.epam.workshop.task.task1;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class BaseTestCase {
    public final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(SchedulerConfiguration.class, CountDownLatchConfig.class);

    @Test
    public void shouldStartJobExecution() {
        runner
                .withUserConfiguration(getUserConfigurations())
                .run(ctx -> {
                    Assertions.assertThat(ctx)
                            .hasSingleBean(JobDetailFactoryBean.class)
                            .hasSingleBean(JobDetail.class)
                            .hasSingleBean(SimpleTriggerFactoryBean.class)
                            .hasSingleBean(Trigger.class);

                    final CountDownLatch latch = ctx.getBean(CountDownLatch.class);

                    Assertions.assertThat(latch.await(5L, TimeUnit.SECONDS))
                            .describedAs("Job should be executed at least 2 times!")
                            .isTrue();
                });
    }

    protected abstract Class<?>[] getUserConfigurations();

    @TestConfiguration
    public static class CountDownLatchConfig {
        @Bean
        public CountDownLatch latch() {
            return new CountDownLatch(2);
        }
    }

    @TestConfiguration
    public static class SchedulerConfiguration {

        @Bean
        public SchedulerFactoryBean scheduler(JobFactory jobFactory, ObjectProvider<Trigger[]> triggers) {
            SchedulerFactoryBean bean = new SchedulerFactoryBean();
            bean.setSchedulerName("name");
            bean.setWaitForJobsToCompleteOnShutdown(true);
            bean.setOverwriteExistingJobs(true);
            bean.setJobFactory(jobFactory);
            bean.setAutoStartup(true);
            triggers.ifAvailable(bean::setTriggers);

            Properties props = new Properties();
            props.put("org.quartz.scheduler.instanceId", "AUTO");
            props.put("org.quartz.threadPool.threadCount", "2");
            props.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");

            bean.setQuartzProperties(props);

            return bean;
        }

        @Bean
        public JobFactory beanFactoryJobFactory(ApplicationContext ctx) {
            final SpringBeanJobFactory springBeanJobFactory = new SpringBeanJobFactory();
            springBeanJobFactory.setApplicationContext(ctx);
            return springBeanJobFactory;
        }
    }

}
