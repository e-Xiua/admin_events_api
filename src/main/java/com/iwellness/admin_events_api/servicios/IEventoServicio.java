package com.iwellness.admin_events_api.servicios;

import java.util.List;


import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.exceptions.UsuarioNoAutenticadoException;

public interface IEventoServicio {
    
    List<Evento> getAllEventos() throws UsuarioNoAutenticadoException;
    Evento getEventoById(Long idEvento);
    Evento crearEvento(Evento evento);
    Evento editarEvento(Evento evento);
    void eliminarEvento(Long idEvento);
    Evento cancelarEvento(Long idEvento);
}
