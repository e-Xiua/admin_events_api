package com.iwellness.admin_events_api.controladores;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.*;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.entidades.TipoEvento;
import com.iwellness.admin_events_api.exceptions.EventoNotFoundException;
import com.iwellness.admin_events_api.exceptions.FormatoFechaInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iwellness.admin_events_api.exceptions.UsuarioNoAutorizadoPorRolException;
import com.iwellness.admin_events_api.seguridad.SeguridadEventos;
import com.iwellness.admin_events_api.servicios.EventoServicioImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Controlador de Eventos")
public class EventoControladorTest {

    @Mock
    private EventoServicioImpl eventoServicioImpl;

    @Mock
    private SeguridadEventos seguridadEventos;

    @InjectMocks
    private EventoControlador eventoControlador;

    private Evento eventoEjemplo;
    private Date fechaEjemplo;

    @BeforeEach
    void setUp() {
        fechaEjemplo = new Date();
        eventoEjemplo = new Evento(
                1L, 
                "Evento de prueba", 
                "Descripción de prueba", 
                fechaEjemplo,
                120L, 
                5000L, 
                Arrays.asList("test@example.com"), 
                TipoEvento.EVENTO, 
                "azul", 
                true
        );
    }

    @Test
    @DisplayName("Debe retornar todos los eventos exitosamente")
    public void getAllEventos_DebeRetornarListaDeEventos() throws Exception {
        // Arrange
        List<Evento> eventos = Arrays.asList(eventoEjemplo, 
                new Evento(2L, "Evento 2", "Desc 2", fechaEjemplo, 60L, 3000L, 
                        List.of(), TipoEvento.REUNION, "verde", true));
        when(eventoServicioImpl.getAllEventos()).thenReturn(eventos);

        // Act
        List<EventoDTO> resultado = eventoControlador.getAllEventos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Evento de prueba", resultado.get(0).getTitulo());
        verify(eventoServicioImpl, times(1)).getAllEventos();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay eventos")
    public void getAllEventos_DebeRetornarListaVacia_CuandoNoHayEventos() throws Exception {
        // Arrange
        when(eventoServicioImpl.getAllEventos()).thenReturn(Collections.emptyList());

        // Act
        List<EventoDTO> resultado = eventoControlador.getAllEventos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(eventoServicioImpl, times(1)).getAllEventos();
    }

    @Test
    @DisplayName("Debe retornar un evento por ID exitosamente")
    public void getEventoById_DebeRetornarEvento_CuandoExiste() throws Exception {
        // Arrange
        when(eventoServicioImpl.getEventoById(1L)).thenReturn(eventoEjemplo);

        // Act
        EventoDTO resultado = eventoControlador.getEventoById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Evento de prueba", resultado.getTitulo());
        assertEquals("Descripción de prueba", resultado.getDescripcion());
        assertTrue(resultado.getActivo());
        verify(eventoServicioImpl, times(1)).getEventoById(1L);
    }

    @Test
    @DisplayName("Debe lanzar EventoNotFoundException cuando el evento no existe")
    public void getEventoById_DebeLanzarExcepcion_CuandoEventoNoExiste() {
        // Arrange
        when(eventoServicioImpl.getEventoById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(EventoNotFoundException.class, () -> {
            eventoControlador.getEventoById(999L);
        });
        verify(eventoServicioImpl, times(1)).getEventoById(999L);
    }

    @Test
    @DisplayName("Debe crear un evento exitosamente")
    public void creaEvento_DebeCrearEventoExitosamente() throws Exception {
        // Arrange
        EventoDTO eventoDTO = new EventoDTO(
                null, 
                "Nuevo Evento", 
                "Nueva descripción", 
                "2024-12-25T10:00:00.000Z",
                90L, 
                4000L, 
                Arrays.asList("nuevo@example.com"), 
                TipoEvento.EVENTO, 
                "rojo", 
                null
        );
        when(eventoServicioImpl.crearEvento(any(Evento.class))).thenReturn(eventoEjemplo);

        // Act
        EventoDTO resultado = eventoControlador.creaEvento(eventoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertTrue(resultado.getActivo());
        verify(eventoServicioImpl, times(1)).crearEvento(any(Evento.class));
    }

    @Test
    @DisplayName("Debe editar un evento exitosamente")
    public void editarEvento_DebeEditarEventoExitosamente() throws Exception {
        // Arrange
        EventoDTO eventoDTO = new EventoDTO(
                1L, 
                "Evento Editado", 
                "Descripción editada", 
                "2024-12-25T10:00:00.000Z",
                150L, 
                6000L, 
                Arrays.asList("editado@example.com"), 
                TipoEvento.REUNION, 
                "amarillo", 
                true
        );
        Evento eventoEditado = new Evento(
                1L, 
                "Evento Editado", 
                "Descripción editada", 
                fechaEjemplo,
                150L, 
                6000L, 
                Arrays.asList("editado@example.com"), 
                TipoEvento.REUNION, 
                "amarillo", 
                true
        );
        when(eventoServicioImpl.editarEvento(any(Evento.class))).thenReturn(eventoEditado);

        // Act
        EventoDTO resultado = eventoControlador.editarEvento(eventoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Evento Editado", resultado.getTitulo());
        verify(eventoServicioImpl, times(1)).editarEvento(any(Evento.class));
    }

    @Test
    @DisplayName("Debe editar parcialmente un evento exitosamente")
    public void editarParcialEvento_DebeEditarCamposEspecificos() throws Exception {
        // Arrange
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("titulo", "Título modificado");
        cambios.put("color", "verde");
        
        Evento eventoModificado = new Evento(
                1L, 
                "Título modificado", 
                "Descripción de prueba", 
                fechaEjemplo,
                120L, 
                5000L, 
                Arrays.asList("test@example.com"), 
                TipoEvento.EVENTO, 
                "verde", 
                true
        );
        when(eventoServicioImpl.editarParcialEvento(eq(1L), eq(cambios))).thenReturn(eventoModificado);

        // Act
        EventoDTO resultado = eventoControlador.editarParcialEvento(1L, cambios);

        // Assert
        assertNotNull(resultado);
        assertEquals("Título modificado", resultado.getTitulo());
        assertEquals("verde", resultado.getColor());
        verify(eventoServicioImpl, times(1)).editarParcialEvento(1L, cambios);
    }

    @Test
    @DisplayName("Debe desactivar un evento al editarlo parcialmente")
    public void editarParcialEvento_DebeDesactivarEvento() throws Exception {
        // Arrange
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("activo", false);
        
        Evento eventoDesactivado = new Evento(
                1L, 
                "Evento de prueba", 
                "Descripción de prueba", 
                fechaEjemplo,
                120L, 
                5000L, 
                Arrays.asList("test@example.com"), 
                TipoEvento.EVENTO, 
                "azul", 
                false
        );
        when(eventoServicioImpl.editarParcialEvento(eq(1L), eq(cambios))).thenReturn(eventoDesactivado);

        // Act
        EventoDTO resultado = eventoControlador.editarParcialEvento(1L, cambios);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
        verify(eventoServicioImpl, times(1)).editarParcialEvento(1L, cambios);
    }

    @Test
    @DisplayName("Debe lanzar excepción al editar parcialmente un evento inexistente")
    public void editarParcialEvento_DebeLanzarExcepcion_CuandoEventoNoExiste() 
            throws FormatoFechaInvalidoException {
        // Arrange
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("titulo", "Nuevo título");
        when(eventoServicioImpl.editarParcialEvento(eq(999L), any())).thenReturn(null);

        // Act & Assert
        assertThrows(EventoNotFoundException.class, () -> {
            eventoControlador.editarParcialEvento(999L, cambios);
        });
        verify(eventoServicioImpl, times(1)).editarParcialEvento(eq(999L), any());
    }

    @Test
    @DisplayName("Debe eliminar un evento exitosamente")
    public void eliminarEvento_DebeEliminarEventoExitosamente() throws UsuarioNoAutorizadoPorRolException {
        // Arrange
        doNothing().when(seguridadEventos).validarRol();
        doNothing().when(eventoServicioImpl).eliminarEvento(1L);

        // Act
        eventoControlador.eliminarEvento(1L);

        // Assert
        verify(seguridadEventos, times(1)).validarRol();
        verify(eventoServicioImpl, times(1)).eliminarEvento(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar sin autorización")
    public void eliminarEvento_DebeLanzarExcepcion_SinAutorizacion() throws UsuarioNoAutorizadoPorRolException {
        // Arrange
        doThrow(new UsuarioNoAutorizadoPorRolException()).when(seguridadEventos).validarRol();

        // Act & Assert
        assertThrows(UsuarioNoAutorizadoPorRolException.class, () -> {
            eventoControlador.eliminarEvento(1L);
        });
        verify(seguridadEventos, times(1)).validarRol();
        verify(eventoServicioImpl, never()).eliminarEvento(anyLong());
    }
}
