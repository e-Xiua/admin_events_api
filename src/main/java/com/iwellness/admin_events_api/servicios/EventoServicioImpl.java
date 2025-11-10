package com.iwellness.admin_events_api.servicios;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.iwellness.admin_events_api.exceptions.FormatoFechaInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;

import com.iwellness.admin_events_api.email.ServicioEmail;
import com.iwellness.admin_events_api.entidades.Evento;
import com.iwellness.admin_events_api.repositorios.EventoRepositorio;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Service
public class EventoServicioImpl implements IEventoServicio{

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private ServicioEmail servicioEmail;

    @Override
    public List<Evento> getAllEventos() {
        return eventoRepositorio.findAll(); 
    }

    @Override
    public Evento getEventoById(Long idEvento) {
        return eventoRepositorio.findById(idEvento).orElse(null);
    }

    @Override
    public Evento crearEvento(Evento evento) {
        evento.setActivo(true);
        return eventoRepositorio.save(evento);
    }

    @Override
    public Evento editarEvento(Evento evento) {
        return eventoRepositorio.save(evento);
    }

    @Override
    public void eliminarEvento(Long idEvento) {
        eventoRepositorio.deleteById(idEvento);
    }

    @Override
    public Evento editarParcialEvento(Long idEvento, Map<String,Object> editados) throws FormatoFechaInvalidoException{
        Optional<Evento> eventoOpt = eventoRepositorio.findById(idEvento);
        if (eventoOpt.isPresent()){
            Evento evento = eventoOpt.get();
            if (editados.containsKey("activo")){
                evento.setActivo(false);
                for (String destinatario : evento.getAsistentes()) {
                    servicioEmail.enviarEmailCancelacion(destinatario, evento.getTitulo());
                }
            } else {
                for (Map.Entry<String, Object> entry : editados.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    Field field = ReflectionUtils.findField(Evento.class, key);
                    if (field != null) {
                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(field, evento, getConvertedValue(field, value));
                    }
                }
                for (String destinatario : evento.getAsistentes()) {
                    servicioEmail.enviarEmailModificacion(destinatario, evento.getTitulo());
                }
            }
            return eventoRepositorio.save(evento);
        }
        return null;
    }

    private Object getConvertedValue(Field field, Object value) throws FormatoFechaInvalidoException {
        if(!field.getType().equals(value.getClass())){
            if(field.getType().equals(Long.class)){
                return Long.valueOf(value.toString());
            }
            if(field.getType().equals(Date.class)){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    return dateFormat.parse(value.toString());
                } catch (ParseException e) {
                    throw new FormatoFechaInvalidoException();
                }
            }
        }
        return value;
    }
}
