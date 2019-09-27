package com.epam.workshop.task.task1._2;

import com.epam.workshop.task.task1.BaseTestCase;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class BeanDefinitionRegistryPostProcessorBasedConfigurationTest extends BaseTestCase {

    @Override
    protected Class<?>[] getUserConfigurations() {
        return new Class[] { Config.class, JobClass.class };
    }

    @TestConfiguration
    public static class Config {

        @Bean
        public static BeanDefinitionRegistryPostProcessor bdrpp() {
            return new RepeatableQuartzJobBeanDefinitionRegistryPostProcessor();
        }
    }

    @RepeatableQuartzJob(repeatInterval = 2000)
    public static class JobClass extends BaseJobClass { }
}
