package vn.edu.fpt.service.VNPAY;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import vn.edu.fpt.config.payment.VNPAY.VNPayConfig;
import vn.edu.fpt.model.dto.Bill;
import vn.edu.fpt.model.entity.Order;
import vn.edu.fpt.model.entity.Payment;
import vn.edu.fpt.service.PaymentService;
import vn.edu.fpt.util.VNPAY.VNPayUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VNPayService {

    private final VNPayConfig config;
    private final PaymentService paymentService;

    public VNPayService(VNPayConfig config, PaymentService paymentService) {
        this.config = config;
        this.paymentService = paymentService;
    }

    /**
     * Query transaction result
     */
    public String queryTransaction(String orderId, String transDate, String ipAddr) {

        try {

            String vnp_RequestId = UUID.randomUUID().toString().substring(0, 8);
            String vnp_Version = "2.1.0";
            String vnp_Command = "querydr";
            String vnp_TmnCode = config.getTmnCode();
            String vnp_TxnRef = orderId;

            String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            JsonObject vnp_Params = new JsonObject();

            vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
            vnp_Params.addProperty("vnp_Version", vnp_Version);
            vnp_Params.addProperty("vnp_Command", vnp_Command);
            vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.addProperty("vnp_TransactionDate", transDate);
            vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.addProperty("vnp_IpAddr", ipAddr);

            String hashData = String.join("|",
                    vnp_RequestId,
                    vnp_Version,
                    vnp_Command,
                    vnp_TmnCode,
                    vnp_TxnRef,
                    transDate,
                    vnp_CreateDate,
                    ipAddr,
                    vnp_OrderInfo
            );

            String secureHash = VNPayUtil.hmacSHA512(config.getSecretKey(), hashData);

            vnp_Params.addProperty("vnp_SecureHash", secureHash);
            System.out.println("PARAMS: " + vnp_Params);
            return sendRequest(vnp_Params);

        } catch (Exception e) {
            throw new RuntimeException("VNPAY Query Error", e);
        }

    }

    /**
     * Refund transaction
     */
    public String refundTransaction(
            String orderId,
            long amount,
            String transDate,
            String user,
            String ipAddr,
            String transactionType
    ) {

        try {

            String vnp_RequestId = UUID.randomUUID().toString().substring(0, 8);
            String vnp_Version = "2.1.0";
            String vnp_Command = "refund";
            String vnp_TmnCode = config.getTmnCode();

            String vnp_TxnRef = orderId;
            String vnp_Amount = String.valueOf(amount * 100);
            String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;

            String vnp_TransactionNo = "";

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            JsonObject vnp_Params = new JsonObject();

            vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
            vnp_Params.addProperty("vnp_Version", vnp_Version);
            vnp_Params.addProperty("vnp_Command", vnp_Command);
            vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.addProperty("vnp_TransactionType", transactionType);
            vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.addProperty("vnp_Amount", vnp_Amount);
            vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

            vnp_Params.addProperty("vnp_TransactionDate", transDate);
            vnp_Params.addProperty("vnp_CreateBy", user);
            vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.addProperty("vnp_IpAddr", ipAddr);

            String hashData = String.join("|",
                    vnp_RequestId,
                    vnp_Version,
                    vnp_Command,
                    vnp_TmnCode,
                    transactionType,
                    vnp_TxnRef,
                    vnp_Amount,
                    vnp_TransactionNo,
                    transDate,
                    user,
                    vnp_CreateDate,
                    ipAddr,
                    vnp_OrderInfo
            );

            String secureHash = VNPayUtil.hmacSHA512(config.getSecretKey(), hashData);

            vnp_Params.addProperty("vnp_SecureHash", secureHash);
            System.out.println("Post Data : " + vnp_Params);

            return sendRequest(vnp_Params);

        } catch (Exception e) {
            throw new RuntimeException("VNPAY Refund Error", e);
        }

    }

    /**
     * Send HTTP POST request to VNPAY
     */
    private String sendRequest(JsonObject body) throws Exception {

        URL url = new URL(config.getApiUrl());

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());

        wr.writeBytes(body.toString());

        wr.flush();

        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
//        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );

        String output;

        StringBuilder response = new StringBuilder();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }

        in.close();

        return response.toString();

    }

    public String createPaymentUrl(HttpServletRequest request, Order order) throws UnsupportedEncodingException {

        Map<String, String> vnpParams = new HashMap<>();

        // Payment object in db
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod("VNPAY");
        payment.setPaymentStatus("Pending");
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("VND");
        payment.setGatewayResponse(null);
        payment.setCreatedAt(LocalDateTime.now());
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", config.getTmnCode());
        long amount = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", payment.getCurrency());
        // Optional
        String bankCode = request.getParameter("bankCode");
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParams.put("vnp_BankCode", bankCode);
            payment.setVnp_BankCode(request.getParameter("bankCode"));
        }
        // Mã tham chiếu đơn hàng không được trùng lặp
        // Hệ thống lấy Order Id làm mã tham chiếu
        // Cũ: random mã tham chiếu
        String txnRef = VNPayUtil.getRandomNumber(12);
        payment.setTransactionId(txnRef);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", order.getOrderInfo());
        // Mã danh mục hàng hóa : 190000 - Giải trí Sáng tạo
        vnpParams.put("vnp_OrderType", "190000");
        String locate = request.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnpParams.put("vnp_Locale", locate);
        } else {
            vnpParams.put("vnp_Locale", "vn");
        }
        vnpParams.put("vnp_ReturnUrl", config.getReturnUrl());
        vnpParams.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", createDate);

        cld.add(Calendar.MINUTE, 10);
        String expireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", expireDate);

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        // Hơi khác src của VNPAY
        for (String fieldName : fieldNames) {

            String fieldValue = vnpParams.get(fieldName);

            if (fieldValue != null && fieldValue.length() > 0) {

                if (hashData.length() > 0) {
                    hashData.append('&');
                    query.append('&');
                }

                hashData.append(fieldName)
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            }

        }

        String secureHash = VNPayUtil.hmacSHA512(config.getSecretKey(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        System.out.println("PARAMS: " + vnpParams);
        System.out.println("HASH STRING: " + hashData);
        System.out.println("HASH: " + secureHash);
        System.out.println("PAYMENT URL: " + config.getPayUrl() + "?" + query.toString());

        paymentService.save(payment);
        return config.getPayUrl() + "?" + query.toString();
    }

}
