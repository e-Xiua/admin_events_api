package com.iwellness.admin_events_api.clientes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.iwellness.admin_events_api.config.CustomFeignConfiguration;
import com.iwellness.admin_events_api.dto.UsuarioDTO;

@FeignClient(name = "seguridad-ms", configuration = CustomFeignConfiguration.class ,url = "http://localhost:8082/auth")
public interface UsuarioFeignCliente {

    @GetMapping("/info")
    public UsuarioDTO getUsuario();

}
