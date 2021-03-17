package com.flashscore;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mail {


    Properties properties = System.getProperties();

    // Recipient's email ID needs to be mentioned.
    String to = "cmpcristian@gmail.com";

    // Sender's email ID needs to be mentioned
    String from = "cristibet83@gmail.com";


    public Mail() throws AddressException, MessagingException {


        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";


        // Get system properties

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

    }


    public void sendMail(String subject, String textmsg) throws MessagingException {
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("cristibet83@gmail.com", "ParolaParola123");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(from));

        // Set To: header field of the header.
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // Set Subject: header field
        message.setSubject(subject);

        // Now set the actual message
        message.setText(textmsg);

        System.out.println("sending...");
        // Send message
        Transport.send(message);
        System.out.println("Sent message successfully....");


    }

}

