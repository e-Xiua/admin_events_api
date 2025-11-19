package com.iwellness.admin_events_api.mapper;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.entidades.TipoEvento;
import com.iwellness.admin_events_api.exceptions.FormatoFechaInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests del Mapper de Eventos")
class EventoMapperTest {

    private SimpleDateFormat dateFormat;
    private Date fechaEjemplo;
    private String fechaEjemploStr;

    @BeforeEach
    void setUp() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        fechaEjemplo = new Date();
        fechaEjemploStr = dateFormat.format(fechaEjemplo);
    }

    @Test
    @DisplayName("Debe convertir EventoDTO a Evento correctamente")
    void eventoDtoToEvento_DebeConvertirCorrectamente() throws FormatoFechaInvalidoException {
        // Arrange
        List<String> asistentes = Arrays.asList("test1@example.com", "test2@example.com");
        EventoDTO eventoDTO = new EventoDTO(
                1L,
                "Evento de prueba",
                "Descripción de prueba",
                fechaEjemploStr,
                120L,
                5000L,
                asistentes,
                TipoEvento.EVENTO,
                "azul",
                true
        );

        // Act
        Evento resultado = EventoMapper.eventoDtoToEvento(eventoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Evento de prueba", resultado.getTitulo());
        assertEquals("Descripción de prueba", resultado.getDescripcion());
        assertNotNull(resultado.getFecha());
        assertEquals(120L, resultado.getDuracion());
        assertEquals(5000L, resultado.getCosto());
        assertEquals(asistentes, resultado.getAsistentes());
        assertEquals(TipoEvento.EVENTO, resultado.getTipo());
        assertEquals("azul", resultado.getColor());
    }

    @Test
    @DisplayName("Debe convertir Evento a EventoDTO correctamente")
    void eventoToEventoDto_DebeConvertirCorrectamente() {
        // Arrange
        List<String> asistentes = Arrays.asList("test1@example.com", "test2@example.com");
        Evento evento = new Evento(
                1L,
                "Evento de prueba",
                "Descripción de prueba",
                fechaEjemplo,
                120L,
                5000L,
                asistentes,
                TipoEvento.EVENTO,
                "azul",
                true
        );

        // Act
        EventoDTO resultado = EventoMapper.eventoToEventoDto(evento);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Evento de prueba", resultado.getTitulo());
        assertEquals("Descripción de prueba", resultado.getDescripcion());
        assertNotNull(resultado.getFecha());
        assertEquals(120L, resultado.getDuracion());
        assertEquals(5000L, resultado.getCosto());
        assertEquals(asistentes, resultado.getAsistentes());
        assertEquals(TipoEvento.EVENTO, resultado.getTipo());
        assertEquals("azul", resultado.getColor());
        assertTrue(resultado.getActivo());
    }

    @Test
    @DisplayName("Debe manejar EventoDTO sin fecha")
    void eventoDtoToEvento_DebeManejarFechaNull() throws FormatoFechaInvalidoException {
        // Arrange
        EventoDTO eventoDTO = new EventoDTO(
                1L,
                "Evento sin fecha",
                "Descripción",
                null,
                60L,
                3000L,
                List.of(),
                TipoEvento.REUNION,
                "verde",
                true
        );

        // Act
        Evento resultado = EventoMapper.eventoDtoToEvento(eventoDTO);

        // Assert
        assertNotNull(resultado);
        assertNull(resultado.getFecha());
        assertEquals("Evento sin fecha", resultado.getTitulo());
    }

    @Test
    @DisplayName("Debe manejar Evento sin fecha")
    void eventoToEventoDto_DebeManejarFechaNull() {
        // Arrange
        Evento evento = new Evento(
                1L,
                "Evento sin fecha",
                "Descripción",
                null,
                60L,
                3000L,
                List.of(),
                TipoEvento.REUNION,
                "verde",
                true
        );

        // Act
        EventoDTO resultado = EventoMapper.eventoToEventoDto(evento);

        // Assert
        assertNotNull(resultado);
        assertNull(resultado.getFecha());
        assertEquals("Evento sin fecha", resultado.getTitulo());
    }

    @Test
    @DisplayName("Debe lanzar excepción con formato de fecha inválido")
    void eventoDtoToEvento_DebeLanzarExcepcion_ConFormatoInvalido() {
        // Arrange
        EventoDTO eventoDTO = new EventoDTO(
                1L,
                "Evento con fecha inválida",
                "Descripción",
                "fecha-invalida",
                60L,
                3000L,
                List.of(),
                TipoEvento.REUNION,
                "rojo",
                true
        );

        // Act & Assert
        assertThrows(FormatoFechaInvalidoException.class, () -> {
            EventoMapper.eventoDtoToEvento(eventoDTO);
        });
    }

    @Test
    @DisplayName("Debe manejar diferentes formatos de fecha válidos")
    void eventoDtoToEvento_DebeManejarFormatoFechaISO8601() throws FormatoFechaInvalidoException {
        // Arrange
        String fechaISO = "2024-12-25T15:30:00.000Z";
        EventoDTO eventoDTO = new EventoDTO(
                1L,
                "Evento de navidad",
                "Descripción",
                fechaISO,
                180L,
                10000L,
                List.of("navidad@example.com"),
                TipoEvento.REUNION,
                "rojo",
                true
        );

        // Act
        Evento resultado = EventoMapper.eventoDtoToEvento(eventoDTO);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getFecha());
        assertEquals("Evento de navidad", resultado.getTitulo());
    }

    @Test
    @DisplayName("Debe preservar todos los tipos de evento")
    void eventoDtoToEvento_DebePreservarTipoEvento() throws FormatoFechaInvalidoException {
        // Test para EVENTO
        EventoDTO eventoDTO1 = new EventoDTO(1L, "Evento", "Desc", fechaEjemploStr, 60L, 1000L, 
                List.of(), TipoEvento.EVENTO, "azul", true);
        assertEquals(TipoEvento.EVENTO, EventoMapper.eventoDtoToEvento(eventoDTO1).getTipo());

        // Test para REUNION
        EventoDTO eventoDTO2 = new EventoDTO(2L, "Reunion", "Desc", fechaEjemploStr, 60L, 0L, 
                List.of(), TipoEvento.REUNION, "verde", true);
        assertEquals(TipoEvento.REUNION, EventoMapper.eventoDtoToEvento(eventoDTO2).getTipo());
    }

    @Test
    @DisplayName("Debe manejar listas de asistentes vacías")
    void eventoDtoToEvento_DebeManejarAsistentesVacios() throws FormatoFechaInvalidoException {
        // Arrange
        EventoDTO eventoDTO = new EventoDTO(
                1L,
                "Evento sin asistentes",
                "Descripción",
                fechaEjemploStr,
                90L,
                2000L,
                List.of(),
                TipoEvento.EVENTO,
                "amarillo",
                true
        );

        // Act
        Evento resultado = EventoMapper.eventoDtoToEvento(eventoDTO);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getAsistentes());
        assertTrue(resultado.getAsistentes().isEmpty());
    }

    @Test
    @DisplayName("Debe convertir correctamente ida y vuelta")
    void debeConvertirIdaYVuelta_SinPerdidaDeDatos() throws FormatoFechaInvalidoException {
        // Arrange
        List<String> asistentes = Arrays.asList("test1@example.com", "test2@example.com");
        Evento eventoOriginal = new Evento(
                1L,
                "Evento completo",
                "Descripción completa",
                fechaEjemplo,
                150L,
                7500L,
                asistentes,
                TipoEvento.EVENTO,
                "morado",
                true
        );

        // Act
        EventoDTO eventoDTO = EventoMapper.eventoToEventoDto(eventoOriginal);
        Evento eventoConvertido = EventoMapper.eventoDtoToEvento(eventoDTO);

        // Assert
        assertEquals(eventoOriginal.getId(), eventoConvertido.getId());
        assertEquals(eventoOriginal.getTitulo(), eventoConvertido.getTitulo());
        assertEquals(eventoOriginal.getDescripcion(), eventoConvertido.getDescripcion());
        assertEquals(eventoOriginal.getDuracion(), eventoConvertido.getDuracion());
        assertEquals(eventoOriginal.getCosto(), eventoConvertido.getCosto());
        assertEquals(eventoOriginal.getAsistentes(), eventoConvertido.getAsistentes());
        assertEquals(eventoOriginal.getTipo(), eventoConvertido.getTipo());
        assertEquals(eventoOriginal.getColor(), eventoConvertido.getColor());
    }

    @Test
    @DisplayName("Debe manejar valores nulos en campos opcionales")
    void eventoDtoToEvento_DebeManejarCamposNulos() throws FormatoFechaInvalidoException {
        // Arrange
        EventoDTO eventoDTO = new EventoDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Act
        Evento resultado = EventoMapper.eventoDtoToEvento(eventoDTO);

        // Assert
        assertNotNull(resultado);
        assertNull(resultado.getId());
        assertNull(resultado.getTitulo());
        assertNull(resultado.getDescripcion());
        assertNull(resultado.getFecha());
    }
}
