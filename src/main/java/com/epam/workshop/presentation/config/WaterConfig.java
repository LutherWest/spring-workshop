package com.epam.workshop.presentation.config;

import com.epam.workshop.presentation.service.SpecialAnnotation;
import com.epam.workshop.presentation.service.Water;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WaterConfig {

    @Bean
    @SpecialAnnotation("and here")
    public Water waterBean() {
        return new Water();
    }

    public static class NestedConfig {

        @Bean
        public Runnable runnable() {
            return () -> {};
        }
    }
}
