package com.github.leloxo.bankserver.config;

import com.github.leloxo.bankserver.model.validation.ValidationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {
    @Bean
    public ValidationManager validationManager() {
        return new ValidationManager();
    }
}
