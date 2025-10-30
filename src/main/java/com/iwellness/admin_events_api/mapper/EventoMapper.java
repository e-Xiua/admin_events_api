package com.iwellness.admin_events_api.mapper;

import com.iwellness.admin_events_api.dto.EventoDTO;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.exceptions.FormatoFechaInvalidoException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventoMapper {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static  Evento eventoDtoToEvento(EventoDTO eventoDTO) throws FormatoFechaInvalidoException {
        Date fechaEventoFormateada = null;
        if(eventoDTO.getFecha() != null){
            try {
                fechaEventoFormateada = dateFormat.parse(eventoDTO.getFecha());
            } catch (ParseException e) {
                throw new FormatoFechaInvalidoException();
            }
        }
        return Evento.builder()
                .id(eventoDTO.getId())
                .titulo(eventoDTO.getTitulo())
                .descripcion(eventoDTO.getDescripcion())
                .fecha(fechaEventoFormateada)
                .duracion(eventoDTO.getDuracion())
                .costo(eventoDTO.getCosto())
                .asistentes(eventoDTO.getAsistentes())
                .tipo(eventoDTO.getTipo())
                .color(eventoDTO.getColor())
                .build();
    }

    public static EventoDTO eventoToEventoDto(Evento evento)
    {
        String format = evento.getFecha() != null ? dateFormat.format(evento.getFecha()) : null;
        return new EventoDTO(evento.getId(), evento.getTitulo(), evento.getDescripcion(), format,
                evento.getDuracion(), evento.getCosto(), evento.getAsistentes(), evento.getTipo(),evento.getColor(),
                evento.getActivo());
    }
}
