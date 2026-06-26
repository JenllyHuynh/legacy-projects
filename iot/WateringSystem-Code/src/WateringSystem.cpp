// ===== INCLUDE CÁC THƯ VIỆN CẦN THIẾT =====
#include <Arduino.h>       // Thư viện cơ bản Arduino
#include <ArduinoJson.h>   // Thư viện xử lý JSON (để giao tiếp API)
#include <stdio.h>         // Thư viện C chuẩn
#include <WiFi.h>          // Thư viện WiFi ESP32
#include <Preferences.h>   // Thư viện lưu trữ flash memory
#include <HTTPClient.h>    // Thư viện HTTP client (gọi API)
#include <WebServer.h>     // Thư viện tạo web server

// ===== INCLUDE CÁC CLASS TỰ TẠO =====
#include "WifiManager.cpp"      // Quản lý kết nối WiFi
#include "MoistureSensor.cpp"   // Cảm biến độ ẩm
#include "WaterPump.cpp"        // Điều khiển máy bơm
#include "WeatherAPI.cpp"       // API thời tiết

// ===== CLASS CHÍNH - BỘ NÃO CỦA HỆ THỐNG =====
class WateringSystem
{
private:
    // ===== CÁC ĐỐI TƯỢNG THÀNH PHẦN =====
    WifiManager *wifiManager;       // Con trỏ đến đối tượng quản lý WiFi
    MoistureSensor *moistureSensor; // Con trỏ đến cảm biến độ ẩm
    WaterPump *waterPump;           // Con trỏ đến máy bơm nước
    WeatherAPI *weatherAPI;         // Con trỏ đến API thời tiết
    WebServer *configServer;        // Con trỏ đến web server (phục vụ giao diện web)
    Preferences systemPrefs;        // Đối tượng lưu trữ cấu hình hệ thống vào flash

    // ===== CÁC BIẾN TRẠNG THÁI HỆ THỐNG =====
    unsigned long lastCheck; // Lưu thời điểm kiểm tra cuối cùng (milliseconds)
    int checkInterval;       // Khoảng thời gian giữa các lần kiểm tra (milliseconds)
    bool manualMode;         // Chế độ thủ công (true) hay tự động (false)
    int moistureThreshold;   // Ngưỡng độ ẩm để quyết định tưới nước (%)
    String location;         // Vị trí/thành phố hiện tại

public:
    // ===== HÀM KHỞI TẠO HỆ THỐNG =====
    WateringSystem()
    {
        // Tạo các đối tượng thành phần (cấp phát động bằng new)
        wifiManager = new WifiManager();         // Tạo đối tượng quản lý WiFi
        moistureSensor = new MoistureSensor(34); // Khởi tạo cảm biến độ ẩm ở chân GPIO 34 (analog)
        waterPump = new WaterPump(26);           // Khởi tạo máy bơm ở chân GPIO 26 (digital)
        weatherAPI = new WeatherAPI();           // Tạo đối tượng API thời tiết
        configServer = new WebServer(80);        // Tạo web server ở cổng 80 (HTTP)

        // Mở namespace "System" trong flash memory để lưu/đọc cấu hình
        systemPrefs.begin("System", false); // false = read/write mode

        // Khởi tạo các biến trạng thái
        lastCheck = 0;                                                   // Chưa có lần kiểm tra nào
        checkInterval = 30 * 60 * 1000;                                  // Kiểm tra mỗi 30 phút (30*60*1000 ms)
        manualMode = false;                                              // Mặc định là chế độ tự động
        moistureThreshold = systemPrefs.getInt("moistureThreshold", 25); // Lấy ngưỡng đã lưu, mặc định 25%
        location = "Cái Răng";                                           // Vị trí mặc định
    }
    
    // ===== HÀM SETUP - CHẠY 1 LẦN KHI KHỞI ĐỘNG =====
    void setup()
    {
        Serial.begin(115200);                          // Khởi động Serial với tốc độ 115200 baud
        Serial.println("Watering System Starting..."); // Thông báo bắt đầu

        // Thiết lập API key cố định cho OpenWeatherMap
        weatherAPI->setApiKey("30d4741c779ba94c470ca1f63045390a");
        Serial.println("Weather API configured with fixed API key");

        Serial.println("Attempting Wifi connection...");
        // Thử kết nối WiFi với thông tin đã lưu
        if (wifiManager->connectToWiFi())
        {
            // ===== KẾT NỐI WIFI THÀNH CÔNG =====
            Serial.println("Wifi connected successfully!");
            delay(5000); // Chờ 5 giây để WiFi ổn định
            
            // Tự động lấy vị trí từ địa chỉ IP
            getLocationFromIP();
            
            // Khởi động chế độ hoạt động chính (Dashboard)
            startMainMode();
        }
        else
        {
            // ===== KẾT NỐI WIFI THẤT BẠI =====
            Serial.println("Wifi connection failed...");
            // Chuyển sang chế độ cấu hình (Access Point) để user nhập WiFi
            startConfigMode();
        }
        Serial.println("WateringSystem setup done");
    }
    
