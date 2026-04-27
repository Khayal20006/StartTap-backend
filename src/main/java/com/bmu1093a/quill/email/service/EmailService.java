package com.bmu1093a.quill.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendVerificationEmail(String to, String token) {
        String link = frontendUrl + "/verify?token=" + token;

        String html = """
                <!DOCTYPE html>
                <html>
                  <body style="margin:0;padding:0;background-color:#f3f4f6;font-family:Arial, sans-serif;">
                
                    <div style="max-width:600px;margin:40px auto;background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                
                      <!-- Header -->
                      <div style="background:#2563eb;padding:24px;text-align:center;">
                        <h1 style="margin:0;color:#ffffff;font-size:22px;">
                          StartTap Verification
                        </h1>
                      </div>
                
                      <!-- Content -->
                      <div style="padding:32px;text-align:center;color:#111827;">
                
                        <h2 style="margin-bottom:12px;font-size:20px;">
                          Verify your email address
                        </h2>
                
                        <p style="font-size:14px;color:#6b7280;line-height:1.6;">
                          Thanks for signing up! Please confirm that this email belongs to you by clicking the button below.
                        </p>
                
                        <div style="margin:28px 0;">
                          <a href="%s"
                             style="
                              background:#2563eb;
                              color:#ffffff;
                              padding:12px 24px;
                              text-decoration:none;
                              border-radius:8px;
                              font-weight:bold;
                              display:inline-block;
                              font-size:14px;
                             ">
                            Verify Email
                          </a>
                        </div>
                
                        <p style="font-size:12px;color:#9ca3af;line-height:1.5;">
                          If you didn’t create an account, you can safely ignore this email.
                        </p>
                
                      </div>
                
                      <!-- Footer -->
                      <div style="background:#f9fafb;padding:16px;text-align:center;font-size:12px;color:#9ca3af;">
                        © %d StartTap. All rights reserved.
                      </div>
                
                    </div>
                
                  </body>
                </html>
                """.formatted(link, java.time.Year.now().getValue());

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Verify your email");
            helper.setText(html, true); // true = HTML

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
