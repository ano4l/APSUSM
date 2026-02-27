package com.apsusm.service;

import com.apsusm.model.Member;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendConfirmationEmail(Member member) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(member.getEmail());
            helper.setSubject("APSUSM Membership Confirmation - " + member.getMemberId());

            String htmlContent = buildConfirmationHtml(member);
            helper.setText(htmlContent, true);

            // Attach card images if they exist
            if (member.getCardFrontPath() != null) {
                File frontCard = new File(member.getCardFrontPath());
                if (frontCard.exists()) {
                    helper.addAttachment("APSUSM_Card_Front.png", new FileSystemResource(frontCard));
                }
            }
            if (member.getCardBackPath() != null) {
                File backCard = new File(member.getCardBackPath());
                if (backCard.exists()) {
                    helper.addAttachment("APSUSM_Card_Back.png", new FileSystemResource(backCard));
                }
            }

            mailSender.send(message);
            log.info("Confirmation email sent to: {}", member.getEmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email to: {}", member.getEmail(), e);
        }
    }

    private String buildConfirmationHtml(Member member) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background: #f5f5fa; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
                    .header { background: linear-gradient(135deg, #0a0a0a, #1a1a2e); color: white; padding: 40px 30px; text-align: center; }
                    .header h1 { margin: 0 0 5px 0; font-size: 24px; letter-spacing: 2px; }
                    .header p { margin: 0; color: #9ca3af; font-size: 13px; }
                    .content { padding: 30px; }
                    .greeting { font-size: 18px; color: #1a1a2e; margin-bottom: 15px; }
                    .info-box { background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 8px; padding: 20px; margin: 20px 0; }
                    .info-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
                    .info-label { color: #6b7280; font-size: 13px; font-weight: 600; }
                    .info-value { color: #1a1a2e; font-size: 14px; font-weight: 500; }
                    .member-id { font-size: 22px; font-weight: 700; color: #007ACC; text-align: center; padding: 15px; background: #f0f7ff; border-radius: 8px; margin: 20px 0; font-family: monospace; letter-spacing: 2px; }
                    .note { color: #6b7280; font-size: 13px; line-height: 1.6; margin-top: 20px; }
                    .footer { background: #f9fafb; padding: 20px 30px; text-align: center; color: #9ca3af; font-size: 12px; border-top: 1px solid #e5e7eb; }
                    .accent-bar { height: 4px; background: linear-gradient(90deg, #007ACC, #D71920, #8BC53F); }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="accent-bar"></div>
                    <div class="header">
                        <h1>AP+SUSM</h1>
                        <p>VERIFIED MEMBER</p>
                    </div>
                    <div class="content">
                        <p class="greeting">Welcome, Dr. %s!</p>
                        <p>Your APSUSM membership has been confirmed. You are now a verified member of the Associação dos Profissionais de Saúde Unidos e Solidários de Moçambique.</p>
                        
                        <div class="member-id">%s</div>
                        
                        <div class="info-box">
                            <div class="info-row">
                                <span class="info-label">Full Name</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">License Number</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Specialization</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Province</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row" style="border-bottom:none;">
                                <span class="info-label">Status</span>
                                <span class="info-value" style="color: #16a34a;">✓ Active Member</span>
                            </div>
                        </div>
                        
                        <p class="note">
                            Your digital membership card is attached to this email. You can also download it from your member portal at any time.
                            <br><br>
                            To verify your membership status, visit: <strong>apsusm.org/verify/%s</strong>
                        </p>
                    </div>
                    <div class="footer">
                        <p>© APSUSM – Defining the Standard</p>
                        <p>Maputo, Moçambique | info@apsusm.org</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                member.getLastName(),
                member.getMemberId(),
                member.getFullName(),
                member.getLicenseNumber(),
                member.getSpecialization() != null ? member.getSpecialization() : "N/A",
                member.getProvince() != null ? member.getProvince() : "N/A",
                member.getMemberId()
        );
    }
}
