package com.test.util.helpers;

import com.test.model.User;
import com.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CustomMailSender {
    private static final String sender = "aregism@gmail.com";

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void send(User recipient, String subject, String message) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(sender);
        smm.setTo(recipient.getEmail());
        smm.setSubject(subject);
        smm.setText(message);
        mailSender.send(smm);
    }
}
