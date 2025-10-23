package com.iwellness.admin_events_api.mapper;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.entidades.Evento;

public class EventoMapper {
    public static  Evento eventoDtoToEvento(EventoDTO eventoDTO)
    {
        return Evento.builder()
                .id(eventoDTO.getId())
                .titulo(eventoDTO.getTitulo())
                .descripcion(eventoDTO.getDescripcion())
                .fecha(eventoDTO.getFecha())
                .duracion(eventoDTO.getDuracion())
                .costo(eventoDTO.getCosto())
                .asistentes(eventoDTO.getAsistentes())
                .tipo(eventoDTO.getTipo())
                .color(eventoDTO.getColor())
                .build();
    }

    public static EventoDTO eventoToEventoDto(Evento evento)
    {
        return new EventoDTO(evento.getId(), evento.getTitulo(), evento.getDescripcion(), evento.getFecha(),
                evento.getDuracion(), evento.getCosto(), evento.getAsistentes(), evento.getTipo(),evento.getColor(),
                evento.getActivo());
    }
}
