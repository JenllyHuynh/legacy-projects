package vn.edu.fpt.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SignatureUtil {
    private static final String SECRET = "Cyrene";

    public static String generateSignature(String ticketId) throws Exception {

        String data = ticketId + SECRET;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(
                SECRET.getBytes(),
                "HmacSHA256"
        );

        mac.init(keySpec);

        byte[] rawHmac = mac.doFinal(data.getBytes());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }
}
