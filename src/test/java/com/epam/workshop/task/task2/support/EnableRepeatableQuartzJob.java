package com.epam.workshop.task.task2.support;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Import(QuartzJobImportBeanDefinitionRegistrar.class)
public @interface EnableRepeatableQuartzJob {

    String[] basePackages() default {};

    Class<? extends SimpleTriggerFactoryBean> triggerClass();
}
