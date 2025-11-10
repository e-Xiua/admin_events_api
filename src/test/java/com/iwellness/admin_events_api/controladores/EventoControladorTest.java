package com.iwellness.admin_events_api.controladores;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import java.util.*;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.entidades.TipoEvento;
import com.iwellness.admin_events_api.exceptions.EventoNotFoundException;
import com.iwellness.admin_events_api.exceptions.FormatoFechaInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iwellness.admin_events_api.exceptions.UsuarioNoAutenticadoException;
import com.iwellness.admin_events_api.exceptions.UsuarioNoAutorizadoPorRolException;
import com.iwellness.admin_events_api.seguridad.SeguridadEventos;
import com.iwellness.admin_events_api.servicios.EventoServicioImpl;

@ExtendWith(MockitoExtension.class)
public class EventoControladorTest {

    @Mock
    private EventoServicioImpl eventoServicioImpl;

    @Mock
    private SeguridadEventos seguridadEventos;

    @InjectMocks
    private EventoControlador eventoControlador;

    @Test
    public void getEventosTest() throws UsuarioNoAutenticadoException, UsuarioNoAutorizadoPorRolException{
        //Mockito.doNothing().when(seguridadEventos).validarRol();
        when(eventoServicioImpl.getAllEventos()).thenReturn(List.of(new Evento(), new Evento()));
        assertEquals(2, eventoControlador.getAllEventos().size());
    }

    @Test
    public void getgetEventoByIdTest() throws UsuarioNoAutenticadoException, UsuarioNoAutorizadoPorRolException, EventoNotFoundException {
        //Mockito.doNothing().when(seguridadEventos).validarRol();
        when(eventoServicioImpl.getEventoById(1L)).thenReturn(new Evento(1L,"titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true));
        assertEquals(1L, eventoControlador.getEventoById(1L).getId());

        assertThrows(EventoNotFoundException.class, () -> eventoControlador.getEventoById(2L));
    }

    @Test
    public void creaEventoTest() throws UsuarioNoAutorizadoPorRolException, FormatoFechaInvalidoException {
        //Mockito.doNothing().when(seguridadEventos).validarRol();
        Evento eventoCrear = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true);
        when(eventoServicioImpl.crearEvento(any())).thenReturn(eventoCrear);

        EventoDTO eventoCreado = eventoControlador.creaEvento(new EventoDTO());
        assertNotNull(eventoCreado);
        assertEquals(eventoCrear.getId(), eventoCreado.getId());
    }

    @Test
    public void editarEventoTest() throws UsuarioNoAutorizadoPorRolException, FormatoFechaInvalidoException {
        //Mockito.doNothing().when(seguridadEventos).validarRol();
        Evento eventoEditar = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true);
        when(eventoServicioImpl.editarEvento(any())).thenReturn(eventoEditar);

        EventoDTO eventoEditado = eventoControlador.editarEvento(new EventoDTO());
        assertNotNull(eventoEditado);
        assertEquals(eventoEditar.getId(), eventoEditado.getId());
    }

    @Test
    public void editarParcialEventoTest() throws UsuarioNoAutorizadoPorRolException, EventoNotFoundException, FormatoFechaInvalidoException {
        //Mockito.doNothing().when(seguridadEventos).validarRol();
        Evento evento = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", false);
        Map<String, Object> atributosAeditar = new HashMap<>();
        when(eventoServicioImpl.editarParcialEvento(1L, atributosAeditar)).thenReturn(evento);

        EventoDTO eventoeditado = eventoControlador.editarParcialEvento(1L, atributosAeditar);
        assertNotNull(eventoeditado);
        assertFalse(eventoeditado.getActivo());

        assertThrows(EventoNotFoundException.class, () -> eventoControlador.editarParcialEvento(2L, new HashMap<>()));
    }
}
