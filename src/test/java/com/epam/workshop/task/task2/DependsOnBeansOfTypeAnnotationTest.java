package com.epam.workshop.task.task2;

import com.epam.workshop.task.task2.support.EnableRepeatableQuartzJob;
import com.epam.workshop.task.task2.support.RepeatableQuartzJob;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

public class DependsOnBeansOfTypeAnnotationTest {
    private static boolean depends1;

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(UserConfigurations.of(DependsOnConfig.class, QuartzJobConfig.class));

    @Test
    public void shouldSetupDependsOnBeansOfTypeOnMethod() {
        runner.withUserConfiguration(DependsOnMethodBeanConfiguration.class)
                .run(ctx -> Assertions.assertThat(ctx)
                        .hasNotFailed()
                        .getBean("dependentBean").isInstanceOf(Runnable.class));
        depends1 = false;
    }

    @Test
    public void shouldSetupDependsOnBeansOfTypeOnClass() {
        runner.withUserConfiguration(TestDependsOnComponent.class)
                .run(ctx -> Assertions.assertThat(ctx)
                        .hasNotFailed()
                        .getBean(Callable.class).isInstanceOf(TestDependsOnComponent.class));
        depends1 = false;
    }

    @TestConfiguration
    public static class DependsOnMethodBeanConfiguration {
        @Bean
        @DependsOnBeansOfType(type = Trigger.class)
        public Runnable dependentBean() {
            Assert.assertTrue("Bean 'dependentBean' should be configured after all Trigger beans", depends1);
            return () -> {/*NOP*/};
        }
    }

    @Component
    @DependsOnBeansOfType(type = Trigger.class)
    public static class TestDependsOnComponent implements Callable<Void> {
        public TestDependsOnComponent() {
            Assertions.assertThat(depends1).isTrue();
        }

        @Override
        public Void call() {
            return null;
        }
    }

    public static class DependsSimpleTriggerFactoryBean extends SimpleTriggerFactoryBean {
        @Override
        public SimpleTrigger getObject() {
            depends1 = true;
            return super.getObject();
        }
    }

    @TestConfiguration
    public static class DependsOnConfig {
        @Bean
        public static BeanFactoryPostProcessor dependsOnBeansOfTypeBeanFactoryPostProcessor() {
            return new DependsOnBeansOfTypeAnnotationBeanFactoryPostProcessor();
        }
    }

    @TestConfiguration
    @EnableRepeatableQuartzJob(triggerClass = DependsSimpleTriggerFactoryBean.class)
    public static class QuartzJobConfig {

        @RepeatableQuartzJob(repeatInterval = 1000)
        public static class JobClass extends QuartzJobBean {
            @Override
            protected void executeInternal(JobExecutionContext context) {
                /*NOP*/
            }
        }
    }
}
