package com.iwellness.admin_events_api.config;

import com.iwellness.admin_events_api.exceptions.UsuarioNoAutenticadoException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder{

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 401){
            return new UsuarioNoAutenticadoException();
        }
        return defaultErrorDecoder.decode(methodKey, response);
    } 
}
