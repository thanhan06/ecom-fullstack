package com.vu.api.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;

/**
 * Class JacksonConfig to config JsonNullableModule
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }
}
