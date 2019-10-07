package com.epam.workshop.task.task1._2;

import com.epam.workshop.task.task1.MetadataReadingUtils;
import org.quartz.Trigger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.Assert;

public class RepeatableQuartzJobBeanDefinitionRegistryPostProcessor
        implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ResourceLoaderAware {
    private Environment environment;
    private ResourceLoader resourceLoader;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Assert.notNull(environment, "Environment must not be null");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");

        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);

        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);

            AnnotationMetadata annotationMetadata =
                    MetadataReadingUtils.retrieveClassAnnotationMetadata(beanDefinition, metadataReaderFactory);

            if (annotationMetadata == null) {
                continue;
            }

            AnnotationAttributes attributes =
                    AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RepeatableQuartzJob.class.getName()));

            if (attributes != null) {
                AbstractBeanDefinition jobDetailBeanDefinition =
                        BeanDefinitionBuilder.rootBeanDefinition(JobDetailFactoryBean.class)
                                .addPropertyValue("name", beanDefinition.getBeanClassName() + "JobDetail")
                                .addPropertyValue("durability", true)
                                .addPropertyValue("requestsRecovery", false)
                                .addPropertyValue("jobClass", beanDefinition.getBeanClassName())
                                .getBeanDefinition();

                final String jobDetailBeanName =
                        BeanDefinitionReaderUtils.registerWithGeneratedName(jobDetailBeanDefinition, registry);

                AbstractBeanDefinition jobTriggerBeanDefinition =
                        BeanDefinitionBuilder.rootBeanDefinition(SimpleTriggerFactoryBean.class)
                                .addPropertyValue("name", beanDefinition.getBeanClassName() + "JobTrigger")
                                .addPropertyValue("misfireInstruction", Trigger.MISFIRE_INSTRUCTION_SMART_POLICY)
                                .addPropertyValue("repeatCount", -1)
                                .addPropertyValue("repeatInterval", attributes.getNumber("repeatInterval").longValue())
                                .addPropertyReference("jobDetail", jobDetailBeanName)
                                .getBeanDefinition();

                BeanDefinitionReaderUtils.registerWithGeneratedName(jobTriggerBeanDefinition, registry);
            }
        }

        metadataReaderFactory.clearCache();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        /*NOP*/
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
