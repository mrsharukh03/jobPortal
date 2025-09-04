package com.jobPortal.Service;
import com.jobPortal.DTO.AuthDTO.MailSendResult;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.senderMail}")
    private String senderMail;

    /**
     * Sends an email verification link to the user.
     *
     * @param email             The recipient's email address.
     * @param verificationLinks  The unique verification URL.
     * @return MailSendResult indicating success or failure with message.
     */
    public MailSendResult sendVerificationLink(String email, Map<String, String> verificationLinks) {
        if (email == null || email.isBlank()) {
            String error = "Email address is null or empty.";
            log.error("Failed to send verification email: {}", error);
            return new MailSendResult(false, error);
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setFrom(senderMail);
            helper.setSubject("AuthServer Email Verification - Link(s) Expire in 2 Minutes");

            // Generate dynamic HTML content for all verification links
            StringBuilder linksHtml = new StringBuilder();
            for (Map.Entry<String, String> entry : verificationLinks.entrySet()) {
                linksHtml.append("""
                <p><strong>%s:</strong> <a href="%s">%s</a></p>
                """.formatted(entry.getKey(), entry.getValue(), entry.getValue()));
            }

            String htmlContent = """
            <html>
            <body>
                <h3>Dear User,</h3>
                <p>Please use the following link(s) to verify your account:</p>
                %s
                <p>These links will expire in 2 minutes. Do not share them with anyone.</p>
                <br/>
                <p>Regards,<br/>The XPay Team</p>
            </body>
            </html>
            """.formatted(linksHtml.toString());

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Verification email sent to {}", email);
            return new MailSendResult(true, "Email sent successfully");

        } catch (Exception e) {
            String error = "Exception while sending email to %s: %s".formatted(email, e.getMessage());
            log.error(error, e);
            return new MailSendResult(false, error);
        }
    }

}
