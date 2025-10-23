package com.iwellness.admin_events_api.servicios;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.email.ServicioEmail;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.entidades.TipoEvento;
import com.iwellness.admin_events_api.exceptions.EventoNotFoundException;
import com.iwellness.admin_events_api.repositorios.EventoRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventoServicioImplTest {

    @Mock
    private ServicioEmail servicioEmail;

    @Mock
    private EventoRepositorio eventoRepositorio;

    @InjectMocks
    private EventoServicioImpl eventoServicio;

    @Test
    void getAllEventos() {
        when(eventoRepositorio.findAll()).thenReturn(List.of(new Evento(), new Evento()));
        assertEquals(2, eventoServicio.getAllEventos().size());
    }

    @Test
    void getEventoById() {
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(new Evento(1L,"titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true)));
        assertEquals(1L, eventoServicio.getEventoById(1L).getId());

        assertNull(eventoServicio.getEventoById(2L));
    }

    @Test
    void crearEvento() {
        Evento eventoCrear = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true);
        when(eventoRepositorio.save(any())).thenReturn(eventoCrear);

        Evento eventoCreado = eventoServicio.crearEvento(new Evento());
        assertNotNull(eventoCreado);
        assertEquals(eventoCrear.getId(), eventoCreado.getId());
    }

    @Test
    void editarEvento() {
        Evento eventoEditar = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true);
        when(eventoRepositorio.save(any())).thenReturn(eventoEditar);

        Evento eventoEditado = eventoServicio.editarEvento(new Evento());
        assertNotNull(eventoEditado);
        assertEquals(eventoEditar.getId(), eventoEditado.getId());
    }

    @Test
    void editarParcialEventoCancelarTest() {
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(new Evento(1L,"titulo", "descripcion", new Date(),
                2L, 1000L, List.of("email1", "email2"), TipoEvento.EVENTO, "rojo", true)));
        Mockito.doNothing().when(servicioEmail).enviarEmailCancelacion(any(),any());
        Evento eventoCancelado = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of("email1", "email2"), TipoEvento.EVENTO, "rojo", false);
        when(eventoRepositorio.save(any())).thenReturn(eventoCancelado);

        eventoCancelado = eventoServicio.editarParcialEvento(1L, Map.of("activo", false));
        assertNotNull(eventoCancelado);
        assertFalse(eventoCancelado.getActivo());

        assertNull(eventoServicio.getEventoById(2L));
    }

    @Test
    void editarParcialEventoTest() {
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(new Evento(1L,"titulo", "descripcion", new Date(),
                2L, 1000L, List.of("email1", "email2"), TipoEvento.EVENTO, "rojo", true)));
        Mockito.doNothing().when(servicioEmail).enviarEmailModificacion(any(),any());
        Evento eventoEditado = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of("email1", "email2"), TipoEvento.EVENTO, "rojo", false);
        when(eventoRepositorio.save(any())).thenReturn(eventoEditado);

        eventoEditado = eventoServicio.editarParcialEvento(1L, Map.of("titulo", "tituloEditado"));
        assertNotNull(eventoEditado);
        assertFalse(eventoEditado.getActivo());

        assertNull(eventoServicio.getEventoById(2L));
    }
}