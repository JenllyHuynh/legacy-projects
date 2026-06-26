package vn.edu.fpt.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.edu.fpt.model.dto.TicketOrderDTO;
import vn.edu.fpt.model.entity.*;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    // ── Method cũ: giữ nguyên ─────────────────────────────────────────
    public void sendOtpEmail(String toEmail, String otpCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setFrom("eventgo.pro.vn@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject("Event Go — Mã xác minh email của bạn");

        String htmlContent = buildEmailTemplate().replace("{{OTP_CODE}}", otpCode);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    // ── Method mới: gửi notification event đến 1 customer ────────────

    /**
     * Gửi email thông báo sự kiện đến 1 địa chỉ email.
     * Được gọi từ NotificationService trong vòng lặp attendees.
     *
     * @param toEmail      Email người nhận
     * @param customerName Tên customer (để cá nhân hóa)
     * @param eventTitle   Tên sự kiện
     * @param title        Tiêu đề thông báo
     * @param message      Nội dung thông báo
     */
    public void sendEventNotificationEmail(
            String toEmail,
            String customerName,
            String eventTitle,
            String title,
            String message) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom("eventgo.pro.vn@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject("Event Go — " + title);

        String htmlContent = buildNotificationEmailTemplate()
                .replace("{{CUSTOMER_NAME}}", customerName != null ? customerName : "Bạn")
                .replace("{{EVENT_TITLE}}", eventTitle)
                .replace("{{NOTIF_TITLE}}", title)
                .replace("{{NOTIF_MESSAGE}}", message.replace("\n", "<br/>"));

        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }

    // ── Template cũ: giữ nguyên ───────────────────────────────────────
    private String buildEmailTemplate() {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;">
                    <div style="background-color: #4F46E5; color: white; padding: 20px; text-align: center;">
                        <h1>Event Go</h1>
                    </div>
                    <div style="padding: 20px; color: #333;">
                        <p>Chào bạn,</p>
                        <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Event Go</strong>. Để hoàn tất quá trình đăng ký, vui lòng nhập mã xác minh (OTP) dưới đây:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #4F46E5; padding: 10px 20px; border: 2px dashed #4F46E5; border-radius: 4px;">
                                {{OTP_CODE}}
                            </span>
                        </div>
                        <p style="color: #666; font-size: 14px;">Mã này sẽ hết hạn sau 2 phút. Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>
                    </div>
                    <div style="background-color: #f9fafb; padding: 15px; text-align: center; font-size: 12px; color: #999;">
                        © 2026 Event Go Team - FPT University
                    </div>
                </div>
                """;
    }

    // ── Template mới: notification email ─────────────────────────────
    private String buildNotificationEmailTemplate() {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;">
                
                    <!-- Header -->
                    <div style="background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); padding: 28px 32px; text-align: center;">
                        <h1 style="margin: 0; color: white; font-size: 22px; font-weight: 800; letter-spacing: -0.5px;">
                            🎫 Event Go
                        </h1>
                        <p style="margin: 6px 0 0; color: rgba(255,255,255,0.8); font-size: 13px;">
                            Thông báo sự kiện
                        </p>
                    </div>
                
                    <!-- Event Badge -->
                    <div style="background-color: #eff6ff; border-bottom: 1px solid #dbeafe; padding: 12px 32px;">
                        <p style="margin: 0; font-size: 12px; color: #2563eb; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px;">
                            📅 Sự kiện
                        </p>
                        <p style="margin: 4px 0 0; font-size: 15px; font-weight: 700; color: #1e3a8a;">
                            {{EVENT_TITLE}}
                        </p>
                    </div>
                
                    <!-- Body -->
                    <div style="padding: 28px 32px;">
                        <p style="margin: 0 0 20px; color: #475569; font-size: 15px;">
                            Xin chào <strong style="color: #0f172a;">{{CUSTOMER_NAME}}</strong>,
                        </p>
                
                        <!-- Notification Card -->
                        <div style="background-color: #f8fafc; border-left: 4px solid #f59e0b; border-radius: 8px; padding: 20px 24px; margin-bottom: 24px;">
                            <p style="margin: 0 0 10px; font-size: 17px; font-weight: 800; color: #0f172a;">
                                📢 {{NOTIF_TITLE}}
                            </p>
                            <p style="margin: 0; font-size: 14px; color: #475569; line-height: 1.7;">
                                {{NOTIF_MESSAGE}}
                            </p>
                        </div>
                
                        <p style="margin: 0; font-size: 13px; color: #94a3b8; line-height: 1.6;">
                            Đây là thông báo tự động từ ban tổ chức sự kiện <strong>{{EVENT_TITLE}}</strong>.<br/>
                            Vui lòng không trả lời email này.
                        </p>
                    </div>
                
                    <!-- Footer -->
                    <div style="background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 16px 32px; text-align: center;">
                        <p style="margin: 0; font-size: 12px; color: #94a3b8;">
                            © 2026 Event Go Team - FPT University
                        </p>
                    </div>
                
                </div>
                """;
    }

    public String buildPurchaseSuccessEmail(
            Customer customer,
            Order order,
            Payment payment,
            Map<Event, List<TicketOrderDTO>> eventTickets
    ) {
        Context context = new Context();
        context.setVariable("customer", customer);
        context.setVariable("order", order);
        context.setVariable("payment", payment);
        context.setVariable("eventTickets", eventTickets);
        return templateEngine.process("email/purchase-success", context);
    }

    public void sendEmailPurchaseSuccess(String to, String html) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setTo(to);
        helper.setSubject("[Event Go] Xác nhận mua vé thành công");
        helper.setText(html, true);

        mailSender.send(message);
    }

    //    GỬI MAIL KHI FAIL
    public String buildPaymentFailedEmail(
            Customer customer,
            Order order,
            Payment payment,
            Map<Event, List<TicketOrderDTO>> eventTickets
    ) {
        Context context = new Context();
        context.setVariable("customer", customer);
        context.setVariable("order", order);
        context.setVariable("payment", payment);
        context.setVariable("eventTickets", eventTickets);

        return templateEngine.process("email/purchase-failed", context);
    }

    public void sendEmailPaymentFailed(String to, String html)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                        "UTF-8"
                );

        helper.setTo(to);
        helper.setSubject("[Event Go] Thanh toán không thành công");
        helper.setText(html, true);

        mailSender.send(message);
    }
}