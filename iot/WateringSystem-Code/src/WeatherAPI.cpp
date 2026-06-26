#include <Arduino.h>      // Thư viện cơ bản của Arduino
#include <Preferences.h>  // Thư viện lưu trữ dữ liệu vào flash memory
#include <HTTPClient.h>   // Thư viện thực hiện HTTP request
#include <ArduinoJson.h>  // Thư viện xử lý dữ liệu JSON

class WeatherAPI {
private:
    String apiKey;      // Khóa API để truy cập OpenWeatherMap
    String city;        // Tên thành phố cần lấy thông tin thời tiết
    HTTPClient http;    // Đối tượng để thực hiện HTTP request

public:
    // Hàm khởi tạo với giá trị mặc định
    WeatherAPI(String key = "", String location = "Can Tho") {
        apiKey = key;           // Gán API key
        city = location;        // Gán tên thành phố, mặc định là "Can Tho"
    }
    
    // Hàm thiết lập API key mới
    void setApiKey(String key) { 
        apiKey = key;  // Cập nhật API key
        Serial.println("Weather API key set");  // Thông báo đã cập nhật
    }
    
    // Hàm thiết lập thành phố mới
    void setCity(String location) { 
        city = location;  // Cập nhật tên thành phố
        Serial.println("Weather city set to: " + location);  // Thông báo đã cập nhật
    }

    // Hàm lấy thông tin thời tiết hiện tại
    // Trả về true nếu thành công, false nếu thất bại
    // Các tham số truyền vào là tham chiếu (&) để nhận giá trị trả về
    bool getCurrentWeather(String &condition, String &description, float &temperature, int &humidity, int &clouds) {
        // Kiểm tra API key đã được thiết lập chưa
        if (apiKey.length() == 0) {
            Serial.println("API key not set");  // Thông báo lỗi
            return false;
        }

        // URL để gọi API
        String encodedCity = city;         // Tên thành phố
        encodedCity.replace(" ", "%20");  // Thay khoảng trắng bằng %20
        
        // Tạo URL hoàn chỉnh với các tham số
        // q=tên_thành_phố, units=metric (đơn vị độ C), APPID=API_key
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&units=metric&APPID=" + apiKey;
        
        Serial.println("Fetching weather from: " + url);  // In URL để debug
        
        http.begin(url);         // Khởi tạo kết nối HTTP với URL
        http.setTimeout(15000);  // Đặt thời gian timeout là 15 giây
        
        int httpCode = http.GET();  // Thực hiện GET request và lấy mã trạng thái HTTP
        Serial.println("Weather API HTTP code: " + String(httpCode));  // In mã trạng thái

        // Kiểm tra mã trạng thái HTTP 200
        if (httpCode == 200) {
            String payload = http.getString();  // Lấy nội dung phản hồi từ API
            Serial.println("Weather API response received");  // Thông báo đã nhận được phản hồi
            Serial.println("Response: " + payload.substring(0, 200) + "...");  // In 200 ký tự đầu để debug
            
            DynamicJsonDocument doc(2048);  // Tạo document JSON với kích thước 2048 bytes
            DeserializationError error = deserializeJson(doc, payload);  // Parse JSON từ chuỗi payload
            
            // Kiểm tra lỗi khi parse JSON
            if (error) {
                Serial.println("JSON parse error: " + String(error.c_str()));  // In lỗi
                http.end();  // Đóng kết nối HTTP
                return false;  // Trả về false
            }

            // Kiểm tra mã lỗi từ API (OpenWeatherMap trả về field "cod")
            if (doc.containsKey("cod")) {
                int cod = doc["cod"];  // Lấy mã lỗi (dạng số)
                String message = doc["message"] | "Unknown error";  // Lấy thông báo lỗi
                
                // Mã 404: Không tìm thấy thành phố
                if (cod == 404) {
                    Serial.println("City not found: " + city + " - " + message);
                    http.end();  // Đóng kết nối
                    return false;
                }
                // Mã 401: API key không hợp lệ
                if (cod == 401) {
                    Serial.println("Invalid API key (401): " + message);
                    http.end();  // Đóng kết nối
                    return false;
                }
                // Mã khác 200: Có lỗi xảy ra
                if (cod != 200) {
                    Serial.println("API error " + String(cod) + ": " + message);
                    http.end();  // Đóng kết nối
                    return false;
                }
            }

            // Lấy dữ liệu thời tiết từ JSON
            // Kiểm tra xem có field "weather" và có ít nhất 1 phần tử không
            if (doc.containsKey("weather") && doc["weather"].size() > 0) {
                condition   = doc["weather"][0]["main"].as<String>();         // Tình trạng thời tiết (Rain, Clear, ...)
                description = doc["weather"][0]["description"].as<String>();  // Mô tả chi tiết
                temperature = doc["main"]["temp"];                            // Nhiệt độ (°C)
                humidity    = doc["main"]["humidity"];                        // Độ ẩm (%)
                clouds      = doc["clouds"]["all"];                           // Độ che phủ của mây (%)

                // In thông tin thời tiết ra Serial
                Serial.println("Weather data - " + condition + ", " + String(temperature, 1) + "°C, " + String(humidity) + "% humidity");
                http.end();  // Đóng kết nối HTTP
                return true;  // Trả về true (thành công)
            } else {
                Serial.println("No weather data in response");  // Không có dữ liệu thời tiết
                http.end();  // Đóng kết nối
                return false;
            }
            
        } else {
            // HTTP request thất bại
            Serial.println("Weather API HTTP error: " + String(httpCode));  // In mã lỗi
            if (httpCode > 0) {
                String response = http.getString();  // Lấy nội dung phản hồi
                Serial.println("Error response: " + response);  // In nội dung lỗi
            }
            http.end();  // Đóng kết nối
            return false;  // Trả về false
        }
    }

