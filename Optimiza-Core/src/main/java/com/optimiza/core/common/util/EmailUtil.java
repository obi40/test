package com.optimiza.core.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.optimiza.core.common.helper.Email;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * EmailUtil.java, Used To
 *
 * @author Wa'el Abu Rahmeh <waburahemh@optimizasolutions.com>
 * @since 21/05/2017
 **/
@Service("EmailUtil")
public class EmailUtil {

	@Autowired
	private JavaMailSender emailSender;
	@Autowired
	private Configuration freemarkerConfig;
	@Value("${system.from.email}")
	private String fromEmail;
	@Value("${system.website.name}")
	private String websiteName;
	@Value("${system.website.assets}")
	private String websiteAssets;

	@Async
	public void sendMail(String to, String subject, String msg) throws MailException, InterruptedException {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromEmail);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(msg);
		emailSender.send(message);

	}

	@Async
	public void sendMailWithAttachment(String to, String subject, String content, String fileName, byte[] fileBytes) {
		MimeMessage message = emailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content);
			helper.addAttachment(fileName,
					new ByteArrayResource(fileBytes));

		} catch (Exception e) {
			e.printStackTrace();
		}
		emailSender.send(message);
	}

	@Async
	public void sendMailWithAttachment(String to, String subject, String content, String fileSystemPath) {

		try {
			FileSystemResource file = new FileSystemResource(fileSystemPath);
			InputStream inputStream = file.getInputStream();
			sendMailWithAttachment(to, subject, content, file.getFilename(), IOUtils.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Async
	public void sendMailTemplate(Email email) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper;

			helper = new MimeMessageHelper(message,
					MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			//helper.addAttachment("logo.png", new ClassPathResource("/reports/images/logo.png"));
			if (!CollectionUtil.isMapEmpty(email.getAttachmentBytes())) {
				for (Map.Entry<String, byte[]> entry : email.getAttachmentBytes().entrySet()) {
					helper.addAttachment(entry.getKey(), new ByteArrayResource(entry.getValue()));
				}
			}
			Template template = freemarkerConfig.getTemplate(email.getTemplateUri());
			email.getTemplateValueMap().put("name", email.getName());
			email.getTemplateValueMap().put("adminEmail", fromEmail);
			email.getTemplateValueMap().put("logo", websiteAssets + "/images/logo/Acculab-logo-white.svg");
			String renderedHtml = FreeMarkerTemplateUtils.processTemplateIntoString(template, email.getTemplateValueMap());
			helper.setFrom(fromEmail);
			helper.setTo(email.getTo());
			helper.setSubject(websiteName);
			helper.setText(renderedHtml, true);
			emailSender.send(message);
		} catch (MessagingException | IOException | TemplateException e) {
			e.printStackTrace();
		}
	}

}
