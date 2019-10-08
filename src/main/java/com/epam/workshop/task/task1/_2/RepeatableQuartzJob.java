package com.epam.workshop.task.task1._2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatableQuartzJob {
    long repeatInterval();
}
