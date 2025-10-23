package com.iwellness.admin_events_api.config;

import org.springframework.context.annotation.Bean;

import feign.codec.ErrorDecoder;

public class CustomFeignConfiguration {
    
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