    // Hàm kiểm tra xem hiện tại có mưa hay không
    bool Rain() {
        String condition, description;  // Biến lưu tình trạng và mô tả
        float temperature;              // Biến lưu nhiệt độ
        int humidity, clouds;           // Biến lưu độ ẩm và độ che phủ mây
        
        // Gọi hàm lấy thông tin thời tiết
        if (getCurrentWeather(condition, description, temperature, humidity, clouds)) {
            // Kiểm tra condition có phải là Rain, Drizzle hoặc Thunderstorm không
            bool isRaining = (condition == "Rain" || condition == "Drizzle" || condition == "Thunderstorm");
            Serial.println("Rain check: " + String(isRaining ? "YES" : "NO"));  // In kết quả
            return isRaining;  // Trả về true nếu có mưa
        }
        Serial.println("Rain check: FAILED - No weather data");  // Thông báo lỗi
        return false;  // Trả về false nếu không lấy được dữ liệu
    }

    // Hàm lấy thông tin thời tiết đầy đủ dạng chuỗi
    String getFullWeatherInfo() {
        String condition, description;  // Biến lưu tình trạng và mô tả
        float temperature;              // Biến lưu nhiệt độ
        int humidity, clouds;           // Biến lưu độ ẩm và độ che phủ mây

        // Gọi hàm lấy thông tin thời tiết
        if (getCurrentWeather(condition, description, temperature, humidity, clouds)) {
            String info = "";  // Khởi tạo chuỗi kết quả
            // Ghép các thông tin thời tiết vào chuỗi
            info += "Nhiệt độ: " + String(temperature, 1) + "°C\n";  // Nhiệt độ (1 số thập phân)
            info += "Điều kiện: " + description + "\n";              // Mô tả thời tiết
            info += "Độ ẩm: " + String(humidity) + "%\n";            // Độ ẩm
            info += "Mây: " + String(clouds) + "%\n";                // Độ che phủ mây
            // Kiểm tra có mưa hay không
            info += "Hiện tại: " + String((condition == "Rain" || condition == "Drizzle" || condition == "Thunderstorm") ? "Có mưa ☔" : "Không mưa ☀️");
            return info;  // Trả về chuỗi thông tin
        } else {
            // Không lấy được dữ liệu thời tiết
            return "Không thể lấy dữ liệu thời tiết cho: " + city;
        }
    }

    // Hàm hủy
    ~WeatherAPI() {}
};