package util;

import dao.LoaiCaDAO;
import model.LoaiCa;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GeminiService {

    // Khóa API cứng cho dễ debug
    private static final String API_KEY = "AIzaSyD_L-YDOjXicRQ4Gp2yfUK7avLX_qgJ7n4";

    // Model 2.5
    private static final String MODEL_NAME = "gemini-2.5-flash";

    private final LoaiCaDAO loaiCaDAO;

    public GeminiService() {
        this.loaiCaDAO = new LoaiCaDAO();
        System.out.println("GeminiService initialized with model: " + MODEL_NAME);
    }

    // Escape chuỗi để gửi JSON
    private static String quote(String text) {
        if (text == null) {
            return "\"\"";
        }
        return "\"" + text.replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    // Lấy text trả về từ Gemini JSON response
    private static String parseGeminiResponse(String json) {
        if (json == null) {
            return null;
        }
        int idx = json.indexOf("\"text\":");
        if (idx == -1) {
            return null;
        }
        idx = json.indexOf("\"", idx + 7);
        int end = json.indexOf("\"", idx + 1);
        if (end == -1) {
            return null;
        }
        return json.substring(idx + 1, end).replace("\\n", "\n");
    }

    public String generateText(String prompt) {
        HttpURLConnection conn = null;
        try {
            // Ghép context từ database
            List<LoaiCa> danhSachCa = loaiCaDAO.getAll();
            StringBuilder context = new StringBuilder("DANH SÁCH CÁ HIỆN CÓ:\n\n");
            for (LoaiCa ca : danhSachCa) {
                context.append("- ").append(ca.getTenLoaiCa())
                        .append(" | Danh mục: ").append(ca.getDanhMuc().getTenDanhMuc())
                        .append(" | Giá: ").append(String.format("%,.0f", ca.getGiaBan())).append("đ")
                        .append(" | Đơn vị: ").append(ca.getDonViTinh())
                        .append("\nMô tả: ").append(ca.getMoTa())
                        .append("\n---\n");
            }

            // Tạo prompt mẫu cho a.i
            String fullPrompt = "Bạn là trợ lý tư vấn của cửa hàng cá tươi FishStore. "
                    + "Trả lời bằng tiếng Việt, thân thiện, dễ hiểu. "
                    + "Tư vấn thêm protein, calo, fat, carb nếu khách hàng có yêu cầu."
                    + "Dưới đây là danh sách sản phẩm hiện có:\n\n"
                    + context + "\n\nCâu hỏi của khách hàng: " + prompt;

            // Gửi request
            String urlStr = "https://generativelanguage.googleapis.com/v1beta/models/" // Lấy link từ genimi 2.5
                    + MODEL_NAME + ":generateContent?key=" + API_KEY;
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Body JSON
            String jsonInput = "{ \"contents\": [{ \"parts\": [{ \"text\": " + quote(fullPrompt) + " }] }] }";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            InputStream is = (status == 200)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }

            if (status != 200) {
                return "API trả về lỗi HTTP " + status + ": " + response;
            }

            // Kết quả
            String result = parseGeminiResponse(response.toString());
            return (result != null && !result.isEmpty())
                    ? result
                    : "Không có phản hồi từ Gemini.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi gọi Gemini API: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
