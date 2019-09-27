package com.epam.workshop.presentation.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Import(SpecialImportSelector.class)
public @interface EnableMyImportSelector {
    String[] value();
    int intValue();
}
