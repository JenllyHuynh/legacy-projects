package vn.edu.fpt.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.entity.Ticket;
import vn.edu.fpt.repository.TicketRepo;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Service
public class QrService {

    @Autowired
    private TicketRepo ticketRepo;

    /**
     * Sinh QR cho ticket:
     * 1. Tạo UUID ngẫu nhiên làm ticketCode (BR-62)
     * 2. Hash UUID bằng SHA-256, lưu vào DB (BR-63)
     * 3. Encode QR với ticketId + ticketCode plain (BR-59)
     */
    public byte[] generateTicketQR(Integer ticketId) throws Exception {

        // Lấy ticket từ DB
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        // Tạo ticketCode UUID ngẫu nhiên (BR-62)
        String ticketCode = UUID.randomUUID().toString();

        // Hash SHA-256 để lưu DB (BR-63)
        String ticketCodeHash = sha256(ticketCode);

        // Lưu hash vào DB
        ticket.setTicketCodeHash(ticketCodeHash);
        ticketRepo.save(ticket);

        // Build URL cho QR: chứa ticketId + ticketCode plain (BR-59)
        String checkinUrl = "http://localhost:8080/api/checkin"
                + "?t=" + ticketId
                + "&c=" + ticketCode;

        // Gen ảnh QR
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(checkinUrl, BarcodeFormat.QR_CODE, 300, 300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Hash chuỗi bằng SHA-256, trả về Base64
     */
    public static String sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}