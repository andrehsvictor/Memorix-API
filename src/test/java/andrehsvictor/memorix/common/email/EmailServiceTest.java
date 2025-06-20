package andrehsvictor.memorix.common.email;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private String testTo;
    private String testSubject;
    private String testText;
    private String testFrom;

    @BeforeEach
    void setUp() {
        testTo = "test@example.com";
        testSubject = "Test Subject";
        testText = "<h1>Test HTML Content</h1>";
        testFrom = "noreply@memorix.io";
        
        ReflectionTestUtils.setField(emailService, "from", testFrom);
    }

    @Test
    @DisplayName("Should send email successfully")
    void send_ShouldSendEmail_WhenParametersValid() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.send(testTo, testSubject, testText);

        // Then
        verify(mailSender).createMimeMessage();
        verify(mimeMessage).setFrom(testFrom);
        verify(mimeMessage).setRecipients(MimeMessage.RecipientType.TO, testTo);
        verify(mimeMessage).setSubject(testSubject);
        verify(mimeMessage).setText(testText, "utf-8", "html");
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should throw RuntimeException when email sending fails")
    void send_ShouldThrowRuntimeException_WhenSendingFails() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MessagingException("SMTP error")).when(mimeMessage).setFrom(testFrom);

        // When & Then
        assertThatThrownBy(() -> emailService.send(testTo, testSubject, testText))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to send email");

        verify(mailSender).createMimeMessage();
    }

    @Test
    @DisplayName("Should throw RuntimeException when JavaMailSender fails")
    void send_ShouldThrowRuntimeException_WhenJavaMailSenderFails() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(mimeMessage);

        // When & Then
        assertThatThrownBy(() -> emailService.send(testTo, testSubject, testText))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to send email");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}
