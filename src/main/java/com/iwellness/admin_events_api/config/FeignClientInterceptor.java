package com.iwellness.admin_events_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Autowired
    private HttpServletRequest request;

    private static final Logger logger = LoggerFactory.getLogger(FeignClientInterceptor.class);

    @Override
    public void apply(RequestTemplate template) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            template.header("Authorization", token);
            logger.info("Token JWT añadido a la solicitud Feign: " + token);
        } else {
            logger.warn("No se encontró token JWT para enviar.");
        }
    }


}
