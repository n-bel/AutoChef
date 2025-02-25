package com.g04autochef.controller.utils;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class ShoppingListEmailSender {
    private static final ShoppingListEmailSender instance = new ShoppingListEmailSender();

    private final String EMAIL_FROM = "g04autochef@gmail.com";
    private final String PASSWORRD_EMAIL = "vycdIb-mickor-9wohnu";

    private final Properties props;

    public static ShoppingListEmailSender getInstance() {return instance;}

    protected ShoppingListEmailSender() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        this.props = props;
    }



    public void sendEmail(String emailDestination, String pdfName) throws MessagingException {
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_FROM, PASSWORRD_EMAIL);
                    }
            });
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailDestination));
            message.setSubject("Liste de courses " + pdfName.replace(".pdf", ""));
            message.setText("Vous trouverez en pièce jointe la liste de courses");

            addPDFAttachment(pdfName, message);
            Transport.send(message);
    }

    private void addPDFAttachment(String pdfName, MimeMessage message) throws MessagingException {
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(pdfName);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName("liste de courses.pdf");
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);
    }

}
