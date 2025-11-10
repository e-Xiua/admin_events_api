package com.iwellness.admin_events_api.dto;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.iwellness.admin_events_api.entidades.TipoEvento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String fecha;
    private Long duracion;
    private Long costo;
    private List<String> asistentes;
    private TipoEvento tipo;
    private String color;
    private Boolean activo;
}