    // ===== HÀM LẤY VỊ TRÍ TỪ ĐỊA CHỈ IP =====
    // Sử dụng API ipinfo.io để lấy thông tin vị trí dựa vào IP công cộng
    void getLocationFromIP()
    {
        // Kiểm tra WiFi đã kết nối chưa
        if (WiFi.status() == WL_CONNECTED) {
            Serial.println("Getting location from IP...");
            
            HTTPClient http;  // Tạo đối tượng HTTP client
            http.begin("http://ipinfo.io/json");  // Gọi API ipinfo.io
            int httpCode = http.GET();  // Thực hiện GET request
            
            // Kiểm tra mã trạng thái HTTP
            if (httpCode == 200) {
                String payload = http.getString();  // Lấy nội dung JSON trả về
                Serial.println("IP info response: " + payload);
                
                // Parse JSON
                DynamicJsonDocument doc(1024);  // Tạo JSON document 1KB
                DeserializationError error = deserializeJson(doc, payload);
                
                if (!error) {
                    // Lấy thông tin city và region từ JSON
                    String city = doc["city"];      // Tên thành phố
                    String region = doc["region"];  // Tên vùng/tỉnh
                    
                    // Ưu tiên lấy city, nếu không có thì lấy region
                    if (city.length() > 0) {
                        location = city;
                        Serial.println("Detected location: " + location);
                    } else if (region.length() > 0) {
                        location = region;
                        Serial.println("Detected location: " + location);
                    }
                    
                    // Thiết lập thành phố cho API thời tiết
                    weatherAPI->setCity(location);
                } else {
                    Serial.println("JSON parsing error for IP info");
                }
            } else {
                Serial.println("IP info API error: " + String(httpCode));
            }
            
            http.end();  // Đóng kết nối HTTP
        }
    }
    
    // ===== HÀM THIẾT LẬP CÁC ROUTE CHO WEB SERVER CẤU HÌNH =====
    // Chế độ này chỉ để nhập WiFi lần đầu
    void setupConfigServer()
    {
        // Route "/" - Trang chủ cấu hình (form nhập WiFi)
        configServer->on("/", [this]()
                         { handleConfigPage(); });
        
        // Route "/save" - Xử lý khi user submit form (lưu WiFi)
        configServer->on("/save", HTTP_POST, [this]()
                         { handleSaveConfig(); });

        Serial.println("Config server router setup done");
    }
    
    // ===== HÀM THIẾT LẬP CÁC ROUTE CHO WEB SERVER CHÍNH =====
    // Chế độ này là Dashboard điều khiển hệ thống
    void setupMainServer()
    {
        // Route "/" - Trang Dashboard chính (giao diện người dùng)
        configServer->on("/", [this]()
                         { handleDashboard(); });
        
        // Route "/api/status" - API lấy trạng thái hệ thống (JSON)
        configServer->on("/api/status", [this]()
                         { handleGetStatus(); });
        
        // Route "/api/water" - API tưới nước thủ công (POST)
        configServer->on("/api/water", HTTP_POST, [this]()
                         { handleManualWater(); });
        
        // Route "/api/settings" - API cập nhật cài đặt (POST)
        configServer->on("/api/settings", HTTP_POST, [this]()
                         { handleUpdateSettings(); });
        
        // Route "/api/mode" - API chuyển đổi chế độ AUTO/MANUAL (POST)
        configServer->on("/api/mode", HTTP_POST, [this]()
                         { handleToggleMode(); });
        
        // Route "/style.css" - File CSS cho giao diện
        configServer->on("/style.css", [this]()
                         { handleCSS(); });
        
        // Route "/script.js" - File JavaScript cho giao diện
        configServer->on("/script.js", [this]()
                         { handleJS(); });

        Serial.println("Main server router setup done");
    }
    
    // ===== HÀM LOOP - CHẠY LIÊN TỤC =====
    void loop()
    {
        // Xử lý các request HTTP đến web server
        configServer->handleClient();

        // ===== DEBUG: In trạng thái hệ thống mỗi 10 giây =====
        static unsigned long lastDebug = 0;  // Biến static giữ giá trị giữa các lần gọi
        if (millis() - lastDebug >= 10000)   // Nếu đã qua 10 giây
        {
            // In thông tin WiFi, IP và bộ nhớ còn trống
            Serial.println("System Status - WiFi: " + String(WiFi.status() == WL_CONNECTED ? "Connected" : "Disconnected") +
                           " | IP: " + WiFi.localIP().toString() +
                           " | Free Memory: " + String(esp_get_free_heap_size()) + " bytes");
            lastDebug = millis();  // Cập nhật thời điểm debug
        }

        // ===== CHẾ ĐỘ TỰ ĐỘNG - Kiểm tra và tưới nước tự động =====
        // Chỉ chạy khi: WiFi đã kết nối VÀ đang ở chế độ AUTO
        if (WiFi.status() == WL_CONNECTED && !manualMode)
        {
            checkAndWater();  // Gọi hàm kiểm tra và tưới
        }
    }

