package com.montola.school.common.service;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending business-related emails such as
 * account activation, password resets, and purchase notifications.
 *
 * This service uses {@link Resend } for sending HTML emails.
 * Logs both success and failure events for monitoring purposes.
 *
 * @author avidewan
 * @date 9/5/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessEmailService {

    private final Resend resend;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Sends an account activation email with a 15-minute expiration link.
     *
     * @param to the recipient's email address
     * @param token the activation token to include in the link
     */
    public void sendActivationEmail(String to, String token) {
        String subject = "✅ Activate Your Account - Montola School";
        String activationLink = frontendUrl + "/auth/activate?email=" + to + "&token=" + token;

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

        if (sendEmail(to, subject, html)) {
            log.info("Activation email sent successfully to {}", to);
        }
    }

    /**
     * Sends a password reset email with a 15-minute expiration link.
     *
     * @param to the recipient's email address
     * @param token the password reset token
     */
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "✅ Password Reset Request - Montola School";
        String resetLink = frontendUrl + "/auth/reset-password?email=" + to + "&token=" + token;

        String html = """
        <div style="font-family:Arial, sans-serif; background-color:#f4fff4; padding:20px; border-radius:10px;">
            <h2 style="color:#2e7d32;">Password Reset Request</h2>
            <p>Click the button below to reset your password:</p>
            <a href="%s" style="display:inline-block; padding:10px 20px; background-color:#66bb6a; color:white; text-decoration:none; border-radius:5px;">Reset Password</a>
            <p style="color:#555;">This link will expire in 15 minutes.</p>
            <p style="color:#555;">If you did not request this, please ignore this email.</p>
            <hr style="border:none; border-top:1px solid #2e7d32;"/>
            <p style="font-size:12px; color:#777;">Montola School &copy; 2025</p>
        </div>
        """.formatted(resetLink);

        if (sendEmail(to, subject, html)) {
            log.info("Password reset email sent successfully to {}", to);
        }
    }

    /**
     * Sends a purchase confirmation email to a user.
     *
     * @param to the recipient's email address
     * @param courseName the name of the purchased course
     * @param packageName the purchased package
     */
    public void sendPurchaseNotification(String to, String courseName, String packageName) {
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

        if (sendEmail(to, subject, html)) {
            log.info("Purchase notification email sent to {} for course {} and package {}", to, courseName, packageName);
        }
    }


    /**
     * Sends an HTML email using JavaMailSender.
     *
     * @param to the recipient email address
     * @param subject the email subject
     * @param htmlContent the HTML content of the email
     * @return true if the email was sent successfully, false otherwise
     */
    private boolean sendEmail(String to, String subject, String htmlContent) {
        try {

            SendEmailRequest request = SendEmailRequest.builder()
                    .from("Montola School <noreply@montolaschool.com>")
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            resend.emails().send(request);

            return true;

        } catch (Exception ex) {
            log.error("Failed to send email to {}. Reason: {}", to, ex.getMessage(), ex);
            return false;
        }
    }
}