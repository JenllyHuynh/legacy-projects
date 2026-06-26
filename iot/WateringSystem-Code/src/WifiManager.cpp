#pragma once   // Đảm bảo file chỉ được biên dịch 1 lần (tránh lỗi duplicate)

#include <Arduino.h>      // Thư viện cơ bản của Arduino
#include <WiFi.h>         // Thư viện WiFi cho ESP32
#include <Preferences.h>  // Thư viện lưu trữ dữ liệu vào flash memory

class WifiManager{
private:
    Preferences preferences; // Đối tượng để lưu trữ dữ liệu vào bộ nhớ flash
    String ssid;             // Tên WiFi đã lưu
    String password;         // Mật khẩu WiFi đã lưu

public:
    // Hàm khởi tạo WiFi Manager
    WifiManager(){
        preferences.begin("wifi", false); // Mở namespace "wifi" với chế độ read/write (false = read/write)
        load(); // Tải thông tin WiFi đã lưu từ bộ nhớ flash
    }

    // Hàm hủy
    ~WifiManager(){
        preferences.end(); // Đóng kết nối với Preferences (giải phóng tài nguyên)
    }

    // Hàm tải thông tin WiFi từ bộ nhớ flash
    void load(){
        ssid     = preferences.getString("ssid", "");     // Lấy SSID từ key "ssid", mặc định là chuỗi rỗng nếu chưa có
        password = preferences.getString("password", ""); // Lấy password từ key "password", mặc định là chuỗi rỗng nếu chưa có
    }

    // Hàm lưu thông tin WiFi mới vào bộ nhớ flash
    void save(String newSSID, String newPassword) {
        // Kiểm tra thông tin WiFi có hợp lệ không (không được rỗng)
        if (newSSID.length() == 0 || newPassword.length() == 0) {
            Serial.println("Invalid WiFi credential");  // Thông báo lỗi
            return;  // Thoát khỏi hàm nếu thông tin không hợp lệ
        }
        // Lưu SSID vào bộ nhớ flash với key "ssid"
        preferences.putString("ssid", newSSID);
        // Lưu password vào bộ nhớ flash với key "password"
        preferences.putString("password", newPassword);
        // Cập nhật thông tin trong RAM (biến thành viên)
        ssid     = newSSID;
        password = newPassword;
        Serial.println("WiFi saved!");  // Thông báo lưu thành công
    }

    // Hàm kết nối đến WiFi đã lưu
    bool connectToWiFi(){
        // Kiểm tra đã có thông tin WiFi chưa
        if (ssid.length() == 0 || password.length() == 0) {
            Serial.println("WiFi not found!");  // Thông báo chưa có thông tin WiFi
            return false;  // Trả về false
        }
        // Kiểm tra đã kết nối WiFi chưa
        if (WiFi.status() == WL_CONNECTED) {
            Serial.println("Already connected to WiFi: " + ssid);  // Thông báo đã kết nối
            return true;  // Trả về true nếu đã kết nối
        }

        // Bắt đầu kết nối WiFi với SSID và password
        WiFi.begin(ssid.c_str(), password.c_str());  // .c_str() chuyển String thành const char*
        Serial.println("Connecting to WiFi: " + ssid);  // Thông báo đang kết nối

        // Chờ kết nối trong 10 giây (20 lần * 500ms = 10 giây)
        int attempts = 0;  // Biến đếm số lần thử
        while (WiFi.status() != WL_CONNECTED && attempts < 20) {
            delay(500);       // Đợi 500ms
            Serial.print("."); // In dấu chấm để hiển thị tiến trình
            attempts++;       // Tăng biến đếm
        }
        Serial.println();  // Xuống dòng sau khi kết thúc vòng lặp

        // Kiểm tra kết nối thành công hay thất bại
        if (WiFi.status() == WL_CONNECTED) {
            Serial.println("WiFi connected successfully!");  // Thông báo kết nối thành công
            Serial.println("IP address: " + WiFi.localIP().toString());  // In địa chỉ IP đã được cấp
            return true;  // Trả về true
        } else {
            Serial.println("WiFi connect failed!");  // Thông báo kết nối thất bại
            return false;  // Trả về false
        }
    }

    // Hàm tạo Access Point (AP) để người dùng kết nối vào và cấu hình
    bool accessPoint(const char* apSSID = "System setup", const char* apPASS = "12345678") {
        WiFi.disconnect();  // Ngắt kết nối WiFi hiện tại (nếu có)
        WiFi.mode(WIFI_AP); // Chuyển sang chế độ Access Point (AP)
        // Tạo Access Point với SSID và password được cung cấp
        bool ok = WiFi.softAP(apSSID, apPASS);
        if (ok) {
            // Nếu tạo AP thành công
            Serial.print("Access Point created: ");
            Serial.println(apSSID);  // In tên AP
            Serial.print("Password: ");
            Serial.println(apPASS);  // In mật khẩu AP
            Serial.print("AP IP: ");
            Serial.println(WiFi.softAPIP());  // In địa chỉ IP của AP (thường là 192.168.4.1)
            return true;  // Trả về true
        } else {
            Serial.println("Failed to create Access Point!");  // Thông báo tạo AP thất bại
            return false;  // Trả về false
        }
    }

    // Hàm lấy SSID đã lưu
    String getSSID()     { return ssid; }
    
    // Hàm lấy password đã lưu
    String getPassword() { return password; }
};