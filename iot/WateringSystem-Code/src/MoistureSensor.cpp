#include <Arduino.h>      // Thư viện cơ bản của Arduino
#include <Preferences.h>  // Thư viện lưu trữ dữ liệu vào flash memory
#include <stdio.h>        // Thư viện chuẩn C cho hàm sprintf

class MoistureSensor{
private:
    int sensorPin; // Chân analog kết nối cảm biến độ ẩm đất
    int threshold; // Ngưỡng độ ẩm để xác định khi nào cần tưới nước (%)

public:
    // Hàm khởi tạo cảm biến độ ẩm
    // pin: chân analog kết nối cảm biến
    // moistureThreshold: ngưỡng độ ẩm mặc định là 25%
    MoistureSensor(int pin, int moistureThreshold = 25){
        sensorPin = pin;                 // Gán chân cảm biến
        threshold = moistureThreshold;   // Gán ngưỡng độ ẩm
        pinMode(sensorPin, INPUT);       // Thiết lập chân là INPUT (đọc giá trị)
        Serial.println("MoistureSensor initialized on pin " + String(pin));  // Thông báo khởi tạo
    }

    // Phương thức đọc giá trị độ ẩm từ cảm biến
    int read(){
        // Đọc giá trị analog từ cảm biến (0-4095 trên ESP32, ADC 12-bit)
        int value = analogRead(sensorPin);
        
        // Chuyển đổi giá trị 0-4095 sang 0-100%
        // map(value, fromLow, fromHigh, toLow, toHigh)
        int percent = map(value, 0, 4095, 0, 100);
        
        // Đảo ngược giá trị vì cảm biến hoạt động ngược:
        // Giá trị cao  == đất khô (độ ẩm thấp)
        // Giá trị thấp == đất ướt (độ ẩm cao)
        percent = 100 - percent;
        
        // Giới hạn giá trị trong khoảng 0-100 (đề phòng trường hợp vượt quá)
        percent = constrain(percent, 0, 100);
        
        // In giá trị độ ẩm ra Serial để debug
        char buffer[32];  // Tạo buffer 32 ký tự
        sprintf(buffer, "Moisture: %d%%", percent);  // Format chuỗi với giá trị percent
        Serial.println(buffer);  // In chuỗi ra Serial
        
        return percent;  // Trả về giá trị độ ẩm (%)
    }

    // Phương thức kiểm tra xem cây có cần tưới nước hay không
    bool Water(){
        int moisture = read();  // Đọc giá trị độ ẩm hiện tại
        // So sánh với ngưỡng: nếu độ ẩm < ngưỡng thì cần tưới
        bool needsWater = moisture < threshold;
        // In kết quả kiểm tra
        Serial.println("Water check: " + String(needsWater ? "NEEDS water" : "NO water needed"));
        return needsWater;  // Trả về true nếu cần tưới, false nếu không cần
    }

    // Phương thức thiết lập ngưỡng độ ẩm mới
    void setThreshold(int newThreshold){
        // Kiểm tra giá trị ngưỡng có hợp lệ không (phải trong khoảng 0-100%)
        if (newThreshold >= 0 && newThreshold <= 100){
            threshold = newThreshold;  // Cập nhật ngưỡng
            Serial.println("Moisture threshold set to: " + String(threshold) + "%");  // Thông báo
        }else{
            // Thông báo lỗi nếu giá trị không hợp lệ
            Serial.println("Invalid threshold value! Must be between 0-100");
        }
    }

    // Phương thức lấy giá trị ngưỡng hiện tại
    int getThreshold() { return threshold; }

    // Hàm hủy
    ~MoistureSensor(){
        Serial.println("MoistureSensor destroyed");  // Thông báo khi đối tượng bị hủy
    }
};