package com.iwellness.admin_events_api.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Servicio de Email")
class ServicioEmailTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ServicioEmail servicioEmail;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private String destinatarioEjemplo;
    private String eventoEjemplo;

    @BeforeEach
    void setUp() {
        destinatarioEjemplo = "test@example.com";
        eventoEjemplo = "Evento de Prueba";
    }

    @Test
    @DisplayName("Debe enviar email de cancelación correctamente")
    void enviarEmailCancelacion_DebeEnviarEmailConDatosCorrectos() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailCancelacion(destinatarioEjemplo, eventoEjemplo);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage mensajeEnviado = messageCaptor.getValue();

        assertNotNull(mensajeEnviado);
        assertEquals(destinatarioEjemplo, mensajeEnviado.getTo()[0]);
        assertEquals("notificaciones.exiua@gmail.com", mensajeEnviado.getFrom());
        assertEquals("Evento Cancelado", mensajeEnviado.getSubject());
        assertTrue(mensajeEnviado.getText().contains("Lamentamos informarle"));
        assertTrue(mensajeEnviado.getText().contains(eventoEjemplo));
        assertTrue(mensajeEnviado.getText().contains("cancelado"));
    }

    @Test
    @DisplayName("Debe enviar email de modificación correctamente")
    void enviarEmailModificacion_DebeEnviarEmailConDatosCorrectos() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailModificacion(destinatarioEjemplo, eventoEjemplo);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage mensajeEnviado = messageCaptor.getValue();

        assertNotNull(mensajeEnviado);
        assertEquals(destinatarioEjemplo, mensajeEnviado.getTo()[0]);
        assertEquals("notificaciones.exiua@gmail.com", mensajeEnviado.getFrom());
        assertEquals("Evento Modificado", mensajeEnviado.getSubject());
        assertTrue(mensajeEnviado.getText().contains("Estimado usuario"));
        assertTrue(mensajeEnviado.getText().contains(eventoEjemplo));
        assertTrue(mensajeEnviado.getText().contains("modificado"));
        assertTrue(mensajeEnviado.getText().contains("Gracias"));
    }

    @Test
    @DisplayName("Debe enviar múltiples emails de cancelación")
    void enviarEmailCancelacion_DebeEnviarMultiplesEmails() {
        // Arrange
        String destinatario1 = "user1@example.com";
        String destinatario2 = "user2@example.com";
        String destinatario3 = "user3@example.com";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailCancelacion(destinatario1, eventoEjemplo);
        servicioEmail.enviarEmailCancelacion(destinatario2, eventoEjemplo);
        servicioEmail.enviarEmailCancelacion(destinatario3, eventoEjemplo);

        // Assert
        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Debe enviar múltiples emails de modificación")
    void enviarEmailModificacion_DebeEnviarMultiplesEmails() {
        // Arrange
        String destinatario1 = "user1@example.com";
        String destinatario2 = "user2@example.com";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailModificacion(destinatario1, eventoEjemplo);
        servicioEmail.enviarEmailModificacion(destinatario2, eventoEjemplo);

        // Assert
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Debe manejar nombres de evento con caracteres especiales")
    void enviarEmailCancelacion_DebeManejarCaracteresEspeciales() {
        // Arrange
        String eventoEspecial = "Evento & Conferencia <Especial> 'Test'";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailCancelacion(destinatarioEjemplo, eventoEspecial);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage mensajeEnviado = messageCaptor.getValue();

        assertTrue(mensajeEnviado.getText().contains(eventoEspecial));
    }

    @Test
    @DisplayName("Debe manejar nombres de evento largos")
    void enviarEmailModificacion_DebeManejarNombresLargos() {
        // Arrange
        String eventoLargo = "Este es un nombre de evento muy largo que contiene mucha información " +
                "sobre el evento y sus características principales para asegurar que funciona correctamente";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailModificacion(destinatarioEjemplo, eventoLargo);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage mensajeEnviado = messageCaptor.getValue();

        assertTrue(mensajeEnviado.getText().contains(eventoLargo));
    }

    @Test
    @DisplayName("Debe propagar excepciones del mailSender")
    void enviarEmailCancelacion_DebePropagar_ExcepcionesMailSender() {
        // Arrange
        doThrow(new RuntimeException("Error al enviar email"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            servicioEmail.enviarEmailCancelacion(destinatarioEjemplo, eventoEjemplo);
        });
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Debe validar que los emails tienen el remitente correcto")
    void enviarEmails_DebenTenerRemitenteConsistente() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act - Email de cancelación
        servicioEmail.enviarEmailCancelacion(destinatarioEjemplo, eventoEjemplo);
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage mensajeCancelacion = messageCaptor.getValue();

        // Act - Email de modificación
        servicioEmail.enviarEmailModificacion(destinatarioEjemplo, eventoEjemplo);
        verify(mailSender, times(2)).send(messageCaptor.capture());
        SimpleMailMessage mensajeModificacion = messageCaptor.getValue();

        // Assert
        assertEquals("notificaciones.exiua@gmail.com", mensajeCancelacion.getFrom());
        assertEquals("notificaciones.exiua@gmail.com", mensajeModificacion.getFrom());
    }

    @Test
    @DisplayName("Debe incluir información del evento en el cuerpo del mensaje")
    void enviarEmailCancelacion_DebeIncluirNombreEventoEnCuerpo() {
        // Arrange
        String eventoConNombreEspecifico = "Conferencia Anual de Tecnología 2024";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailCancelacion(destinatarioEjemplo, eventoConNombreEspecifico);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage mensajeEnviado = messageCaptor.getValue();

        String cuerpoMensaje = mensajeEnviado.getText();
        assertTrue(cuerpoMensaje.contains(eventoConNombreEspecifico),
                "El cuerpo del mensaje debe contener el nombre del evento");
    }

    @Test
    @DisplayName("Debe validar formato del mensaje de cancelación")
    void enviarEmailCancelacion_DebeContenerMensajeFormateadoCorrectamente() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailCancelacion(destinatarioEjemplo, eventoEjemplo);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage mensajeEnviado = messageCaptor.getValue();

        String mensajeEsperado = "Lamentamos informarle que el evento " + 
                eventoEjemplo + " fue cancelado.";
        assertEquals(mensajeEsperado, mensajeEnviado.getText());
    }

    @Test
    @DisplayName("Debe validar formato del mensaje de modificación")
    void enviarEmailModificacion_DebeContenerMensajeFormateadoCorrectamente() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        servicioEmail.enviarEmailModificacion(destinatarioEjemplo, eventoEjemplo);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage mensajeEnviado = messageCaptor.getValue();

        String mensajeEsperado = "Estimado usuario se informa que el evento " + 
                eventoEjemplo + " fue modificado.\nGracias.";
        assertEquals(mensajeEsperado, mensajeEnviado.getText());
    }
}
