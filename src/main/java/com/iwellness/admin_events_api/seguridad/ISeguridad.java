package com.iwellness.admin_events_api.seguridad;

import com.iwellness.admin_events_api.exceptions.UsuarioNoAutorizadoPorRolException;

public interface ISeguridad {
    void validarRol() throws UsuarioNoAutorizadoPorRolException;
}
