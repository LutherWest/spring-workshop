package com.epam.workshop.task.task2;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class DependsOnBeansOfTypeAnnotationBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    private static final String TYPE_PROPERTY = "type";
    private static final String EXCLUDE_NAME_PROPERTY = "excludingNames";

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);

            BiConsumer<Class<?>, List<String>> dependsOnBeansOfTypeHandler = (type, excludeNames) ->
                Arrays.stream(beanFactory.getBeanNamesForType(type))
                        .filter(name -> !(excludeNames.contains(name) || beanName.equals(name)))
                        .forEach(name -> {
                            String[] dependencies = definition.getDependsOn();
                            dependencies = StringUtils.addStringToArray(dependencies, name);
                            definition.setDependsOn(dependencies);
                        });

            // try to find class level annotation
            Optional.ofNullable(beanFactory.getType(beanName))
                    .map(cls -> AnnotatedElementUtils.findMergedAnnotation(cls, DependsOnBeansOfType.class))
                    .ifPresent(annotation -> dependsOnBeansOfTypeHandler.accept(annotation.type(),
                            Arrays.asList(annotation.excludingNames())));

            if (definition instanceof AnnotatedBeanDefinition) {
                // scanning of method annotations
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) definition;
                Optional.ofNullable(beanDefinition.getFactoryMethodMetadata())
                        .map(meta -> meta.getAnnotationAttributes(DependsOnBeansOfType.class.getName()))
                        .map(AnnotationAttributes::new)
                        .ifPresent(attrs -> dependsOnBeansOfTypeHandler.accept(attrs.getClass(TYPE_PROPERTY),
                                Arrays.asList(attrs.getStringArray(EXCLUDE_NAME_PROPERTY))));
            }
        }
    }
}
