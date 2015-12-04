package com.hfjy.framework.common.util;

import java.util.Date;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;

import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.message.entity.EmailInfo;

public class EmailUtil {
	private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	public boolean sendEmail(final EmailInfo mailInfo) {
		Authenticator aut = null;
		Session sendMailSession = null;
		if (mailInfo.isVerify()) {
			aut = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailInfo.getUserName(), mailInfo.getPassword());
				}
			};
			sendMailSession = Session.getDefaultInstance(mailInfo.getProperties(), aut);
		} else {
			sendMailSession = Session.getDefaultInstance(mailInfo.getProperties());
		}
		try {
			Message mailMessage = new MimeMessage(sendMailSession);
			mailMessage.setFrom(new InternetAddress(mailInfo.getMy()));
			mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(mailInfo.getTo()));
			mailMessage.setSubject(mailInfo.getTitle());
			mailMessage.setSentDate(new Date());
			if (mailInfo.isHTML()) {
				Multipart mainPart = new MimeMultipart();
				BodyPart html = new MimeBodyPart();
				html.setContent(mailInfo.getData(), "text/html;charset=utf-8");
				mainPart.addBodyPart(html);
				mailMessage.setContent(mainPart);
			} else {
				mailMessage.setText(mailInfo.getData());
			}
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
}
