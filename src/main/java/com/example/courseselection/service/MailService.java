package com.example.courseselection.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.courseselection.models.User;

import jakarta.mail.internet.MimeMessage;


@EnableConfigurationProperties
@ConfigurationProperties(prefix="mail-service")
@Service
public class MailService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String base;
	
	private String from;
	
	private static final String BASE_URL = "baseUrl";
	
	private static final String USER = "user";

	private final JavaMailSender javaMailSender;
	
	private final MessageSource messageSource;
	
	private final SpringTemplateEngine templateEngine;
	

	public MailService(JavaMailSender javaMailSender, MessageSource messageSource,
			SpringTemplateEngine templateEngine) {
		super();
		this.javaMailSender = javaMailSender;
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@Async
	public void sendActivationEmail(User user) {
		logger.info("Sending activation email to {}", user.getEmail());
		sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
				
	}

	@Async
	public void sendCreationEmail(User user) {
		logger.info("Sending creation email to {}", user.getEmail());
		sendEmailFromTemplate(user, "mail/creationEmail", "email.creation.title");
	}

	@Async
	public void sendPasswordReset(User user) {
		logger.info("Sending password reset email to {}", user.getEmail());
		sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
	}

	@Async
	private void sendEmailFromTemplate(User user, String template, String titleKey) {
		logger.info("The lang key is {}", user.getLangKey());
		Locale locale = Locale.forLanguageTag(user.getLangKey());
		Context context = new Context(locale);
		context.setVariable(USER, user);
		context.setVariable(BASE_URL, base);
		String content = templateEngine.process(template, context);
		logger.info("The content is {}", content);

		String subject = messageSource.getMessage(titleKey, null, locale);
		logger.info("The subject is {}", subject);
//		sendEmail(user.getEmail(), subject, content, false, true);
	}

	@Async
	private void sendEmail(String to, String subject, String content, 
			boolean isMultipart, boolean isHtml) {
		logger.info("Send email[multipart '{}' and html '{}'] to '{}' "
				+ "with subject '{}' and content='{}'",
				isMultipart, isHtml, to, subject, content);
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, 
					isMultipart, StandardCharsets.UTF_8.name());
			message.setTo(to);
			message.setFrom(from);
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			logger.info("Send email to user '{}'", to);
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.warn("Email could not be sent to user '{}': {}", to, e);
			} else {
				logger.warn("Email could not be sent to user, due to '{}': {}", to, e.getMessage());
			}
		}
	}

}
