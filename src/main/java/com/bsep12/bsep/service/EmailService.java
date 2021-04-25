package com.bsep12.bsep.service;

import com.bsep12.bsep.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private Environment env;

	@Async
	public void sendMail(User user) throws MailException {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Confirm registration to BSEP12");
		mail.setText("http://localhost:8080/auth/verity?token=" + user.getToken());
		javaMailSender.send(mail);
	}

	@Async
	public void sendMail2(User user) throws MailException {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Reset password BSEP12");
		mail.setText("http://localhost:8080/auth/reset-password?token=" + user.getToken());
		javaMailSender.send(mail);
	}

}