    // ===== HÀM KIỂM TRA VÀ TƯỚI NƯỚC TỰ ĐỘNG =====
    // Hàm này liên tục đọc cảm biến và quyết định bật/tắt máy bơm
    void checkAndWater()
    {
        int moisture = moistureSensor->read();  // Đọc độ ẩm hiện tại (%)

        // ===== KIỂM TRA: Độ ẩm thấp hơn ngưỡng -> BẬT MÁY BƠM =====
        if (moisture < moistureThreshold)
        {
            // Chỉ bật nếu máy bơm chưa chạy (tránh bật lại nhiều lần)
            if (!waterPump->getRun())
            {
                waterPump->turnOn();  // Bật máy bơm
                Serial.println("AUTO: Moisture " + String(moisture) + "% < " + String(moistureThreshold) + "% -> Pump ON");
            }
        }
        // ===== KIỂM TRA: Độ ẩm đủ -> TẮT MÁY BƠM =====
        else
        {
            // Chỉ tắt nếu máy bơm đang chạy (tránh tắt lại nhiều lần)
            if (waterPump->getRun())
            {
                waterPump->turnOff();  // Tắt máy bơm
                Serial.println("AUTO: Moisture " + String(moisture) + "% >= " + String(moistureThreshold) + "% -> Pump OFF");
            }
        }
    }

    // ===== HÀM DỪNG MÁY BƠM KHẨN CẤP =====
    // Tắt máy bơm ngay lập tức (có thể gọi từ bên ngoài nếu cần)
    void emergencyStopPump()
    {
        waterPump->turnOff();
        Serial.println("Emergency pump stop activated");
    }
    
    // ===== HÀM LẤY TRẠNG THÁI HỆ THỐNG DẠNG CHUỖI =====
    // Trả về một chuỗi mô tả trạng thái (để debug hoặc hiển thị LCD)
    String getSystemStatus()
    {
        int moisture = moistureSensor->read();     // Đọc độ ẩm
        bool pumpRunning = waterPump->getRun();    // Lấy trạng thái máy bơm
        bool wifiConnected = WiFi.status() == WL_CONNECTED;  // Kiểm tra WiFi

        // Ghép chuỗi và trả về
        return "Moisture: " + String(moisture) + "% | Pump: " +
               (pumpRunning ? "ON" : "OFF") + " | WiFi: " +
               (wifiConnected ? "Connected" : "Disconnected");
    }
    
    // ===== HANDLER: TRANG DASHBOARD CHÍNH =====
    // Tạo HTML cho trang điều khiển chính
    void handleDashboard()
    {
        // ===== LẤY THÔNG TIN THỜI TIẾT =====
        String weatherCondition, weatherDescription;
        float temperature;
        int humidity, clouds;
        
        // Gọi API thời tiết
        bool weatherSuccess = weatherAPI->getCurrentWeather(weatherCondition, weatherDescription, temperature, humidity, clouds);
        
        // Khởi tạo giá trị mặc định
        String weatherDisplay = "Không tìm thấy thông tin thời tiết";
        String tempDisplay = "N/A";
        String humidityDisplay = "N/A";
        
        // Nếu lấy thời tiết thành công, cập nhật giá trị
        if (weatherSuccess) {
            weatherDisplay = weatherDescription;          // Mô tả thời tiết
            tempDisplay = String(temperature, 1) + "°C";  // Nhiệt độ (1 số thập phân)
            humidityDisplay = String(humidity) + "%";     // Độ ẩm không khí
        }

        // ===== TẠO HTML DASHBOARD =====
        // Sử dụng Raw String Literal R"(...)" để dễ viết HTML
        String html = R"(
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng điều khiển hệ thống</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🌱 Hệ thống tưới cây tự động</h1>
            <div class="status-badge" id="systemStatus">Đang tải...</div>
        </div>
        
        <div class="dashboard-grid">
            <!-- Thẻ hiển thị độ ẩm đất -->
            <div class="card">
                <h3>💧 Độ ẩm đất</h3>
                <div class="big-number" id="moistureValue">--</div>
                <div class="moisture-bar">
                    <div class="moisture-fill" id="moistureFill"></div>
                </div>
            </div>
            
            <!-- Thẻ hiển thị thời tiết -->
            <div class="card">
                <h3>🌤️ Thời tiết - )" + location + R"(</h3>
                <div class="weather-info">
                    <div class="weather-main">)" + weatherDisplay + R"(</div>
                    <div class="weather-details">
                        <div>🌡️ Nhiệt độ: )" + tempDisplay + R"(</div>
                        <div>💦 Độ ẩm: )" + humidityDisplay + R"(</div>
                    </div>
                </div>
                <div id="currentRainStatus" class="rain-status">--</div>
            </div>
            
            <!-- Thẻ điều khiển máy bơm -->
            <div class="card">
                <h3>⚙️ Điều khiển máy bơm</h3>
                <div class="pump-status" id="pumpStatus">OFF</div>
                <button id="manualWaterBtn" class="btn btn-primary">💧 Tưới thủ công</button>
                <button id="toggleModeBtn" class="btn btn-secondary">🔄 Chuyển đổi chế độ</button>
            </div>
            
            <!-- Thẻ cài đặt -->
            <div class="card">
                <h3>⚙️ Cài đặt</h3>
                <div class="setting-item">
                    <label>Ngưỡng độ ẩm:</label>
                    <input type="range" id="thresholdSlider" min="10" max="80" step="5" value=")" +
                      String(moistureThreshold) + R"(">
                    <span id="thresholdValue">)" +
                      String(moistureThreshold) + R"(%</span>
                </div>
                <div class="setting-item">
                    <label>Chế độ:</label>
                    <span id="currentMode">AUTO</span>
                </div>
                <button id="saveSettingsBtn" class="btn btn-success">💾 Lưu cài đặt</button>
            </div>
        </div>
        
