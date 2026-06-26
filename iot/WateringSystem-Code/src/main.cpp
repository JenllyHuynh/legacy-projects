#include <Arduino.h>                 // Thư viện cơ bản của Arduino
#include "WateringSystem.cpp"        // Include file WateringSystem (hệ thống tưới nước)
#include "WifiManager.cpp"           // Include file WifiManager (quản lý WiFi)
#include <Wire.h>                    // Thư viện giao tiếp I2C
#include <LiquidCrystal_I2C.h>       // Thư viện điều khiển LCD qua I2C

WateringSystem *wateringSystem;      // Con trỏ đến đối tượng WateringSystem (khởi tạo bằng new)
WifiManager wifiManager;             // Tạo đối tượng WifiManager toàn cục
LiquidCrystal_I2C lcd(0x27, 16, 2);  // Tạo đối tượng LCD 16x2, địa chỉ I2C là 0x27

// Hàm setup() chạy 1 lần khi ESP32 khởi động
void setup()
{
    Serial.begin(115200);  // Khởi tạo Serial với tốc độ 115200 baud
    // In thông tin khởi động ra Serial Monitor
    Serial.println("\n=================================");
    Serial.println("Smart Watering System vBeta");
    Serial.println("=================================");

    // Bật WiFi Access Point để người dùng có thể kết nối vào và cấu hình
    wifiManager.accessPoint(); // Tạo AP với SSID: "System setup", PASS: "12345678"

    // Khởi tạo đối tượng WateringSystem bằng toán tử new (cấp phát động)
    wateringSystem = new WateringSystem();
    wateringSystem->setup();  // Gọi hàm setup() của WateringSystem để khởi tạo hệ thống
    Serial.println("System initialization complete");  // Thông báo hoàn tất khởi tạo

    lcd.init();      // Khởi tạo màn hình LCD
    lcd.backlight(); // Bật đèn nền của LCD
    
    // In "SYSTEM" căn giữa trên hàng 0 của LCD
    String msg = "SYSTEM";              // Chuỗi cần in
    int col = (16 - msg.length()) / 2;  // Tính vị trí cột để căn giữa (LCD có 16 cột)
    lcd.setCursor(col, 0);              // Di chuyển con trỏ đến vị trí (col, 0)
    lcd.print(msg);                     // In chuỗi "SYSTEM"
    
    // In địa chỉ IP của Access Point trên hàng 1
    lcd.setCursor(0, 1);           // Di chuyển con trỏ đến đầu hàng 1
    lcd.print("IP: 192.168.4.1");  // In địa chỉ IP (mặc định của ESP32 AP)
}

// Hàm loop() chạy liên tục sau khi setup() hoàn thành
void loop()
{
    // Kiểm tra wateringSystem đã được khởi tạo chưa (khác nullptr)
    if (wateringSystem != nullptr)
    {
        wateringSystem->loop();  // Gọi hàm loop() của WateringSystem để xử lý logic
    }
    delay(50);  // Đợi 50ms trước khi lặp lại (giảm tải CPU)
}