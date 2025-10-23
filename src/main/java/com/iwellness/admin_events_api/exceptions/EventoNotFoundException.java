package com.iwellness.admin_events_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Evento Not Found")
public class EventoNotFoundException extends Exception{
    
}
