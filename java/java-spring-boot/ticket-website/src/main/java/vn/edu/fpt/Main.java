package vn.edu.fpt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.edu.fpt.util.SignatureUtil;

public class Main {
    public static void main(String[] args) throws Exception {
//        int[] ticketIds = {19, 20, 21, 22, 23}; // thay bằng TicketId thực
//        for (int id : ticketIds) {
//            String sig = SignatureUtil.generateSignature(String.valueOf(id));
//            System.out.println("TicketId " + id + ":");
//            System.out.println("http://localhost:8080/api/checkin?t=" + id + "&s=" + sig);
//            System.out.println();
//        }
        String rawString = "123456"; // chuỗi cần mã hóa

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(rawString);

        System.out.println("Raw: " + rawString);
        System.out.println("BCrypt: " + encoded);
    }
}
