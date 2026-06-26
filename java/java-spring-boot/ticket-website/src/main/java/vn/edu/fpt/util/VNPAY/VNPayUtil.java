package vn.edu.fpt.util.VNPAY;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

public class VNPayUtil {
    public static String hmacSHA512(String key, String data) {
        try {

            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );

            hmac512.init(secretKey);

            byte[] bytes = hmac512.doFinal(
                    data.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hash = new StringBuilder(bytes.length * 2);

            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }

            return hash.toString();

        } catch (Exception ex) {
            throw new RuntimeException("Error while hashing VNPAY", ex);
        }
    }

    public static String getQueryString(Map<String, String> params) {

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder query = new StringBuilder();

        try {

            for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {

                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);

                if (fieldValue != null && fieldValue.length() > 0) {

                    query.append(fieldName);
                    query.append("=");
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));

                    if (itr.hasNext()) {
                        query.append("&");
                    }

                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return query.toString();
    }

    public static String getIpAddress(HttpServletRequest request) {

        String ipAddress;

        try {

            ipAddress = request.getHeader("X-FORWARDED-FOR");

            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }

        } catch (Exception e) {

            ipAddress = "Invalid IP:" + e.getMessage();
        }

//        return "127.0.0.1";
        return ipAddress;
    }

    public static String getRandomNumber(int len) {

        SecureRandom random = new SecureRandom();

        String digits = "0123456789";

        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {

            sb.append(digits.charAt(random.nextInt(digits.length())));

        }

        return sb.toString();
    }

    public static String hashAllFields(Map<String, String> fields, String secretKey) {

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (String fieldName : fieldNames) {

            String fieldValue = fields.get(fieldName);

            if (fieldValue != null && fieldValue.length() > 0) {

                if (hashData.length() > 0) {
                    hashData.append('&');
                }

                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(
                        URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII)
                );

            }
        }
        // DEBUG
        System.out.println("HASH DATA = " + hashData.toString());
        System.out.println("SECRET KEY = " + secretKey);
        return hmacSHA512(secretKey, hashData.toString());
    }
}
