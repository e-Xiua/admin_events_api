package com.iwellness.admin_events_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iwellness.admin_events_api.entidades.Evento;

public interface EventoRepositorio extends JpaRepository<Evento, Long>{
    
}
