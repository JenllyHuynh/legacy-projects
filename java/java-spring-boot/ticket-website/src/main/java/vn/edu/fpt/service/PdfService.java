package vn.edu.fpt.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.edu.fpt.model.dto.TicketOrderDTO;
import vn.edu.fpt.model.entity.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generateInvoicePdf(
            String txnRef,
            Customer customer,
            String address,
            Payment payment,
            Order order,
            Map<Event, List<TicketOrderDTO>> eventTickets,
            BigDecimal subTotal,
            DiscountCode discountCode,
            BigDecimal discountTotal
    ) {

        try {

            // ================= DATA =================
            Context context = new Context();
            context.setVariable("discountTotal", discountTotal);
            context.setVariable("subTotal", subTotal);
            context.setVariable("discountCode", discountCode);
            context.setVariable("txnRef", txnRef);
            context.setVariable("customer", customer);
            context.setVariable("address", address);
            context.setVariable("payment", payment);
            context.setVariable("order", order);
            context.setVariable("eventTickets", eventTickets);

            // ================= HTML =================
            String html =
                    templateEngine.process("payment/invoice-pdf.html", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();

            // VERY IMPORTANT (resolve fonts/images)
            builder.withHtmlContent(
                    html,
                    PdfService.class.getResource("/").toExternalForm()
            );

            // ================= FONT REGISTER =================

            // Regular
            // ===== FONT REGISTER =====

            // Regular
            builder.useFont(
                    () -> PdfService.class.getResourceAsStream(
                            "/static/fonts/font-for-pdf/Inter24pt-Regular.ttf"),
                    "Inter",
                    400,
                    PdfRendererBuilder.FontStyle.NORMAL,
                    true
            );

            // Bold
            builder.useFont(
                    () -> PdfService.class.getResourceAsStream(
                            "/static/fonts/font-for-pdf/Inter24pt-Bold.ttf"),
                    "Inter",
                    700,
                    PdfRendererBuilder.FontStyle.NORMAL,
                    true
            );

            // Italic
            builder.useFont(
                    () -> PdfService.class.getResourceAsStream(
                            "/static/fonts/font-for-pdf/Inter24pt-Italic.ttf"),
                    "Inter",
                    400,
                    PdfRendererBuilder.FontStyle.ITALIC,
                    true
            );

            // ================= RENDER =================
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }
    }
}
