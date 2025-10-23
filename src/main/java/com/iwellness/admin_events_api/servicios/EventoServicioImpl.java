package com.iwellness.admin_events_api.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.iwellness.admin_events_api.email.ServicioEmail;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.repositorios.EventoRepositorio;

import org.springframework.stereotype.Service;

@Service
public class EventoServicioImpl implements IEventoServicio{

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private ServicioEmail servicioEmail;

    @Override
    public List<Evento> getAllEventos() {
        return eventoRepositorio.findAll(); 
    }

    @Override
    public Evento getEventoById(Long idEvento) {
        return eventoRepositorio.findById(idEvento).orElse(null);
    }

    @Override
    public Evento crearEvento(Evento evento) {
        evento.setActivo(true);
        return eventoRepositorio.save(evento);
    }

    @Override
    public Evento editarEvento(Evento evento) {
        return eventoRepositorio.save(evento);
    }

    @Override
    public void eliminarEvento(Long idEvento) {
        eventoRepositorio.deleteById(idEvento);
    }

    @Override
    public Evento cancelarEvento(Long idEvento) {
        Optional<Evento> eventoOpt = eventoRepositorio.findById(idEvento);
        if (eventoOpt.isPresent()){
            Evento evento = eventoOpt.get();
            evento.setActivo(false);
            for (String destinatario : evento.getAsistentes()) {
                servicioEmail.enviarEmailCancelacion(destinatario, evento.getTitulo());
            }
            return eventoRepositorio.save(evento);
        }
        return null;
    }
    
}
