package com.epam.workshop.task.task1;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MetadataReadingUtils {

    public static AnnotationMetadata retrieveClassAnnotationMetadata(BeanDefinition bd, MetadataReaderFactory mrf) {
        String className = bd.getBeanClassName();
        if (className == null || bd.getFactoryMethodName() != null) {
            return null;
        }

        if (bd instanceof AnnotatedBeanDefinition &&
                className.equals(((AnnotatedBeanDefinition) bd).getMetadata().getClassName())) {
            // Can reuse the pre-parsed metadata from the given BeanDefinition
            return ((AnnotatedBeanDefinition) bd).getMetadata();
        } else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
            // Check already loaded Class if present...
            // since we possibly can't even load the class file for this Class.
            return new StandardAnnotationMetadata(((AbstractBeanDefinition) bd).getBeanClass(), true);
        } else {
            try {
                // and finally we should inspect this class manually
                return mrf.getMetadataReader(className).getAnnotationMetadata();
            } catch (IOException ex) {
                throw new BeanDefinitionStoreException("I/O failure during classpath access for class - " + className, ex);
            }
        }
    }
}
