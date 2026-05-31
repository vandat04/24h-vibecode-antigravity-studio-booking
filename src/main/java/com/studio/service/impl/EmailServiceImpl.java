package com.studio.service.impl;

import com.studio.entity.Booking;
import com.studio.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
@Async
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendBookingConfirmation(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getCustomerEmail());
            helper.setSubject("✅ Xác nhận đặt lịch chụp ảnh - Mã: " + booking.getBookingCode());
            helper.setText(buildEmailContent(booking), true);

            mailSender.send(message);
            log.info("Booking confirmation email sent to: {}", booking.getCustomerEmail());

        } catch (MessagingException e) {
            log.error("Failed to send booking confirmation email to {}: {}", booking.getCustomerEmail(), e.getMessage());
            // Don't throw - email failure should not break the booking flow
        }
    }

    private String buildEmailContent(Booking booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 30px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #1a1a2e, #16213e); padding: 40px 30px; text-align: center; }
                        .header h1 { color: #fff; margin: 0; font-size: 24px; letter-spacing: 2px; }
                        .header p { color: #c9b99a; margin: 8px 0 0; font-size: 14px; }
                        .body { padding: 32px 30px; }
                        .greeting { font-size: 16px; color: #333; margin-bottom: 20px; }
                        .booking-code { background: #FFF4E4; border: 2px dashed #c9a96e; border-radius: 8px; padding: 16px; text-align: center; margin: 20px 0; }
                        .booking-code span { font-size: 26px; font-weight: bold; color: #1a1a2e; letter-spacing: 3px; }
                        .info-table { width: 100%%; border-collapse: collapse; margin-top: 24px; }
                        .info-table tr { border-bottom: 1px solid #f0f0f0; }
                        .info-table td { padding: 12px 8px; font-size: 14px; }
                        .info-table td:first-child { color: #888; width: 40%%; }
                        .info-table td:last-child { color: #222; font-weight: 600; }
                        .note { background: #fff8f0; border-left: 4px solid #c9a96e; padding: 14px 16px; margin: 24px 0; border-radius: 4px; font-size: 14px; color: #555; }
                        .footer { background: #1a1a2e; padding: 24px 30px; text-align: center; }
                        .footer p { color: #888; font-size: 12px; margin: 4px 0; }
                        .footer a { color: #c9a96e; text-decoration: none; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>STUDIO BOOKING</h1>
                            <p>Xác nhận đặt lịch chụp ảnh thành công</p>
                        </div>
                        <div class="body">
                            <p class="greeting">Xin chào <strong>%s</strong>,</p>
                            <p style="color:#555; line-height:1.7;">Chúng tôi đã nhận được yêu cầu đặt lịch chụp ảnh của bạn. Đây là thông tin chi tiết:</p>
                            
                            <div class="booking-code">
                                <p style="margin:0 0 8px; color:#888; font-size:12px;">MÃ ĐẶT LỊCH CỦA BẠN</p>
                                <span>%s</span>
                            </div>
                            
                            <table class="info-table">
                                <tr><td>📅 Ngày chụp</td><td>%s</td></tr>
                                <tr><td>⏰ Khung giờ</td><td>%s</td></tr>
                                <tr><td>📍 Địa điểm</td><td>%s</td></tr>
                                <tr><td>📦 Gói dịch vụ</td><td>%s</td></tr>
                                <tr><td>🎨 Concept</td><td>%s</td></tr>
                                <tr><td>💰 Tổng tiền</td><td>%s VNĐ</td></tr>
                                <tr><td>📊 Trạng thái</td><td style="color:#e67e22;">Đang chờ xác nhận (PENDING)</td></tr>
                            </table>
                            
                            <div class="note">
                                <strong>📌 Lưu ý quan trọng:</strong><br>
                                Vui lòng lưu lại <strong>Mã đặt lịch</strong> ở trên để tra cứu tiến độ lịch chụp của bạn tại website. Studio sẽ liên hệ xác nhận lịch trong vòng 24 giờ làm việc.
                            </div>
                        </div>
                        <div class="footer">
                            <p>© 2025 Studio Booking. Cảm ơn bạn đã tin tưởng dịch vụ của chúng tôi.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                booking.getCustomerName(),
                booking.getBookingCode(),
                booking.getShootDate().format(dateFormatter),
                booking.getShootTimeSlot().format(timeFormatter),
                booking.getShootLocation(),
                booking.getServicePackage().getPackageName(),
                booking.getConcept().getTitle(),
                String.format("%,.0f", booking.getTotalAmount())
        );
    }

    @Override
    public void sendBookingStatusUpdate(Booking booking, String note) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getCustomerEmail());
            helper.setSubject("🔔 Cập nhật trạng thái đơn đặt lịch - Mã: " + booking.getBookingCode());
            
            String content = """
                <!DOCTYPE html>
                <html lang="vi">
                <head><meta charset="UTF-8"><style>
                    body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 30px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: #1a1a2e; padding: 30px; text-align: center; color: #fff; }
                    .header h2 { margin: 0; }
                    .body { padding: 30px; line-height: 1.6; }
                    .highlight { background: #fff4e4; border: 1px solid #c9a96e; border-radius: 6px; padding: 15px; margin: 20px 0; }
                    .footer { background: #f0f0f0; padding: 15px; text-align: center; font-size: 12px; color: #888; }
                </style></head>
                <body>
                    <div class="container">
                        <div class="header"><h2>CẬP NHẬT TIẾN ĐỘ ĐƠN ĐẶT</h2></div>
                        <div class="body">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Studio xin thông báo trạng thái đơn đặt lịch của bạn (Mã: <strong>%s</strong>) đã được cập nhật:</p>
                            <div class="highlight">
                                <strong>Trạng thái mới:</strong> <span style="color:#e67e22; font-weight:bold;">%s</span><br>
                                <strong>Ghi chú từ Studio:</strong> %s
                            </div>
                            <p>Bạn có thể tra cứu trực tiếp tiến độ đầy đủ trên trang web của chúng tôi bất cứ lúc nào.</p>
                        </div>
                        <div class="footer"><p>© Studio Booking. Trân trọng cảm ơn bạn.</p></div>
                    </div>
                </body>
                </html>
                """.formatted(booking.getCustomerName(), booking.getBookingCode(), booking.getBookingStatus().name(), note != null ? note : "Không có");

            helper.setText(content, true);
            mailSender.send(message);
            log.info("Booking status update email sent to: {}", booking.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send booking status update email to {}: {}", booking.getCustomerEmail(), e.getMessage());
        }
    }

    @Override
    public void sendPaymentUpdate(Booking booking, String method) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getCustomerEmail());
            helper.setSubject("💰 Xác nhận thanh toán thành công - Mã: " + booking.getBookingCode());

            String content = """
                <!DOCTYPE html>
                <html lang="vi">
                <head><meta charset="UTF-8"><style>
                    body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 30px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: #27ae60; padding: 30px; text-align: center; color: #fff; }
                    .header h2 { margin: 0; }
                    .body { padding: 30px; line-height: 1.6; }
                    .highlight { background: #eafaf1; border: 1px solid #2ecc71; border-radius: 6px; padding: 15px; margin: 20px 0; }
                    .footer { background: #f0f0f0; padding: 15px; text-align: center; font-size: 12px; color: #888; }
                </style></head>
                <body>
                    <div class="container">
                        <div class="header"><h2>XÁC NHẬN THANH TOÁN</h2></div>
                        <div class="body">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Studio đã nhận được thanh toán thành công cho đơn đặt lịch <strong>%s</strong> của bạn:</p>
                            <div class="highlight">
                                <strong>Trạng thái thanh toán:</strong> <span style="color:#27ae60; font-weight:bold;">%s</span><br>
                                <strong>Phương thức thanh toán:</strong> %s<br>
                                <strong>Tổng số tiền:</strong> %s VNĐ
                            </div>
                            <p>Cảm ơn bạn đã hoàn thành giao dịch tài chính. Chúng tôi sẽ chuẩn bị thật tốt cho ca chụp sắp tới.</p>
                        </div>
                        <div class="footer"><p>© Studio Booking. Trân trọng cảm ơn bạn.</p></div>
                    </div>
                </body>
                </html>
                """.formatted(booking.getCustomerName(), booking.getBookingCode(), booking.getPaymentStatus().name(), method, String.format("%,.0f", booking.getTotalAmount()));

            helper.setText(content, true);
            mailSender.send(message);
            log.info("Payment update email sent to: {}", booking.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send payment update email to {}: {}", booking.getCustomerEmail(), e.getMessage());
        }
    }

    @Override
    public void sendStaffAssignment(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getCustomerEmail());
            helper.setSubject("📸 Thông tin phân công nhân sự - Mã: " + booking.getBookingCode());

            String content = """
                <!DOCTYPE html>
                <html lang="vi">
                <head><meta charset="UTF-8"><style>
                    body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 30px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: #2980b9; padding: 30px; text-align: center; color: #fff; }
                    .header h2 { margin: 0; }
                    .body { padding: 30px; line-height: 1.6; }
                    .highlight { background: #ebf5fb; border: 1px solid #3498db; border-radius: 6px; padding: 15px; margin: 20px 0; }
                    .footer { background: #f0f0f0; padding: 15px; text-align: center; font-size: 12px; color: #888; }
                </style></head>
                <body>
                    <div class="container">
                        <div class="header"><h2>THÔNG TIN NHÂN SỰ CHĂM SÓC</h2></div>
                        <div class="body">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Studio đã phân công ê kíp phục vụ tận tình cho buổi chụp của bạn (Mã: <strong>%s</strong>):</p>
                            <div class="highlight">
                                <strong>Ngày chụp:</strong> %s<br>
                                <strong>Khung giờ:</strong> %s<br>
                                <strong>Trạng thái lịch chụp:</strong> ASSIGNED
                            </div>
                            <p>Vui lòng tra cứu trực tuyến trên hệ thống để xem chi tiết thông tin và ảnh đại diện của Photographer & Makeup Artist phụ trách.</p>
                        </div>
                        <div class="footer"><p>© Studio Booking. Trân trọng cảm ơn bạn.</p></div>
                    </div>
                </body>
                </html>
                """.formatted(booking.getCustomerName(), booking.getBookingCode(), booking.getShootDate().toString(), booking.getShootTimeSlot().toString());

            helper.setText(content, true);
            mailSender.send(message);
            log.info("Staff assignment email sent to: {}", booking.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send staff assignment email to {}: {}", booking.getCustomerEmail(), e.getMessage());
        }
    }

    @Override
    public void sendPhotosDelivered(Booking booking, String editedPhotoLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getCustomerEmail());
            helper.setSubject("🎉 Đã bàn giao bộ ảnh hoàn thiện - Mã: " + booking.getBookingCode());

            String content = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 30px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #8e44ad, #2c3e50); padding: 40px 30px; text-align: center; }
                        .header h1 { color: #fff; margin: 0; font-size: 24px; letter-spacing: 2px; }
                        .header p { color: #f1c40f; margin: 8px 0 0; font-size: 14px; font-weight: bold; }
                        .body { padding: 32px 30px; }
                        .greeting { font-size: 16px; color: #333; margin-bottom: 20px; }
                        .download-btn { display: block; text-align: center; margin: 30px auto; }
                        .download-link { background: #8e44ad; color: #ffffff !important; font-size: 16px; font-weight: bold; text-decoration: none; padding: 14px 28px; border-radius: 30px; box-shadow: 0 4px 10px rgba(142,68,173,0.3); transition: all 0.3s ease; }
                        .info-table { width: 100%%; border-collapse: collapse; margin-top: 24px; }
                        .info-table tr { border-bottom: 1px solid #f0f0f0; }
                        .info-table td { padding: 12px 8px; font-size: 14px; }
                        .info-table td:first-child { color: #888; width: 40%%; }
                        .info-table td:last-child { color: #222; font-weight: 600; }
                        .note { background: #fbf5ff; border-left: 4px solid #8e44ad; padding: 14px 16px; margin: 24px 0; border-radius: 4px; font-size: 14px; color: #555; }
                        .footer { background: #1a1a2e; padding: 24px 30px; text-align: center; }
                        .footer p { color: #888; font-size: 12px; margin: 4px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>STUDIO ARTWORK</h1>
                            <p>BÀN GIAO ALBUM ẢNH CHỈNH SỬA HOÀN THIỆN</p>
                        </div>
                        <div class="body">
                            <p class="greeting">Xin chào <strong>%s</strong>,</p>
                            <p style="color:#555; line-height:1.7;">Ekip của Studio đã hoàn thành việc chỉnh sửa hình ảnh xuất sắc nhất cho ca chụp của bạn. Chúng tôi rất hân hạnh được gửi tới bạn bộ ảnh nghệ thuật hoàn thiện này!</p>
                            
                            <div class="download-btn">
                                <a href="%s" target="_blank" class="download-link">🔗 NHẤN VÀO ĐÂY ĐỂ TẢI BỘ ẢNH HOÀN THIỆN</a>
                            </div>
                            
                            <table class="info-table">
                                <tr><td>🎫 Mã đặt lịch</td><td>%s</td></tr>
                                <tr><td>📅 Ngày chụp</td><td>%s</td></tr>
                                <tr><td>📦 Gói chụp</td><td>%s</td></tr>
                                <tr><td>🎨 Concept</td><td>%s</td></tr>
                                <tr><td>📊 Trạng thái hậu kỳ</td><td style="color:#27ae60;">ĐÃ BÀN GIAO (DELIVERED)</td></tr>
                            </table>
                            
                            <div class="note">
                                <strong>📌 Hướng dẫn và lưu ý:</strong><br>
                                - Vui lòng tải xuống và sao lưu ảnh của bạn trong vòng 30 ngày để lưu trữ tốt nhất.<br>
                                - Nếu gặp bất kỳ vấn đề nào về tải ảnh hoặc muốn chỉnh sửa thêm, đừng ngần ngại liên hệ trực tiếp với hotline của Studio nhé!
                            </div>
                        </div>
                        <div class="footer">
                            <p>© 2025 Studio Booking. Trân trọng cảm ơn bạn đã lựa chọn lưu giữ kỷ niệm cùng chúng tôi!</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                booking.getCustomerName(),
                editedPhotoLink,
                booking.getBookingCode(),
                booking.getShootDate().toString(),
                booking.getServicePackage().getPackageName(),
                booking.getConcept().getTitle()
            );

            helper.setText(content, true);
            mailSender.send(message);
            log.info("Photos delivered email successfully sent to: {}", booking.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send photos delivered email to {}: {}", booking.getCustomerEmail(), e.getMessage());
        }
    }
}

