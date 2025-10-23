package com.iwellness.admin_events_api.controladores;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.exceptions.EventoNotFoundException;
import com.iwellness.admin_events_api.exceptions.UsuarioNoAutenticadoException;
import com.iwellness.admin_events_api.exceptions.UsuarioNoAutorizadoPorRolException;
import com.iwellness.admin_events_api.mapper.EventoMapper;
import com.iwellness.admin_events_api.seguridad.ISeguridad;
import com.iwellness.admin_events_api.servicios.IEventoServicio;

@RestController
@RequestMapping("/evento")
@CrossOrigin(origins = "*")
public class EventoControlador {

    @Autowired
    private IEventoServicio eventoServicio;

    @Autowired
    @Qualifier("SeguridadEventos")
    private ISeguridad seguridadEventos;

    @GetMapping
    public List<EventoDTO> getAllEventos() throws UsuarioNoAutenticadoException, UsuarioNoAutorizadoPorRolException{
        //seguridadEventos.validarRol();
        return eventoServicio.getAllEventos()
            .stream()
            .map(evento -> EventoMapper.eventoToEventoDto(evento))
            .toList();
    }

    @GetMapping(value = "/{id}")
    public EventoDTO getEventoById(@PathVariable("id") Long id) throws EventoNotFoundException, UsuarioNoAutorizadoPorRolException{
        //seguridadEventos.validarRol();
        Evento evento = eventoServicio.getEventoById(id);
        if (evento == null){
            throw new EventoNotFoundException();
        }
        return EventoMapper.eventoToEventoDto(evento);
    }

    @PostMapping
    public EventoDTO creaEvento(@RequestBody EventoDTO eventoDto) throws UsuarioNoAutorizadoPorRolException{
        //seguridadEventos.validarRol();
        Evento eventoDtoCrear = EventoMapper.eventoDtoToEvento(eventoDto);
        return EventoMapper.eventoToEventoDto(eventoServicio.crearEvento(eventoDtoCrear));
    }

    @PutMapping
    public EventoDTO editarEvento(@RequestBody EventoDTO eventoDto) throws UsuarioNoAutorizadoPorRolException{
        //seguridadEventos.validarRol();
        Evento eventoDtoCrear = EventoMapper.eventoDtoToEvento(eventoDto);
        return EventoMapper.eventoToEventoDto(eventoServicio.editarEvento(eventoDtoCrear));
    }

    @DeleteMapping(value = "/{id}")
    public void eliminarEvento(@PathVariable("id") Long id) throws UsuarioNoAutorizadoPorRolException{
        seguridadEventos.validarRol();
        eventoServicio.eliminarEvento(id);
    }

    @PatchMapping(value = "/{id}")
    public EventoDTO editarParcialEvento(@PathVariable("id") Long id, @RequestBody Map<String, Object> editados) throws EventoNotFoundException, UsuarioNoAutorizadoPorRolException{
        //seguridadEventos.validarRol();
        Evento evento = eventoServicio.editarParcialEvento(id, editados);
        if (evento == null){
            throw new EventoNotFoundException();
        }
        return EventoMapper.eventoToEventoDto(evento);
    }
    
}
