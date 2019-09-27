package com.epam.workshop.presentation.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;

public class SpecialImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        final AnnotationAttributes attrs = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(
                        EnableMyImportSelector.class.getName()
                )
        );
        System.out.println(Arrays.toString(attrs.getStringArray("value"))); // [1, 2]
        System.out.println(attrs.getNumber("intValue")); // 100
        return new String[0];
    }
}
