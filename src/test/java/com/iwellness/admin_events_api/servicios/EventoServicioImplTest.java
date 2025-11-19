package com.iwellness.admin_events_api.servicios;

import com.iwellness.admin_events_api.email.ServicioEmail;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.entidades.TipoEvento;
import com.iwellness.admin_events_api.exceptions.FormatoFechaInvalidoException;
import com.iwellness.admin_events_api.repositorios.EventoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Servicio de Eventos")
class EventoServicioImplTest {

    @Mock
    private ServicioEmail servicioEmail;

    @Mock
    private EventoRepositorio eventoRepositorio;

    @InjectMocks
    private EventoServicioImpl eventoServicio;

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
                Arrays.asList("test1@example.com", "test2@example.com"), 
                TipoEvento.EVENTO, 
                "azul", 
                true
        );
    }

    @Test
    @DisplayName("Debe retornar todos los eventos")
    void getAllEventos_DebeRetornarTodosLosEventos() {
        // Arrange
        List<Evento> eventos = Arrays.asList(eventoEjemplo, 
                new Evento(2L, "Evento 2", "Desc 2", fechaEjemplo, 60L, 3000L, 
                        List.of(), TipoEvento.REUNION, "verde", true));
        when(eventoRepositorio.findAll()).thenReturn(eventos);

        // Act
        List<Evento> resultado = eventoServicio.getAllEventos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Evento de prueba", resultado.get(0).getTitulo());
        verify(eventoRepositorio, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay eventos")
    void getAllEventos_DebeRetornarListaVacia_CuandoNoHayEventos() {
        // Arrange
        when(eventoRepositorio.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Evento> resultado = eventoServicio.getAllEventos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(eventoRepositorio, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar un evento por ID cuando existe")
    void getEventoById_DebeRetornarEvento_CuandoExiste() {
        // Arrange
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(eventoEjemplo));

        // Act
        Evento resultado = eventoServicio.getEventoById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Evento de prueba", resultado.getTitulo());
        verify(eventoRepositorio, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe retornar null cuando el evento no existe")
    void getEventoById_DebeRetornarNull_CuandoEventoNoExiste() {
        // Arrange
        when(eventoRepositorio.findById(999L)).thenReturn(Optional.empty());

        // Act
        Evento resultado = eventoServicio.getEventoById(999L);

        // Assert
        assertNull(resultado);
        verify(eventoRepositorio, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear un evento y establecer activo en true")
    void crearEvento_DebeCrearEventoYEstablecerActivoTrue() {
        // Arrange
        Evento eventoNuevo = new Evento(
                null, 
                "Nuevo Evento", 
                "Nueva descripción", 
                fechaEjemplo,
                90L, 
                4000L, 
                List.of(), 
                TipoEvento.EVENTO, 
                "rojo", 
                null
        );
        Evento eventoGuardado = new Evento(
                1L, 
                "Nuevo Evento", 
                "Nueva descripción", 
                fechaEjemplo,
                90L, 
                4000L, 
                List.of(), 
                TipoEvento.EVENTO, 
                "rojo", 
                true
        );
        when(eventoRepositorio.save(any(Evento.class))).thenReturn(eventoGuardado);

        // Act
        Evento resultado = eventoServicio.crearEvento(eventoNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertTrue(resultado.getActivo());
        verify(eventoRepositorio, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Debe editar un evento exitosamente")
    void editarEvento_DebeEditarEventoExitosamente() {
        // Arrange
        Evento eventoEditado = new Evento(
                1L, 
                "Evento Editado", 
                "Descripción editada", 
                fechaEjemplo,
                150L, 
                6000L, 
                Arrays.asList("nuevo@example.com"), 
                TipoEvento.REUNION, 
                "amarillo", 
                true
        );
        when(eventoRepositorio.save(any(Evento.class))).thenReturn(eventoEditado);

        // Act
        Evento resultado = eventoServicio.editarEvento(eventoEditado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Evento Editado", resultado.getTitulo());
        assertEquals(150L, resultado.getDuracion());
        verify(eventoRepositorio, times(1)).save(eventoEditado);
    }

    @Test
    @DisplayName("Debe eliminar un evento exitosamente")
    void eliminarEvento_DebeEliminarEventoExitosamente() {
        // Arrange
        doNothing().when(eventoRepositorio).deleteById(1L);

        // Act
        eventoServicio.eliminarEvento(1L);

        // Assert
        verify(eventoRepositorio, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe cancelar evento y enviar emails de cancelación")
    void editarParcialEvento_DebeCancelarEventoYEnviarEmails() throws FormatoFechaInvalidoException {
        // Arrange
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("activo", false);
        
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(eventoEjemplo));
        doNothing().when(servicioEmail).enviarEmailCancelacion(anyString(), anyString());
        
        Evento eventoCancelado = new Evento(
                1L, 
                "Evento de prueba", 
                "Descripción de prueba", 
                fechaEjemplo,
                120L, 
                5000L, 
                Arrays.asList("test1@example.com", "test2@example.com"), 
                TipoEvento.EVENTO, 
                "azul", 
                false
        );
        when(eventoRepositorio.save(any(Evento.class))).thenReturn(eventoCancelado);

        // Act
        Evento resultado = eventoServicio.editarParcialEvento(1L, cambios);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
        verify(eventoRepositorio, times(1)).findById(1L);
        verify(servicioEmail, times(2)).enviarEmailCancelacion(anyString(), eq("Evento de prueba"));
        verify(eventoRepositorio, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Debe modificar evento y enviar emails de modificación")
    void editarParcialEvento_DebeModificarEventoYEnviarEmails() throws FormatoFechaInvalidoException {
        // Arrange
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("titulo", "Título modificado");
        cambios.put("color", "verde");
        
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(eventoEjemplo));
        doNothing().when(servicioEmail).enviarEmailModificacion(anyString(), anyString());
        
        Evento eventoModificado = new Evento(
                1L, 
                "Título modificado", 
                "Descripción de prueba", 
                fechaEjemplo,
                120L, 
                5000L, 
                Arrays.asList("test1@example.com", "test2@example.com"), 
                TipoEvento.EVENTO, 
                "verde", 
                true
        );
        when(eventoRepositorio.save(any(Evento.class))).thenReturn(eventoModificado);

        // Act
        Evento resultado = eventoServicio.editarParcialEvento(1L, cambios);

        // Assert
        assertNotNull(resultado);
        assertEquals("Título modificado", resultado.getTitulo());
        assertEquals("verde", resultado.getColor());
        verify(eventoRepositorio, times(1)).findById(1L);
        verify(servicioEmail, times(2)).enviarEmailModificacion(anyString(), anyString());
        verify(eventoRepositorio, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Debe modificar campo Long correctamente")
    void editarParcialEvento_DebeConvertirLongCorrectamente() throws FormatoFechaInvalidoException {
        // Arrange
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("duracion", "180");
        
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(eventoEjemplo));
        doNothing().when(servicioEmail).enviarEmailModificacion(anyString(), anyString());
        when(eventoRepositorio.save(any(Evento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Evento resultado = eventoServicio.editarParcialEvento(1L, cambios);

        // Assert
        assertNotNull(resultado);
        verify(eventoRepositorio, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción con formato de fecha inválido")
    void editarParcialEvento_DebeLanzarExcepcion_ConFormatoFechaInvalido() {
        // Arrange
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("fecha", "fecha-invalida");
        
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(eventoEjemplo));

        // Act & Assert
        assertThrows(FormatoFechaInvalidoException.class, () -> {
            eventoServicio.editarParcialEvento(1L, cambios);
        });
        verify(eventoRepositorio, times(1)).findById(1L);
        verify(eventoRepositorio, never()).save(any());
    }

    @Test
    @DisplayName("Debe retornar null cuando evento no existe al editar parcialmente")
    void editarParcialEvento_DebeRetornarNull_CuandoEventoNoExiste() throws FormatoFechaInvalidoException {
        // Arrange
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("titulo", "Nuevo título");
        when(eventoRepositorio.findById(999L)).thenReturn(Optional.empty());

        // Act
        Evento resultado = eventoServicio.editarParcialEvento(999L, cambios);

        // Assert
        assertNull(resultado);
        verify(eventoRepositorio, times(1)).findById(999L);
        verify(eventoRepositorio, never()).save(any());
    }

    @Test
    @DisplayName("No debe enviar emails cuando no hay asistentes registrados")
    void editarParcialEvento_NoDebeEnviarEmails_SinAsistentes() throws FormatoFechaInvalidoException {
        // Arrange
        Evento eventoSinAsistentes = new Evento(
                1L, 
                "Evento sin asistentes", 
                "Descripción", 
                fechaEjemplo,
                120L, 
                5000L, 
                Collections.emptyList(), 
                TipoEvento.EVENTO, 
                "azul", 
                true
        );
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("titulo", "Título modificado");
        
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(eventoSinAsistentes));
        when(eventoRepositorio.save(any(Evento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Evento resultado = eventoServicio.editarParcialEvento(1L, cambios);

        // Assert
        assertNotNull(resultado);
        verify(servicioEmail, never()).enviarEmailModificacion(anyString(), anyString());
        verify(servicioEmail, never()).enviarEmailCancelacion(anyString(), anyString());
    }
}