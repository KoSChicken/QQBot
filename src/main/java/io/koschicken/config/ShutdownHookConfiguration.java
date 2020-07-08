package io.koschicken.config;

import io.koschicken.service.SpringShutDownHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShutdownHookConfiguration {

    @Bean(destroyMethod = "destroy")
    public SpringShutDownHook initializeShutDownHook() {
        return new SpringShutDownHook();
    }
}