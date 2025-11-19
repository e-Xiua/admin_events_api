package com.iwellness.admin_events_api.integration;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.entidades.TipoEvento;
import com.iwellness.admin_events_api.repositorios.EventoRepositorio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para el flujo completo de eventos
 * Estas pruebas utilizan el contexto completo de Spring
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Tests de Integración - Eventos")
class EventoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private ObjectMapper objectMapper;

    private SimpleDateFormat dateFormat;
    private Evento eventoTest;

    @BeforeEach
    void setUp() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        // Limpiar base de datos antes de cada test
        eventoRepositorio.deleteAll();

        // Crear evento de prueba
        eventoTest = new Evento(
                null,
                "Evento de Integración",
                "Descripción para pruebas de integración",
                new Date(),
                120L,
                5000L,
                Arrays.asList("test1@example.com", "test2@example.com"),
                TipoEvento.EVENTO,
                "azul",
                true
        );
    }

    @AfterEach
    void tearDown() {
        // Limpiar base de datos después de cada test
        eventoRepositorio.deleteAll();
    }

    @Test
    @DisplayName("Debe obtener todos los eventos exitosamente")
    void getAllEventos_DebeRetornarTodosLosEventos() throws Exception {
        // Arrange - Guardar eventos en la base de datos
        eventoRepositorio.save(eventoTest);
        Evento evento2 = new Evento(null, "Evento 2", "Desc 2", new Date(), 
                60L, 3000L, List.of(), TipoEvento.REUNION, "verde", true);
        eventoRepositorio.save(evento2);

        // Act & Assert
        mockMvc.perform(get("/evento")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo", is("Evento de Integración")))
                .andExpect(jsonPath("$[1].titulo", is("Evento 2")));
    }

    @Test
    @DisplayName("Debe obtener un evento por ID")
    void getEventoById_DebeRetornarEventoCorrectamente() throws Exception {
        // Arrange
        Evento eventoGuardado = eventoRepositorio.save(eventoTest);

        // Act & Assert
        mockMvc.perform(get("/evento/" + eventoGuardado.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventoGuardado.getId().intValue())))
                .andExpect(jsonPath("$.titulo", is("Evento de Integración")))
                .andExpect(jsonPath("$.descripcion", is("Descripción para pruebas de integración")))
                .andExpect(jsonPath("$.activo", is(true)));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el evento no existe")
    void getEventoById_DebeRetornar404_CuandoEventoNoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/evento/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe crear un nuevo evento exitosamente")
    void crearEvento_DebeCrearEventoExitosamente() throws Exception {
        // Arrange
        EventoDTO nuevoEvento = new EventoDTO(
                null,
                "Nuevo Evento",
                "Nueva descripción",
                dateFormat.format(new Date()),
                90L,
                4000L,
                Arrays.asList("nuevo@example.com"),
                TipoEvento.EVENTO,
                "rojo",
                null
        );
        String eventoJson = objectMapper.writeValueAsString(nuevoEvento);

        // Act & Assert
        mockMvc.perform(post("/evento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Nuevo Evento")))
                .andExpect(jsonPath("$.activo", is(true)))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("Debe editar un evento completo exitosamente")
    void editarEvento_DebeEditarEventoExitosamente() throws Exception {
        // Arrange
        Evento eventoGuardado = eventoRepositorio.save(eventoTest);
        
        EventoDTO eventoEditado = new EventoDTO(
                eventoGuardado.getId(),
                "Evento Editado",
                "Descripción editada",
                dateFormat.format(new Date()),
                150L,
                6000L,
                Arrays.asList("editado@example.com"),
                TipoEvento.REUNION,
                "amarillo",
                true
        );
        String eventoJson = objectMapper.writeValueAsString(eventoEditado);

        // Act & Assert
        mockMvc.perform(put("/evento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Evento Editado")))
                .andExpect(jsonPath("$.descripcion", is("Descripción editada")))
                .andExpect(jsonPath("$.duracion", is(150)));
    }

    @Test
    @DisplayName("Debe editar parcialmente un evento")
    void editarParcialEvento_DebeEditarCamposEspecificos() throws Exception {
        // Arrange
        Evento eventoGuardado = eventoRepositorio.save(eventoTest);
        
        String cambiosJson = "{\"titulo\": \"Título Modificado\", \"color\": \"verde\"}";

        // Act & Assert
        mockMvc.perform(patch("/evento/" + eventoGuardado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(cambiosJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Título Modificado")))
                .andExpect(jsonPath("$.color", is("verde")))
                .andExpect(jsonPath("$.descripcion", is("Descripción para pruebas de integración")));
    }

    @Test
    @DisplayName("Debe desactivar un evento al editarlo parcialmente")
    void editarParcialEvento_DebeDesactivarEvento() throws Exception {
        // Arrange
        Evento eventoGuardado = eventoRepositorio.save(eventoTest);
        
        String cambiosJson = "{\"activo\": false}";

        // Act & Assert
        mockMvc.perform(patch("/evento/" + eventoGuardado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(cambiosJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(false)));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay eventos")
    void getAllEventos_DebeRetornarListaVacia_SinEventos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/evento")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Debe crear evento con valores mínimos requeridos")
    void crearEvento_DebeCrear_ConValoresMinimos() throws Exception {
        // Arrange
        EventoDTO eventoMinimo = new EventoDTO(
                null,
                "Evento Mínimo",
                "Descripción",
                dateFormat.format(new Date()),
                60L,
                0L,
                List.of(),
                TipoEvento.EVENTO,
                "blanco",
                null
        );
        String eventoJson = objectMapper.writeValueAsString(eventoMinimo);

        // Act & Assert
        mockMvc.perform(post("/evento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Evento Mínimo")))
                .andExpect(jsonPath("$.costo", is(0)));
    }

    @Test
    @DisplayName("Flujo completo: crear, leer, actualizar evento")
    void flujoCRU_DebeCompletarExitosamente() throws Exception {
        // 1. Crear evento
        EventoDTO nuevoEvento = new EventoDTO(
                null,
                "Evento Flujo Completo",
                "Descripción inicial",
                dateFormat.format(new Date()),
                120L,
                5000L,
                List.of("flujo@example.com"),
                TipoEvento.EVENTO,
                "morado",
                null
        );
        String eventoJson = objectMapper.writeValueAsString(nuevoEvento);

        String responseCreate = mockMvc.perform(post("/evento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Evento Flujo Completo")))
                .andReturn().getResponse().getContentAsString();

        EventoDTO eventoCreado = objectMapper.readValue(responseCreate, EventoDTO.class);
        Long eventoId = eventoCreado.getId();

        // 2. Leer evento creado
        mockMvc.perform(get("/evento/" + eventoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Evento Flujo Completo")));

        // 3. Actualizar evento parcialmente
        String cambiosJson = "{\"descripcion\": \"Descripción actualizada\"}";
        mockMvc.perform(patch("/evento/" + eventoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cambiosJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Descripción actualizada")));

        // 4. Verificar actualización
        mockMvc.perform(get("/evento/" + eventoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Descripción actualizada")));
    }
}
