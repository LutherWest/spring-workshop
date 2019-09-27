package com.epam.workshop.task.task1._1;

import com.epam.workshop.task.task1.BaseTestCase;
import org.springframework.boot.test.context.TestConfiguration;

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
    public static class JobClass extends BaseJobClass { }
}
