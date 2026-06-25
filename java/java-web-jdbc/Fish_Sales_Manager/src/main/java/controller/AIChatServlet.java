package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.GeminiService;

public class AIChatServlet extends HttpServlet {

    private GeminiService geminiService;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            geminiService = new GeminiService();
            // in ra để biết tạo có thành công chưa
            System.out.println("AIChatServlet initialized successfully");
        } catch (Exception e) {
            // Lỗi quái gì thì in ra
            System.out.println("Error initializing AIChatServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // gửi về một cái json
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String userMessage = request.getParameter("message");
        System.out.println("User message: " + userMessage);

        try {
            String aiResponse;

            if (geminiService != null) {
                // Gọi AI thật
                aiResponse = geminiService.generateText(userMessage);
            } else {
                // Fallback nếu AI service lỗi
                aiResponse = "Xin chào! Tôi là trợ lý AI của FishStore. "
                        + "Bạn hỏi: \"" + userMessage + "\". "
                        + "Hiện hệ thống AI đang bảo trì, vui lòng thử lại sau!";
            }

            String jsonResponse = "{\"response\": \"" + escapeJson(aiResponse) + "\"}";
            System.out.println("Sending response: " + jsonResponse);

            response.getWriter().write(jsonResponse);

        } catch (Exception e) {
            System.out.println("Error in AI servlet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"response\": \"Lỗi server: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
