package com.iwellness.admin_events_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Usuario no autenticado")
public class UsuarioNoAutenticadoException extends Exception{
    
}
