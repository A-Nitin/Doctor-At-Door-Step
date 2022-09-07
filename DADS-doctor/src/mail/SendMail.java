package mail;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class SendMail {
	final String senderEmail = "salimlias4444qwez@gmail.com"; //change email address
	final String senderPassword = "salim987654321"; //change password
	final String emailSMTPserver = "smtp.gmail.com";
	final String emailServerPort = "465";
	String receiverEmail = null;
	static String emailSubject;
	static String emailBody;
	public SendMail(String receiverEmail, String subject, String body) {
		//receiver email
		this.receiverEmail = receiverEmail;
		//subject
		this.emailSubject = subject;
		//body
		this.emailBody = body;
		
		Properties props = new Properties();
		props.put("mail.smtp.user",senderEmail);
		props.put("mail.smtp.host", emailSMTPserver);
		props.put("mail.smtp.port", emailServerPort);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", emailServerPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		SecurityManager security = System.getSecurityManager();
		
		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);
			MimeMessage msg = new MimeMessage(session);
			msg.setText(emailBody);
			//System.out.println(emailBody);
			msg.setSubject(emailSubject);
			msg.setFrom(new InternetAddress(senderEmail));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
			Transport.send(msg);
			System.out.println("Message sent successfully");
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(senderEmail, senderPassword);
		}
	}				
}
	

