package edu.nure.email;

import edu.nure.Manager;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;


public class EmailSender {

    private String username;
    private String password;
    private Properties props;

    public EmailSender() {
        ResourceBundle rb = ResourceBundle.getBundle("config", new Locale("email"));
        this.username = rb.getString("email");
        this.password = rb.getString("pass");
        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

    }

    public void send(final String subject, final String text, final String toEmail) {
        new Thread() {
            @Override
            public void run() {
                Session session = Session.getDefaultInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("Admin"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                    message.setSubject(subject);
                    message.setText(text);

                    Transport.send(message);
                } catch (MessagingException ex) {
                    Manager.setLog(ex);
                }
            }
        }.start();

    }
}

