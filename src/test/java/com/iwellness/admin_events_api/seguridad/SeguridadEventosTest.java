package com.iwellness.admin_events_api.seguridad;

import com.iwellness.admin_events_api.clientes.UsuarioFeignCliente;
import com.iwellness.admin_events_api.dto.RolDTO;
import com.iwellness.admin_events_api.dto.UsuarioDTO;
import com.iwellness.admin_events_api.exceptions.UsuarioNoAutorizadoPorRolException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Seguridad de Eventos")
class SeguridadEventosTest {

    @Mock
    private UsuarioFeignCliente usuarioFeignCliente;

    @InjectMocks
    private SeguridadEventos seguridadEventos;

    private UsuarioDTO usuarioAdmin;
    private UsuarioDTO usuarioNormal;

    @BeforeEach
    void setUp() {
        // Usuario con rol Admin
        RolDTO rolAdmin = new RolDTO("Admin");
        usuarioAdmin = new UsuarioDTO(
                1L, 
                "Admin User", 
                "password123", 
                "admin@example.com", 
                rolAdmin
        );

        // Usuario con rol normal (no admin)
        RolDTO rolUsuario = new RolDTO("Usuario");
        usuarioNormal = new UsuarioDTO(
                2L, 
                "Normal User", 
                "password456", 
                "user@example.com", 
                rolUsuario
        );
    }

    @Test
    @DisplayName("Debe permitir acceso a usuario con rol Admin")
    void validarRol_DebePermitirAcceso_ConRolAdmin() {
        // Arrange
        when(usuarioFeignCliente.getUsuario()).thenReturn(usuarioAdmin);

        // Act & Assert
        assertDoesNotThrow(() -> seguridadEventos.validarRol());
        verify(usuarioFeignCliente, times(1)).getUsuario();
    }

    @Test
    @DisplayName("Debe lanzar excepción para usuario sin rol Admin")
    void validarRol_DebeLanzarExcepcion_SinRolAdmin() {
        // Arrange
        when(usuarioFeignCliente.getUsuario()).thenReturn(usuarioNormal);

        // Act & Assert
        assertThrows(UsuarioNoAutorizadoPorRolException.class, () -> {
            seguridadEventos.validarRol();
        });
        verify(usuarioFeignCliente, times(1)).getUsuario();
    }

    @Test
    @DisplayName("Debe lanzar excepción para rol nulo")
    void validarRol_DebeLanzarExcepcion_ConRolNulo() {
        // Arrange
        UsuarioDTO usuarioSinRol = new UsuarioDTO(
                3L, 
                "User Sin Rol", 
                "password", 
                "sinrol@example.com", 
                null
        );
        when(usuarioFeignCliente.getUsuario()).thenReturn(usuarioSinRol);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            seguridadEventos.validarRol();
        });
        verify(usuarioFeignCliente, times(1)).getUsuario();
    }

    @Test
    @DisplayName("Debe lanzar excepción para nombre de rol nulo")
    void validarRol_DebeLanzarExcepcion_ConNombreRolNulo() {
        // Arrange
        RolDTO rolSinNombre = new RolDTO(null);
        UsuarioDTO usuarioRolSinNombre = new UsuarioDTO(
                4L, 
                "User", 
                "password", 
                "user@example.com", 
                rolSinNombre
        );
        when(usuarioFeignCliente.getUsuario()).thenReturn(usuarioRolSinNombre);

        // Act & Assert
        // El código actual lanza NullPointerException porque List.of().contains(null) no permite null
        assertThrows(NullPointerException.class, () -> {
            seguridadEventos.validarRol();
        });
        verify(usuarioFeignCliente, times(1)).getUsuario();
    }

    @Test
    @DisplayName("Debe rechazar roles con mayúsculas/minúsculas incorrectas")
    void validarRol_DebeLanzarExcepcion_ConRolCaseIncorrecto() {
        // Arrange
        RolDTO rolMinusculas = new RolDTO("admin"); // con minúsculas
        UsuarioDTO usuarioMinusculas = new UsuarioDTO(
                5L, 
                "User", 
                "password", 
                "user@example.com", 
                rolMinusculas
        );
        when(usuarioFeignCliente.getUsuario()).thenReturn(usuarioMinusculas);

        // Act & Assert
        assertThrows(UsuarioNoAutorizadoPorRolException.class, () -> {
            seguridadEventos.validarRol();
        });
        verify(usuarioFeignCliente, times(1)).getUsuario();
    }

    @Test
    @DisplayName("Debe rechazar otros roles válidos pero no autorizados")
    void validarRol_DebeLanzarExcepcion_ConRolesNoAutorizados() {
        // Test con rol "Moderador"
        RolDTO rolModerador = new RolDTO("Moderador");
        UsuarioDTO usuarioModerador = new UsuarioDTO(
                6L, 
                "Moderador", 
                "password", 
                "mod@example.com", 
                rolModerador
        );
        when(usuarioFeignCliente.getUsuario()).thenReturn(usuarioModerador);

        assertThrows(UsuarioNoAutorizadoPorRolException.class, () -> {
            seguridadEventos.validarRol();
        });

        // Test con rol "Editor"
        RolDTO rolEditor = new RolDTO("Editor");
        UsuarioDTO usuarioEditor = new UsuarioDTO(
                7L, 
                "Editor", 
                "password", 
                "editor@example.com", 
                rolEditor
        );
        when(usuarioFeignCliente.getUsuario()).thenReturn(usuarioEditor);

        assertThrows(UsuarioNoAutorizadoPorRolException.class, () -> {
            seguridadEventos.validarRol();
        });
    }

    @Test
    @DisplayName("Debe manejar múltiples validaciones consecutivas")
    void validarRol_DebePermitirMultiplesValidaciones() {
        // Arrange
        when(usuarioFeignCliente.getUsuario()).thenReturn(usuarioAdmin);

        // Act & Assert - Primera validación
        assertDoesNotThrow(() -> seguridadEventos.validarRol());
        
        // Act & Assert - Segunda validación
        assertDoesNotThrow(() -> seguridadEventos.validarRol());
        
        // Act & Assert - Tercera validación
        assertDoesNotThrow(() -> seguridadEventos.validarRol());

        verify(usuarioFeignCliente, times(3)).getUsuario();
    }

    @Test
    @DisplayName("Debe propagar excepciones del Feign Client")
    void validarRol_DebePropagar_ExcepcionesFeignClient() {
        // Arrange
        when(usuarioFeignCliente.getUsuario()).thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            seguridadEventos.validarRol();
        });
        verify(usuarioFeignCliente, times(1)).getUsuario();
    }
}
