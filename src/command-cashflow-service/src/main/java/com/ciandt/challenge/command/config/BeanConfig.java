package com.ciandt.challenge.command.config;

import com.ciandt.challenge.command.service.RecordService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public RecordService recordService(){
        return new RecordService();
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                // Add custom configurations here, e.g.,
                // .setDateFormat("yyyy-MM-dd HH:mm:ss")
                // .excludeFieldsWithoutExposeAnnotation()
                .create();
    }
}
