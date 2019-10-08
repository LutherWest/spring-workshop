package com.epam.workshop.task.task1._1;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RepeatableQuartzJob {
    long repeatInterval();
}
