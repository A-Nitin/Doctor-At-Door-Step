package mail;
import java.io.File;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

public class sendAttachments {
    public sendAttachments(String receiver) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", 587);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.transport.protocol", "smtp");
            
            Session ses = Session.getInstance(props,new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("salimlias4444qwez@gmail.com", "salim987654321");
                }
            });
            
            Message msg = new MimeMessage(ses) {};
            msg.setSubject("Message From DADS Official App");
            
            Address addressto = new InternetAddress(receiver);
            msg.setRecipient(Message.RecipientType.TO, addressto);
            
            MimeMultipart multipart = new MimeMultipart();
            
            MimeBodyPart attach = new MimeBodyPart();
            attach.attachFile(new File("src\\doc\\prescription.pdf"));
            
            MimeBodyPart bodyMsg = new MimeBodyPart();
            bodyMsg.setContent("<html>Prescription of your currently ended session.</html>","text/html");
            
            multipart.addBodyPart(attach);
            multipart.addBodyPart(bodyMsg);
            
            msg.setContent(multipart);
            
            Transport.send(msg);
            System.out.println("Attachment sent");
        } catch (Exception ex) {}
        
    }
	
}
	

