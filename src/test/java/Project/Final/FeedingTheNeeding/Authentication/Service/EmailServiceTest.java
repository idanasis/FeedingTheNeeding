package Project.Final.FeedingTheNeeding.Authentication.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private final String TO = "to@gmail.com", SUBJECT = "VerificationEmail", TEXT = "<h1>Verify your email</h1>";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendVerificationEmail_Success() throws MessagingException {
        emailService.sendVerificationEmail(TO, SUBJECT, TEXT);

        verify(emailSender, times(1)).createMimeMessage();
        verify(emailSender, times(1)).send(mimeMessage);

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(TO);
        helper.setSubject(SUBJECT);
        helper.setText(TEXT, true);
    }

    @Test
    void testSendVerificationEmail_ThrowsException() throws MessagingException {
        doThrow(new org.springframework.mail.MailSendException("Email failed"))
                .when(emailSender).send(any(MimeMessage.class));

        assertThrows(MessagingException.class, () -> {
            emailService.sendVerificationEmail(TO, SUBJECT, TEXT);
        });

        verify(emailSender, times(1)).createMimeMessage();
        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }



}
