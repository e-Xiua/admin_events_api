package com.iwellness.admin_events_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Usuario no tiene rol para acceder al recurso")
public class UsuarioNoAutorizadoPorRolException extends Exception{
    
}
