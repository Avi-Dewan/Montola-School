package com.montola.school.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author avidewan
 * @date 9/5/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessEmailService {

    private final JavaMailSender mailSender;

    public boolean sendActivationEmail(String to, String token) {
        String subject = "✅ Activate Your Account - Montola School";
        String activationLink = "http://localhost:3000/auth/activate?email=" + to + "&token=" + token;

        String html = """
            <div style="font-family:Arial, sans-serif; background-color:#f4fff4; padding:20px; border-radius:10px;">
            
                <h2 style="color:#2e7d32;">Welcome to Montola School!</h2>
                <p>Thank you for registering. Please activate your account by clicking the link below:</p>
                <a href="%s" style="display:inline-block; padding:10px 20px; background-color:#66bb6a; color:white; text-decoration:none; border-radius:5px;">Activate Account</a>
                <p style="color:#555;">This link will expire in 15 minutes.</p>
                <hr style="border:none; border-top:1px solid #2e7d32;"/>
                <p style="font-size:12px; color:#777;">Montola School &copy; 2025</p>
            </div>
            """.formatted(activationLink);

        return sendEmail(to, subject, html);
    }

    public boolean sendPasswordResetEmail(String to, String token) {
        String subject = "✅ Password Reset Request - Montola School";
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;

        String html = """
            <div style="font-family:Arial, sans-serif; background-color:#f4fff4; padding:20px; border-radius:10px;">
                <h2 style="color:#2e7d32;">Password Reset Request</h2>
                <p>Click the button below to reset your password:</p>
                <a href="%s" style="display:inline-block; padding:10px 20px; background-color:#66bb6a; color:white; text-decoration:none; border-radius:5px;">Reset Password</a>
                <p style="color:#555;">If you did not request this, please ignore this email.</p>
                <hr style="border:none; border-top:1px solid #2e7d32;"/>
                <p style="font-size:12px; color:#777;">Montola School &copy; 2025</p>
            </div>
            """.formatted(resetLink);

        return sendEmail(to, subject, html);
    }

    public boolean sendPurchaseNotification(String to, String courseName, String packageName) {
        String subject = "✅ Purchase Confirmation - Montola School";

        String html = """
            <div style="font-family:Arial, sans-serif; background-color:#f4fff4; padding:20px; border-radius:10px;">
                <h2 style="color:#2e7d32;">Thank you for your purchase!</h2>
                <p>You have successfully purchased:</p>
                <ul>
                    <li><strong>Course:</strong> %s</li>
                    <li><strong>Package:</strong> %s</li>
                </ul>
                <p>We hope you enjoy your learning experience.</p>
                <hr style="border:none; border-top:1px solid #2e7d32;"/>
                <p style="font-size:12px; color:#777;">Montola School &copy; 2025</p>
            </div>
            """.formatted(courseName, packageName);

        return sendEmail(to, subject, html);
    }

    private boolean sendEmail(String to,
                              String subject,
                              String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            return true;

        } catch (MailException | MessagingException ex) {
            log.error("Failed to send email to {}. Reason: {}", to, ex.getMessage(), ex);

            return false;
        }
    }
}