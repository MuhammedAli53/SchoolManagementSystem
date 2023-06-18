package com.schoolmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    //eposya gondermek icin java mail apisini olusturuyoruz. Bunu spring ile nasil yapiyoruz bunu gorucez. Ayni security gibi.
    private static String MAIL_ADDRESS;
    private static String PASSWORD;

    public static void sendMail(String recipient , String mailMessage, String subject) throws MessagingException {
        //alici adresi, mesaj adresi ve konu basligini bu methodda gondericez.

        Properties properties = new Properties(); // burda properties ile setledik. application.propertieste de yazabilirdik.

        // !!! Gmail SMTP sunucuzunu kullanarak e-posta gondemrek icin yapilandiriliyor
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.enable", "false");

        Session session = Session.getInstance(properties, new Authenticator() {// Authenticator nesnesi icinde properties dosyamizi gondermemiz lazim.
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_ADDRESS,PASSWORD); // string iki ifademizi acikca goondermememizi saglar. Bohcalayip gonderiyoruz yani.
            }
        });

        Message message =  prepareMessage(session, MAIL_ADDRESS, recipient,mailMessage,subject);
        Transport.send(message); // message sinifi uzerinden alicinin adresini konu ve aciklamayi alip setlicez. Bunun icin asagida bir method olusturduk.

    }

    private static Message prepareMessage(Session session, String from, String recipient,
                                          String mailMessage, String subject) throws MessagingException {
        Message message = new MimeMessage(session); // farkli farkli message yapilari var. Mimemessage goruruz genelde.
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setText(mailMessage);
        return message;

    }

    @Value("${email.address}") // method bazli value de oluyor.
    public void setEmail(String email){
        MAIL_ADDRESS = email;
    }

    @Value("${email.password}")
    public void setPassword(String password){
        PASSWORD = password;
    }

}