package com.iwellness.admin_events_api.seguridad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.iwellness.admin_events_api.clientes.UsuarioFeignCliente;
import com.iwellness.admin_events_api.dto.UsuarioDTO;
import com.iwellness.admin_events_api.exceptions.UsuarioNoAutorizadoPorRolException;

@Service
@Qualifier("SeguridadEventos")
public class SeguridadEventos implements ISeguridad{

    @Autowired
    private UsuarioFeignCliente usuarioFeignCliente;
    
    private final List<String> rolesAutorizados = List.of("Admin");

    public void validarRol() throws UsuarioNoAutorizadoPorRolException{
        UsuarioDTO usuarioDTO = usuarioFeignCliente.getUsuario();
        if(!rolesAutorizados.contains(usuarioDTO.getRol().getNombre())){
            throw new UsuarioNoAutorizadoPorRolException();
        }
    }
    
}
