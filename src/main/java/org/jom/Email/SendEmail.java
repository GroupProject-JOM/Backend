package org.jom.Email;

import java.util.Properties;
import java.util.Random;
import java.nio.charset.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class SendEmail {

    private static String from = "saumyasewwandi05@gmail.com";
    private static String pin = "auzciwkhdnhnzofw";
    public static int SendOTP (String email){
        int otpvalue = 0;
        // sending otp
        Random rand = new Random();
        otpvalue = rand.nextInt(1255650);

        String to = email;// change accordingly
        // Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from,pin);
            }
        });
        // compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));// change accordingly
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Email Verification");
            message.setText("your OTP is: " + otpvalue);
            // send message
            Transport.send(message);
            System.out.println("Email otp sent successfully");
            return otpvalue;
        }

        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String SendPassword (String email,String name){
        String password = getAlphaNumericString();

        String to = email;// change accordingly
        // Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from,pin);
            }
        });
        // compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));// change accordingly
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Employee Password");
            message.setText("Hello "+name+",\n\nYour password is: " + password +"\n\n*This is system generated password. After login you can change your password anytime.");

            // send message
            Transport.send(message);
            System.out.println("Password sent successfully");
            return password;
        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAlphaNumericString()
    {
        int length = 6;
        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString = new String(array, Charset.forName("UTF-8"));

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9'))
                    && (length > 0)) {

                r.append(ch);
                length--;
            }
        }

        // return the resultant string
        return r.toString();
    }

    public static int optionalVerification (String email,int amount,String name){
        int otpvalue = 0;
        // sending otp
        Random rand = new Random();
        otpvalue = rand.nextInt(1255650);

        String to = email;// change accordingly
        // Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from,pin);
            }
        });
        // compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));// change accordingly
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Optional Verification");
            message.setText("Hello "+name+",\n\nSupplier entered coconut amount is: " + amount +"\n\nIf you are agree with this amount tell collector to this OTP : <b>"+otpvalue+"</b> to complete collection.");
            // send message
            Transport.send(message);
            System.out.println("Email otp sent successfully");
            return otpvalue;
        }

        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

