package com.pagacz.flatflex.infrastructure.config;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class EmailConfig {

    private final Logger log = LoggerFactory.getLogger(EmailConfig.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${FF_EMAIL_NAME}")
    private String username;

    @Value("${FF_EMAIL_PASS}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttls;

    @Value("${spring.mail.properties.mail.smtp.socketFactory.class}")
    private String socketFactoryClass;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String sslTrust;

    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
    private String connectionTimeout;

    public Message prepareMessage(String mail, String subject, String sender, String recipients) {
        Session session = configurePropertiesAndPrepareSession();
        session.setDebug(true);
        Message message = new MimeMessage(session);
        prepareSubject(message, subject);
        prepareSender(message, sender);
        prepareRecipients(message, recipients);
        prepareMessageContent(message, mail);
        return message;
    }

    private void prepareSubject(Message message, String subject) {
        try {
            message.setSubject(subject);
        } catch (MessagingException e) {
            log.error("Error occurred at setting message subject stage", e);
        }
    }

    private void prepareRecipients(Message message, String recipients) {
        try {
            message.setRecipients(
                    Message.RecipientType.BCC, InternetAddress.parse(recipients));
        } catch (MessagingException e) {
            log.error("Error occurred at setting message recipients stage", e);
        }
    }

    private void prepareSender(Message message, String sender) {
        try {
            message.setFrom(new InternetAddress(sender));
        } catch (MessagingException e) {
            log.error("Error occurred at setting message sender stage", e);
        }
    }

    private Session configurePropertiesAndPrepareSession() {
        Properties prop = new Properties();
        configureProperties(prop);
        return prepareSession(prop);
    }

    private void prepareMessageContent(Message message, String mail) {
        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(mail, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
        } catch (MessagingException e) {
            log.error("Error occurred at preparing message content stage", e);
        }
    }

    private Session prepareSession(Properties prop) {
        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private void configureProperties(Properties prop) {
        prop.put("mail.smtp.auth", auth);
        prop.put("mail.smtp.starttls.enable", starttls);
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.ssl.trust", sslTrust);
        prop.put("mail.smtp.socketFactory.class", socketFactoryClass);
        prop.put("mail.smtp.connectiontimeout", connectionTimeout);
    }
}