        <!-- Logs hệ thống -->
        <div class="card">
            <h3>📋 Lịch sử hệ thống</h3>
            <div id="systemLog" class="log-container">
                <div class="log-entry">Hệ thống khởi động thành công</div>
            </div>
        </div>
    </div>

    <script src="/script.js"></script>
</body>
</html>
        )";
        // Gửi HTML về client
        configServer->send(200, "text/html", html);
    }
    
    // ===== HANDLER: API LẤY TRẠNG THÁI (JSON) =====
    // Trả về trạng thái hệ thống dưới dạng JSON để JavaScript cập nhật giao diện
    void handleGetStatus()
    {
        Serial.println("Status API requested");
        
        // ===== ĐỌC DỮ LIỆU TỪ CÁC CẢM BIẾN =====
        int moisture = moistureSensor->read();     // Độ ẩm đất (%)
        bool isRaining = weatherAPI->Rain();       // Có mưa không?
        bool pumpRunning = waterPump->getRun();    // Máy bơm có đang chạy không?

        // Lấy thông tin thời tiết chi tiết
        String weatherCondition, weatherDescription;
        float temperature;
        int humidity, clouds;
        
        bool weatherSuccess = weatherAPI->getCurrentWeather(weatherCondition, weatherDescription, temperature, humidity, clouds);

        // ===== TẠO JSON RESPONSE =====
        DynamicJsonDocument doc(2048);  // Tạo JSON document 2KB
        
        // Thêm các field vào JSON
        doc["moisture"] = moisture;                      // Độ ẩm đất
        doc["threshold"] = moistureThreshold;            // Ngưỡng độ ẩm
        doc["pump_running"] = pumpRunning;               // Trạng thái máy bơm
        doc["manual_mode"] = manualMode;                 // Chế độ MANUAL hay AUTO
        doc["rain_expected"] = isRaining;                // Có mưa không
        doc["wifi_connected"] = WiFi.status() == WL_CONNECTED;  // Trạng thái WiFi
        doc["uptime"] = millis() / 1000;                 // Thời gian hoạt động (giây)
        doc["location"] = location;                      // Vị trí hiện tại
        
        // ===== THÊM THÔNG TIN THỜI TIẾT VÀO JSON =====
        if (weatherSuccess) {
            doc["weather_condition"] = weatherCondition;      // Tình trạng (Rain, Clear,...)
            doc["weather_description"] = weatherDescription;  // Mô tả chi tiết
            doc["temperature"] = temperature;                 // Nhiệt độ
            doc["humidity"] = humidity;                       // Độ ẩm không khí
            doc["clouds"] = clouds;                           // Độ che phủ mây
        } else {
            // Nếu không lấy được thời tiết, trả về giá trị mặc định
            doc["weather_condition"] = "Unknown";
            doc["weather_description"] = "Không tìm thấy thông tin thời tiết";
            doc["temperature"] = 0;
            doc["humidity"] = 0;
            doc["clouds"] = 0;
        }

        // ===== CHUYỂN JSON THÀNH CHUỖI VÀ GỬI VỀ CLIENT =====
        String response;
        serializeJson(doc, response);  // Chuyển JSON thành chuỗi

        configServer->send(200, "application/json", response);  // Gửi với Content-Type: application/json

        Serial.println("Status requested: Moisture=" + String(moisture) + "%, Pump=" + (pumpRunning ? "ON" : "OFF"));
    }
    
    // ===== HANDLER: API TƯỚI NƯỚC THỦ CÔNG =====
    // Xử lý khi user nhấn nút "Tưới thủ công" trên giao diện
    void handleManualWater()
    {
        Serial.println("Manual watering requested");
        
        // ===== KIỂM TRA THAM SỐ DURATION (Thời gian tưới) =====
        if (configServer->hasArg("duration"))
        {
            int duration = configServer->arg("duration").toInt();  // Lấy giá trị duration từ POST
            
            // Giới hạn thời gian tưới trong khoảng 10-60 giây
            if (duration < 1000)  duration = 10000;  // Tối thiểu 10 giây
            if (duration > 60000) duration = 60000;  // Tối đa 60 giây

            Serial.println("Manual watering started for " + String(duration / 1000) + " seconds");

            // ===== BẬT MÁY BƠM, ĐỢI, RỒI TẮT =====
            waterPump->turnOn();   // Bật máy bơm
            delay(duration);       // Chờ (blocking - máy sẽ dừng trong thời gian này)
            waterPump->turnOff();  // Tắt máy bơm

            // Gửi response JSON thành công
            configServer->send(200, "application/json", "{\"status\":\"success\",\"message\":\"Watering completed\"}");
        }
        else
        {
            // ===== KHÔNG CÓ THAM SỐ -> TƯỚI MẶC ĐỊNH 20 GIÂY =====
            Serial.println("Manual watering started (10 seconds)");
            waterPump->turnOn();   // Bật máy bơm
            delay(20000);          // Chờ 20 giây
            waterPump->turnOff();  // Tắt máy bơm

            configServer->send(200, "application/json", "{\"status\":\"success\",\"message\":\"Watering completed\"}");
        }
    }
    
    // ===== HANDLER: API CẬP NHẬT CÀI ĐẶT =====
    // Xử lý khi user thay đổi cài đặt và nhấn "Lưu"
    void handleUpdateSettings()
    {
        Serial.println("Setting update requested");
        bool updated = false;  // Cờ đánh dấu có thay đổi hay không
        
        // ===== CẬP NHẬT NGƯỠNG ĐỘ ẨM =====
        if (configServer->hasArg("threshold"))
        {
            int newThreshold = configServer->arg("threshold").toInt();  // Lấy giá trị mới
            
            // Kiểm tra giá trị hợp lệ (10-80%)
            if (newThreshold >= 10 && newThreshold <= 80)
            {
                moistureThreshold = newThreshold;  // Cập nhật biến trong RAM
                moistureSensor->setThreshold(newThreshold);  // Cập nhật trong cảm biến

                systemPrefs.putInt("moistureThreshold", newThreshold);  // Lưu vào flash

                updated = true;
                Serial.println("Moisture threshold updated to: " + String(newThreshold) + "%");
            }
        }

        // ===== CẬP NHẬT KHOẢNG THỜI GIAN KIỂM TRA =====
        if (configServer->hasArg("interval"))
        {
            int newInterval = configServer->arg("interval").toInt();  // Lấy giá trị mới (phút)
            
            // Kiểm tra giá trị hợp lệ (5 phút đến 3 giờ)
            if (newInterval >= 5 && newInterval <= 180)
            {
                checkInterval = newInterval * 60 * 1000;  // Chuyển phút sang milliseconds
                systemPrefs.putInt("interval", newInterval);  // Lưu vào flash
                updated = true;
                Serial.println("Check interval updated to: " + String(newInterval) + " minutes");
            }
        }

        // ===== GỬI RESPONSE =====
        if (updated)
        {
            // Cập nhật thành công
            configServer->send(200, "application/json", "{\"status\":\"success\",\"message\":\"Settings updated\"}");
        }
        else
        {
            // Tham số không hợp lệ
            configServer->send(400, "application/json", "{\"status\":\"error\",\"message\":\"Invalid parameters\"}");
        }
    }
    
    // ===== HANDLER: API CHUYỂN ĐỔI CHẾ ĐỘ AUTO/MANUAL =====
    // Xử lý khi user nhấn nút "Chuyển đổi chế độ"
    void handleToggleMode()
    {
        manualMode = !manualMode;  // Đảo ngược chế độ (true <-> false)
        systemPrefs.putBool("mode", manualMode);  // Lưu vào flash

        String mode = manualMode ? "MANUAL" : "AUTO";  // Chuyển thành chuỗi
        Serial.println("Mode switched to: " + mode);

        // Gửi response JSON
        configServer->send(200, "application/json", "{\"status\":\"success\",\"mode\":\"" + mode + "\"}");
    }
    
    // ===== HÀM KHỞI ĐỘNG CHẾ ĐỘ CÁU HÌNH (Access Point) =====
    // Chế độ này dùng khi chưa có WiFi hoặc kết nối thất bại
    void startConfigMode()
    {
        Serial.println("Starting config mode...");
        wifiManager->accessPoint();    // Tạo Access Point (ESP32 thành router WiFi)
        setupConfigServer();           // Thiết lập các route cho web server cấu hình
        configServer->begin();         // Khởi động web server

        Serial.println("Config Mode started successfully!");
        Serial.println("Connect to WiFi: System setup");
        Serial.println("http://" + WiFi.softAPIP().toString());  // In địa chỉ IP của AP (thường là 192.168.4.1)
    }
    
    // ===== HÀM KHỞI ĐỘNG CHẾ ĐỘ HOẠT ĐỘNG CHÍNH =====
    // Chế độ này chạy khi đã kết nối WiFi thành công
    void startMainMode()
    {
        Serial.println("Starting main mode...");
        setupMainServer();       // Thiết lập các route cho Dashboard chính
        configServer->begin();   // Khởi động web server
        lastCheck = millis();    // Ghi nhận thời điểm bắt đầu

        Serial.println("Web server started successfully!");
        Serial.println("http://" + WiFi.localIP().toString());      // In URL truy cập Dashboard
        Serial.println("IP Address: " + WiFi.localIP().toString()); // In địa chỉ IP
        Serial.println("Signal: " + String(WiFi.RSSI()) + " dBm");  // In cường độ tín hiệu WiFi (RSSI)
    }
    
    // ===== HANDLER: FILE CSS =====
    // Trả về file CSS cho giao diện web
    // NOTE: Không cần comment chi tiết CSS vì bạn đã nói không cần
    void handleCSS()
    {
        String css = R"(
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f2f5; line-height: 1.6; }
.container { max-width: 1200px;  margin: 0 auto; padding: 20px; }
.header    { text-align: center; margin-bottom: 30px; }
.header h1 { color: #2e8b57;   margin-bottom: 10px; }
.status-badge   { display: inline-block; padding: 8px 16px; border-radius: 20px; font-weight: bold; }
.status-online  { background: #d4edda; color: #155724; }
.status-offline { background: #f8d7da; color: #721c24; }
.dashboard-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-bottom: 20px; }
.card    { background: white;   padding: 25px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.card h3 { margin-bottom: 15px; color: #333; }
.big-number    { font-size: 3em; font-weight: bold; color: #2e8b57; text-align: center;  margin: 10px 0; }
.moisture-bar  { width: 100%;    height: 20px; background: #e0e0e0; border-radius: 10px; overflow: hidden; margin-top: 10px; }
.moisture-fill { height: 100%;   background: linear-gradient(90deg, #ff6b6b, #ffd93d, #6bcf7f); transition: width 0.5s ease; }
.btn           { padding: 12px 24px; border: none; border-radius: 8px; cursor: pointer; font-size: 16px; font-weight: bold; margin: 5px; transition: all 0.3s; width: 100%; }
.btn-primary   { background: #007bff; color: white; }
.btn-secondary { background: #6c757d; color: white; }
.btn-success   { background: #28a745; color: white; }
.btn:hover     { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.2); }
.pump-status   { font-size: 1.5em; font-weight: bold; text-align: center; padding: 10px; margin: 10px 0; border-radius: 8px; }
.pump-on       { background: #d1ecf1; color: #0c5460; }
.pump-off      { background: #f8d7da; color: #721c24; }
.setting-item                     { margin: 15px 0; }
.setting-item label               { display: block; margin-bottom: 5px; font-weight: bold; }
.setting-item input[type="range"] { width: 80%; }
.log-container { max-height: 200px;  overflow-y: auto; background: #f8f9fa; padding: 15px; border-radius: 8px; font-family: monospace; }
.log-entry     { margin: 5px 0;      padding: 5px;     border-left: 3px solid #007bff;     background: white; }
.rain-status   { text-align: center; padding: 10px;    margin: 10px 0; border-radius: 8px;   font-weight: bold; }
.rain-yes { background: #cce5ff; color: #004085; }
.rain-no  { background: #fff3cd; color: #856404; }
.weather-info { margin: 10px 0; }
.weather-main { font-size: 1.2em; font-weight: bold; text-align: center; margin-bottom: 10px; }
.weather-details { font-size: 0.9em; line-height: 1.4; }
.weather-details div { margin: 5px 0; }
        )";
        configServer->send(200, "text/css", css);  // Gửi với Content-Type: text/css
    }

    // ===== HANDLER: FILE JAVASCRIPT =====
    // Trả về file JavaScript cho giao diện web
    void handleJS()
    {
        String js = R"(
// ===== BIẾN TOÀN CỤC =====
let updateInterval;  // Biến lưu interval ID để cập nhật định kỳ

// ===== HÀM CẬP NHẬT TRẠNG THÁI TỪ API =====
// Hàm này gọi API /api/status và cập nhật giao diện
function updateStatus(){
    // Gọi API bằng fetch()
    fetch("/api/status")
        .then(response => response.json())  // Parse JSON từ response
        .then(data =>{
            // ===== CẬP NHẬT ĐỘ ẨM ĐẤT =====
            document.getElementById("moistureValue").textContent = data.moisture + "%";  // Hiển thị giá trị
            document.getElementById("moistureFill").style.width  = data.moisture + "%";  // Cập nhật thanh progress
            
            // ===== CẬP NHẬT TRẠNG THÁI MÁY BƠM =====
            const pumpStatus       = document.getElementById("pumpStatus");
            pumpStatus.textContent = data.pump_running ? "ON" : "OFF";  // Hiển thị ON/OFF
            pumpStatus.className   = data.pump_running ? "pump-status pump-on" : "pump-status pump-off";  // Thay đổi màu
            
            // ===== CẬP NHẬT CHẾ ĐỘ =====
            document.getElementById("currentMode").textContent = data.manual_mode ? "MANUAL" : "AUTO";
            
            // ===== CẬP NHẬT TRẠNG THÁI HỆ THỐNG (ONLINE/OFFLINE) =====
            const statusBadge       = document.getElementById("systemStatus");
            statusBadge.textContent = data.wifi_connected ? "ONLINE" : "OFFLINE";
            statusBadge.className   = data.wifi_connected ? "status-badge status-online" : "status-badge status-offline";
            
            // ===== CẬP NHẬT THÔNG TIN THỜI TIẾT =====
            const currentRainStatus = document.getElementById("currentRainStatus");
            currentRainStatus.textContent = data.rain_expected ? "Có mưa ☔" : "Không có mưa ☀️";
            currentRainStatus.className = data.rain_expected ? "rain-status rain-yes" : "rain-status rain-no";
            
            // ===== THÊM LOG ENTRY =====
            addLogEntry("Cập nhật lúc - Độ ẩm: " + data.moisture + "%");
        })
        .catch(error => {
            // ===== XỬ LÝ LỖI KHI GỌI API =====
            console.error("Error updating status:", error);
            addLogEntry("Lỗi cập nhật trạng thái");
        });
}

// ===== HÀM THÊM LOG VÀO LỊCH SỬ =====
// Thêm một dòng log mới vào đầu danh sách
function addLogEntry(message) {
    const logContainer = document.getElementById("systemLog");
    const entry        = document.createElement("div");  // Tạo thẻ div mới
    entry.className    = "log-entry";
    entry.textContent  = new Date().toLocaleTimeString() + ": " + message;  // Thêm timestamp
    logContainer.insertBefore(entry, logContainer.firstChild);  // Thêm vào đầu danh sách
    
    // ===== GIỚI HẠN SỐ LƯỢNG LOG (CHỈ GIỮ 10 DÒNG GẦN NHẤT) =====
    while (logContainer.children.length > 10) {
        logContainer.removeChild(logContainer.lastChild);  // Xóa dòng cũ nhất
    }
}

// ===== SỰ KIỆN: NÚT TƯỚI NƯỚC THỦ CÔNG =====
document.getElementById("manualWaterBtn").addEventListener("click", function() {
    // Hiển thị hộp thoại xác nhận
    if (confirm("Bắt đầu tưới nước thủ công trong 10 giây?")) {
        this.disabled = true;  // Vô hiệu hóa nút (tránh nhấn nhiều lần)
        this.textContent = "Đang tưới...";  // Thay đổi text
        
        // Gọi API tưới nước
        fetch("/api/water", {method: "POST"})
            .then(response => response.json())
            .then(data => {
                addLogEntry("Tưới nước thủ công thành công");
                this.disabled = false;  // Kích hoạt lại nút
                this.textContent = "💧 Tưới thủ công";
                updateStatus();  // Cập nhật trạng thái ngay
            })
            .catch(error => {
                console.error("Error:", error);
                addLogEntry("Tưới nước thủ công thất bại");
                this.disabled = false;
                this.textContent = "💧 Tưới thủ công";
            });
    }
});

// ===== SỰ KIỆN: NÚT CHUYỂN ĐỔI CHẾ ĐỘ =====
document.getElementById("toggleModeBtn").addEventListener("click", function() {
    // Gọi API chuyển chế độ
    fetch("/api/mode", {method: "POST"})
        .then(response => response.json())
        .then(data => {
            addLogEntry("Chuyển sang chế độ: " + data.mode);
            updateStatus();  // Cập nhật trạng thái
        })
        .catch(error => {
            console.error("Error:", error);
            addLogEntry("Chuyển chế độ thất bại");
        });
});

// ===== SỰ KIỆN: NÚT LƯU CÀI ĐẶT =====
document.getElementById("saveSettingsBtn").addEventListener("click", function() {
    const threshold = document.getElementById("thresholdSlider").value;  // Lấy giá trị ngưỡng
    
    // Gọi API lưu cài đặt
    fetch("/api/settings", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "threshold=" + threshold  // Gửi dữ liệu dạng form
    })
    .then(response => response.json())
    .then(data => {
        addLogEntry("Cài đặt đã lưu thành công");
        updateStatus();
    })
    .catch(error => {
        console.error("Error:", error);
        addLogEntry("Lỗi khi lưu cài đặt");
    });
});

// ===== SỰ KIỆN: SLIDER THAY ĐỔI GIÁ TRỊ =====
// Cập nhật hiển thị giá trị khi kéo slider
document.getElementById("thresholdSlider").addEventListener("input", function() {
    document.getElementById("thresholdValue").textContent = this.value + "%";
});

// ===== KHỞI TẠO KHI TRANG TẢI XONG =====
document.addEventListener("DOMContentLoaded", function() {
    updateStatus();  // Cập nhật trạng thái ngay lập tức
    updateInterval = setInterval(updateStatus, 5000);  // Cập nhật mỗi 5 giây
});
    )";
        configServer->send(200, "application/javascript", js);  // Gửi với Content-Type: application/javascript
    }

    // ===== HANDLER: TRANG CẤU HÌNH WIFI =====
    // Trang này hiển thị khi hệ thống ở chế độ Access Point (chưa có WiFi)
    void handleConfigPage()
    {
        // Tạo HTML form để nhập thông tin WiFi
        String html = R"(
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thiết lập hệ thống tưới nước</title>
    <style>
        body { font-family: Arial; margin: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .container { max-width: 450px; margin: 50px auto;  background: white; padding: 40px; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }
        h1         { color: #2e8b57; text-align: center; margin-bottom: 30px; }
        .form-group { margin-bottom: 20px; }
        label       { display: block; margin-bottom: 8px; font-weight: bold; color: #333; }
        input       { width: 100%; padding: 15px; border: 2px solid #ddd; border-radius: 8px; box-sizing: border-box; font-size: 16px; }
        input:focus { border-color: #4CAF50; outline: none; }
        button       { width: 100%; padding: 18px;  background: linear-gradient(45deg, #4CAF50, #45a049); color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 18px; font-weight: bold; }
        button:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(76, 175, 80, 0.4); }
        .icon     { font-size: 60px;    text-align: center; margin-bottom: 20px; }
        .subtitle { text-align: center; color: #666;      margin-bottom: 30px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="icon">🌱💧</div>
        <h1>Hệ thống tưới nước tự động</h1>
        <p class="subtitle">Thiết lập hệ thống tưới nước thông minh</p>
        <form action="/save" method="POST">
            <div class="form-group">
                <label>🌐 Tên WiFi:</label>
                <input type="text" name="ssid" placeholder="Nhập tên WiFi của bạn" required>
            </div>
            <div class="form-group">
                <label>🔐 Mật khẩu WiFi:</label>
                <input type="password" name="password" placeholder="Nhập mật khẩu WiFi">
            </div>
            <button type="submit">💾 Lưu Cấu Hình & Khởi Động</button>
        </form>
    </div>
</body>
</html>
        )";
        configServer->send(200, "text/html", html);  // Gửi HTML về client
    }
    
    // ===== HANDLER: XỬ LÝ LƯU CẤU HÌNH WIFI =====
    // Hàm này được gọi khi user submit form cấu hình WiFi
    void handleSaveConfig()
    {
        // ===== LẤY DỮ LIỆU TỪ FORM =====
        String ssid = configServer->arg("ssid");          // Lấy tên WiFi
        String password = configServer->arg("password");  // Lấy mật khẩu

        // ===== VALIDATE DỮ LIỆU =====
        if (ssid.length() == 0)
        {
            Serial.println("Error: WiFi name is required!");
            configServer->send(400, "text/html", "<h1>Error: WiFi name is required!</h1>");
            return;  // Dừng hàm nếu không có SSID
        }
        
        // ===== LƯU THÔNG TIN WIFI VÀO FLASH =====
        wifiManager->save(ssid, password);

        Serial.println("Configuration saved successfully, restarting...");
        
        // ===== HIỂN THỊ TRANG THÀNH CÔNG =====
        String successPage = R"(
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial; text-align: center; padding: 50px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }
        .success-box { background: white; padding: 40px; border-radius: 15px; max-width: 500px; margin: 0 auto; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }
        h1 { color: #4CAF50; margin-bottom: 20px; }
        .countdown { font-size: 24px; color: #ff6b6b; font-weight: bold; }
    </style>
</head>
<body>
    <div class="success-box">
        <h1>✅ Cấu hình thành công!</h1>
        <p>🌱 Hệ thống tưới nước sẽ khởi động lại và kết nối WiFi.</p>
        <p>📱 Truy cập lại IP của thiết bị để sử dụng dashboard.</p>
        <div class="countdown" id="countdown">Khởi động lại sau 5 giây...</div>
    </div>
    <script>
        // JavaScript đếm ngược 5 giây
        let seconds = 5;
        const interval = setInterval(() => {
            seconds--;
            document.getElementById('countdown').textContent = `Khởi động lại sau ${seconds} giây...`;
            if (seconds <= 0) {
                document.getElementById('countdown').textContent = "Đang khởi động lại...";
                clearInterval(interval);
            }
        }, 1000);
    </script>
</body>
</html>
        )";
        configServer->send(200, "text/html", successPage);  // Gửi trang thành công
        delay(5000);      // Chờ 5 giây để user đọc thông báo
        ESP.restart();    // Khởi động lại ESP32 để áp dụng cấu hình WiFi mới
    }
    
    // ===== HÀM HỦY - GIẢI PHÓNG BỘ NHỚ =====
    // Destructor được gọi khi đối tượng bị hủy
    ~WateringSystem()
    {
        // Giải phóng tất cả các đối tượng đã cấp phát động
        delete wifiManager;     // Giải phóng WifiManager
        delete moistureSensor;  // Giải phóng MoistureSensor
        delete waterPump;       // Giải phóng WaterPump
        delete weatherAPI;      // Giải phóng WeatherAPI
        delete configServer;    // Giải phóng WebServer
        systemPrefs.end();      // Đóng kết nối với Preferences

        Serial.println("WateringSystem resources cleaned up");
    }
};