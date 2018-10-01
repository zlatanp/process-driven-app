package upp.project.service.impl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import upp.project.model.dto.UserDTO;
import upp.project.service.MailService;

@Service("mailService")
public class MailServiceImpl implements MailService {
	
	@Override
	public void send(String executionId, String processInstanceId, String emailTo) {
		System.out.println("Usao u slanje maila " + executionId + " " + processInstanceId);
		final String username = "zijdev@gmail.com";
		final String password = "mojalozinka12";
		
		String activationLink = "http://localhost:8080/api/registration/active/" + processInstanceId + "/" + executionId;
		
		String m_to = emailTo, m_subject = "AMS Account",
				m_text = "Hi,\t\n\t\nThank you for registering on our website.\t\n"
						+ "To activate your account please go on link: " + activationLink +
						"\t\n\t\nBest Regards,\t\nYour AMS.";
		Properties props = new Properties();
		props.put("mail.smtp.user", username);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.EnableSSL.enable", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");		
		props.put("mail.smtp.socketFactory.port", "465");

		Session session = Session.getInstance(props,
		        new javax.mail.Authenticator() {
		            protected PasswordAuthentication getPasswordAuthentication() {
		                return new PasswordAuthentication(username, password);
		            }
		        });

		MimeMessage msg = new MimeMessage(session);
		try {
			msg.setSubject(m_subject);
			msg.setText(m_text);
			msg.setFrom(new InternetAddress(username));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));

			Transport transport = session.getTransport("smtps");
			transport.connect("smtp.gmail.com", Integer.valueOf("465"), "AMS", password);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();

			System.out.println("Poslao mail");
		} catch (AddressException e) {
			e.printStackTrace();
			return;
		} catch (MessagingException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("Izlazi iz maila");
		
	}

	@Override
	public void sendNemaFirmi(UserDTO user, String kategorija) {
		System.out.println("Usao u nema firmi mail");
		
		final String username = "zijdev@gmail.com";
		final String password = "mojalozinka12";
		
		String m_to = user.getEmail(), m_subject = "AMS Notification",
				m_text = "Ćao " + user.getUsername() + ",\t\n\t\n Ne možemo da ispunimo vaš zahtev.\t\n"
						+ "Ne postoje firme za traženu kategoriju posla: " + kategorija + ".\t\n\t\nPokušajte ponovo.\t\n\t\nVaš,\t\nAMS.";
		Properties props = new Properties();
		props.put("mail.smtp.user", username);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.EnableSSL.enable", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");		
		props.put("mail.smtp.socketFactory.port", "465");
		
		Session session = Session.getInstance(props,
		        new javax.mail.Authenticator() {
		            protected PasswordAuthentication getPasswordAuthentication() {
		                return new PasswordAuthentication(username, password);
		            }
		        });

		MimeMessage msg = new MimeMessage(session);
		try {
			msg.setSubject(m_subject);
			msg.setText(m_text);
			msg.setFrom(new InternetAddress(username));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));

			Transport transport = session.getTransport("smtps");
			transport.connect("smtp.gmail.com", Integer.valueOf("465"), "AMS", password);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();

			System.out.println("Poslao mail nema firmi");
		} catch (AddressException e) {
			e.printStackTrace();
			return;
		} catch (MessagingException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("Izlazi iz maila nema firmi");
	}
}
