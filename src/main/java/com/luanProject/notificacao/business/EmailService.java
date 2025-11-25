package com.luanProject.notificacao.business;

import com.luanProject.notificacao.business.dto.TarefasDTO;
import com.luanProject.notificacao.infrastructure.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    public JavaMailSender javaMailSender;

    @Autowired
    public TemplateEngine templateEngine;

    @Value("${envio.email.from}")
    public String from;

@Value("${envio.email.nameFrom}")
    public String nameFrom;

public void enviarEmail(TarefasDTO dto){

    try{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        mimeMessageHelper.setFrom(new InternetAddress(from, nameFrom));
        mimeMessageHelper.setTo(InternetAddress.parse(dto.getEmailUsuario()));
        mimeMessageHelper.setSubject("Notificação de Tarefa");

        Context context = new Context();
        context.setVariable("nomeTarefa", dto.getNomeTarefa());
        context.setVariable("dataEvento", dto.getDataEvento());
        context.setVariable("descricao", dto.getDescricao());
        String template = templateEngine.process("notificacao", context);
        mimeMessageHelper.setText(template, true);
        javaMailSender.send(message);

    } catch (MessagingException | UnsupportedEncodingException e){
        throw new EmailException("Erro ao enviar o email " + e.getCause());
    }
}


}
