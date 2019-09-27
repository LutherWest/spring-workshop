package com.epam.workshop.task.task2;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

public class DependsOnBeansOfTypeAnnotationTest {
    private static boolean depends1;

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DependsOnConfig.class));

    @Test
    public void shouldSetupDependsOnBeansOfTypeOnMethod() {
        runner.withUserConfiguration(DependsOnMethodBeanConfiguration.class, RunnableConfiguration.class)
                .run(ctx -> Assertions.assertThat(ctx)
                        .hasNotFailed()
                        .getBean("dependentBean").isInstanceOf(Runnable.class));
        depends1 = false;
    }

    @Test
    public void shouldSetupDependsOnBeansOfTypeOnClass() {
        runner.withUserConfiguration(TestDependsOnComponent.class, RunnableConfiguration.class)
                .run(ctx -> Assertions.assertThat(ctx)
                        .hasNotFailed()
                        .getBean(Callable.class).isInstanceOf(TestDependsOnComponent.class));
        depends1 = false;
    }

    @Test
    public void shouldSetupDependsOnBeansOfTypeWithExcludingNames() {
        runner.withUserConfiguration(WithExclusionsConfig.class, RunnableConfiguration.class)
                .run(ctx -> Assertions.assertThat(ctx)
                        .hasNotFailed()
                        .getBean("dependentBean").isInstanceOf(Runnable.class));
        depends1 = false;
    }

    @TestConfiguration
    public static class DependsOnMethodBeanConfiguration {
        @Bean
        @DependsOnBeansOfType(type = Runnable.class)
        public Runnable dependentBean() {
            Assert.assertTrue("Bean 'dependentBean' should be configured after all Runnable beans", depends1);
            return () -> {/*NOP*/};
        }
    }

    @TestConfiguration
    public static class RunnableConfiguration {
        @Bean
        public Runnable runnable() {
            depends1 = true;
            return () -> {/*NOP*/};
        }
    }

    @TestConfiguration
    public static class WithExclusionsConfig {
        @Bean
        @DependsOnBeansOfType(type = Runnable.class, excludingNames = "runnable")
        public Runnable dependentBean() {
            Assert.assertFalse("Bean 'dependentBean' should be configured before 'runnable' bean", depends1);
            return () -> {/*NOP*/};
        }
    }

    @Component
    @DependsOnBeansOfType(type = Runnable.class)
    public static class TestDependsOnComponent implements Callable<Void> {
        public TestDependsOnComponent() {
            Assertions.assertThat(depends1).isTrue();
        }

        @Override
        public Void call() {
            return null;
        }
    }

    @TestConfiguration
    public static class DependsOnConfig {
        @Bean
        public static BeanFactoryPostProcessor dependsOnBeansOfTypeBeanFactoryPostProcessor() {
            return new DependsOnBeansOfTypeAnnotationBeanFactoryPostProcessor();
        }
    }
}
