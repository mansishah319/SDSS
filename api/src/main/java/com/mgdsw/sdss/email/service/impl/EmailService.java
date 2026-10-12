/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.email.service.impl;

import com.mgdsw.sdss.common.enums.TemplatesImages;
import com.mgdsw.sdss.common.infrastructure.exception.child.CustomDataException;
import com.mgdsw.sdss.email.dto.EmailDetails;
import com.mgdsw.sdss.email.service.EmailSender;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

import static com.mgdsw.sdss.common.enums.ErrorCodes.INTERNAL_SERVER_ERROR;
import static com.mgdsw.sdss.common.enums.ErrorCodes.TEMPLATE_ERROR;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailService implements EmailSender {

    private final FreeMarkerConfigurer freemarkerConfigurer;
    private final Environment env;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    public void sendEmail(EmailDetails mail) {
        String emailContent = this.getContentFromTemplate(mail);
        log.info("email content --> {}", emailContent);

        try {
            Properties props = buildSmtpProperties();

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(env.getProperty("spring.mail.username"), env.getProperty("spring.mail.password"));
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setSubject(mail.getSubject());

            for (String recipient : mail.getTo()) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            }

            MimeMultipart multipart = new MimeMultipart("related");
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            if (Objects.nonNull(mail.getAttachmentName()) && Objects.nonNull(mail.getAttachmentPath())) {
                File attachment = new File(mail.getAttachmentPath());
                if (attachment.exists()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(attachment);
                    attachmentPart.setFileName(mail.getAttachmentName());
                    multipart.addBodyPart(attachmentPart);
                } else {
                    log.info("Attachment path is invalid or file does not exist");
                }
            }

            // Add images as inline resources
            Arrays.stream(TemplatesImages.values()).forEach(image -> {
                Resource resource = new ClassPathResource(image.getPath());
                try {
                    MimeBodyPart imagePart = new MimeBodyPart();
                    imagePart.attachFile(resource.getFile());
                    imagePart.setHeader("Content-ID", "<" + image.getName() + ">");
                    multipart.addBodyPart(imagePart);
                } catch (Exception e) {
                    log.info("Didn't add inline image: {}", image.getName());
                }
            });

            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception e) {
            log.error("Failed to send email through SMTP: {}", e.getMessage(), e);
            throw new CustomDataException("Failed to send email. Reason: " + resolveRootMessage(e),
                    INTERNAL_SERVER_ERROR.getErrorCode());
        }
    }

    private Properties buildSmtpProperties() {
        String host = env.getProperty("spring.mail.host");
        String port = env.getProperty("spring.mail.port", "587");
        boolean startTlsEnabled = getBooleanProperty("spring.mail.properties.mail.smtp.starttls.enable", true);
        boolean startTlsRequired = getBooleanProperty("spring.mail.properties.mail.smtp.starttls.required", true);
        boolean sslConfigured = getBooleanProperty("spring.mail.properties.mail.smtp.ssl.enable", "465".equals(port));
        boolean sslEnabled = sslConfigured && !startTlsEnabled;

        Properties props = new Properties();
        props.put("mail.transport.protocol", env.getProperty("spring.mail.properties.mail.transport.protocol", "smtp"));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        props.put("mail.smtp.timeout", env.getProperty("spring.mail.properties.mail.smtp.timeout", "5000"));
        props.put("mail.smtp.connectiontimeout", env.getProperty("spring.mail.properties.mail.smtp.connectiontimeout", "5000"));
        props.put("mail.smtp.writetimeout", env.getProperty("spring.mail.properties.mail.smtp.writetimeout", "5000"));
        props.put("mail.debug", env.getProperty("spring.mail.properties.mail.debug", "false"));
        props.put("mail.smtp.starttls.enable", Boolean.toString(startTlsEnabled));
        props.put("mail.smtp.starttls.required", Boolean.toString(startTlsRequired));
        props.put("mail.smtp.ssl.enable", Boolean.toString(sslEnabled));
        props.put("mail.smtp.ssl.trust", env.getProperty("spring.mail.properties.mail.smtp.ssl.trust", Objects.nonNull(host) ? host : "smtp.gmail.com"));

        if (sslEnabled) {
            props.put("mail.smtp.socketFactory.class", env.getProperty(
                    "spring.mail.properties.mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory"));
            props.put("mail.smtp.socketFactory.port", env.getProperty(
                    "spring.mail.properties.mail.smtp.socketFactory.port",
                    port));
        }

        return props;
    }

    private boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(env.getProperty(key, Boolean.toString(defaultValue)));
    }

    private String resolveRootMessage(Exception exception) {
        Throwable cause = exception;
        while (Objects.nonNull(cause.getCause())) {
            cause = cause.getCause();
        }
        return Objects.nonNull(cause.getMessage()) ? cause.getMessage() : exception.getMessage();
    }

    private String getContentFromTemplate(EmailDetails mail) {
        try {
            Template template = freemarkerConfigurer.getConfiguration().getTemplate(mail.getTemplateName());
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.getModel());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new CustomDataException(TEMPLATE_ERROR.getMessage(), TEMPLATE_ERROR.getErrorCode());
        }
    }
}
