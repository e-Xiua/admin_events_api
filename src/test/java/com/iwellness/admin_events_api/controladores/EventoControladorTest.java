package com.iwellness.admin_events_api.controladores;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.entidades.TipoEvento;
import com.iwellness.admin_events_api.exceptions.EventoNotFoundException;
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
        Mockito.doNothing().when(seguridadEventos).validarRol();
        when(eventoServicioImpl.getAllEventos()).thenReturn(List.of(new Evento(), new Evento()));
        assertEquals(2, eventoControlador.getAllEventos().size());
    }

    @Test
    public void getgetEventoByIdTest() throws UsuarioNoAutenticadoException, UsuarioNoAutorizadoPorRolException, EventoNotFoundException {
        Mockito.doNothing().when(seguridadEventos).validarRol();
        when(eventoServicioImpl.getEventoById(1L)).thenReturn(new Evento(1L,"titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true));
        assertEquals(1L, eventoControlador.getEventoById(1L).getId());

        assertThrows(EventoNotFoundException.class, () -> eventoControlador.getEventoById(2L));
    }

    @Test
    public void creaEventoTest() throws UsuarioNoAutorizadoPorRolException{
        Mockito.doNothing().when(seguridadEventos).validarRol();
        Evento eventoCrear = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true);
        when(eventoServicioImpl.crearEvento(any())).thenReturn(eventoCrear);

        EventoDTO eventoCreado = eventoControlador.creaEvento(new EventoDTO());
        assertNotNull(eventoCreado);
        assertEquals(eventoCrear.getId(), eventoCreado.getId());
    }

    @Test
    public void editarEventoTest() throws UsuarioNoAutorizadoPorRolException{
        Mockito.doNothing().when(seguridadEventos).validarRol();
        Evento eventoEditar = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", true);
        when(eventoServicioImpl.editarEvento(any())).thenReturn(eventoEditar);

        EventoDTO eventoEditado = eventoControlador.editarEvento(new EventoDTO());
        assertNotNull(eventoEditado);
        assertEquals(eventoEditar.getId(), eventoEditado.getId());
    }

    @Test
    public void cancelarEventoTest() throws UsuarioNoAutorizadoPorRolException, EventoNotFoundException {
        Mockito.doNothing().when(seguridadEventos).validarRol();
        Evento eventoCancelar = new Evento(1L, "titulo", "descripcion", new Date(),
                2L, 1000L, List.of(), TipoEvento.EVENTO, "rojo", false);
        when(eventoServicioImpl.cancelarEvento(1L)).thenReturn(eventoCancelar);

        EventoDTO eventoCancelado = eventoControlador.cancelarEvento(1L);
        assertNotNull(eventoCancelado);
        assertFalse(eventoCancelado.getActivo());

        assertThrows(EventoNotFoundException.class, () -> eventoControlador.cancelarEvento(2L));
    }
}
