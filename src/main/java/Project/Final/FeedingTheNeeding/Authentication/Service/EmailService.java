package Project.Final.FeedingTheNeeding.Authentication.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    private static final Logger logger = LogManager.getLogger(EmailService.class);

    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        logger.info("start-send verification email to: {}", to);
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            emailSender.send(message);
            logger.info("end-send verification email to: {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new MessagingException("Failed to send email", e);
        }
    }
}