package com.iwellness.admin_events_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Formato de fecha invalido")
public class FormatoFechaInvalidoException extends Exception{
    
}
