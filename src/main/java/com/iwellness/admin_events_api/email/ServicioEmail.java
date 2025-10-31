package com.iwellness.admin_events_api.email;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ServicioEmail {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailCancelacion(String destinatario, String evento) {
        SimpleMailMessage message = new SimpleMailMessage();
        String mensaje = "Lamentamos informarle que el evento "+evento+" fue cancelado.";
        message.setFrom("d4rkd3m0n1@gmail.com");
        message.setTo(destinatario);
        message.setSubject("Evento Cancelado");
        message.setText(mensaje);
        mailSender.send(message);
    }

    public void enviarEmailModificacion(String destinatario, String evento) {
        SimpleMailMessage message = new SimpleMailMessage();
        String mensaje = "Estimado usuario se informa que el evento "+evento+" fue modificado.\nGracias.";
        message.setFrom("d4rkd3m0n1@gmail.com");
        message.setTo(destinatario);
        message.setSubject("Evento Modificado");
        message.setText(mensaje);
        mailSender.send(message);
    }
    
}
