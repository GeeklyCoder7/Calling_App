package com.example.callingapp.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
    //Method for sending email to the user
    public static void sendEmail(Context context, String receipentEmailId, String subject, String messageBody) {
        final String myEmail = "afwanhusaini07@gmail.com";
        final String myPass = "qium vfhx nxnc ehqc";

        //Configuring SMTP server settings
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Gmail SMTP server
        props.put("mail.smtp.port", "587");

        //Creating sessions
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myEmail, myPass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receipentEmailId));
            message.setSubject(subject);
            message.setText(String.valueOf(messageBody));

            //Sending the composed email to the user
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}