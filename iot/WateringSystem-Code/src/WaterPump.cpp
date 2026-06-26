#include <Arduino.h>        // Thư viện cơ bản của Arduino
#include <Preferences.h>    // Thư viện lưu trữ dữ liệu vào flash memory

class WaterPump{
private:
    int relayPin;    // Chân điều khiển relay
    bool run;        // Trạng thái máy bơm (true: đang chạy, false: đang tắt)

public:
    // Hàm khởi tạo máy bơm
    WaterPump(int pin){
        relayPin = pin;     // Gán chân điều khiển
        run      = false;   // Mặc định máy bơm ở trạng thái tắt
        pinMode(relayPin, OUTPUT);  // Thiết lập chân relay là OUTPUT
        digitalWrite(relayPin, LOW); // Tắt relay ban đầu (ACTIVE HIGH)
    }
    // Hàm bật máy bơm
    void turnOn(){
        if (!run){  // Chỉ bật nếu máy bơm đang tắt
            digitalWrite(relayPin, HIGH); // Bật relay (ACTIVE HIGH)
            run = true;                   // Cập nhật trạng thái
            Serial.println("On");         // In trạng thái ra Serial
        }
    }

    // Hàm tắt máy bơm
    void turnOff(){
        if (run){   // Chỉ tắt nếu máy bơm đang chạy
            digitalWrite(relayPin, LOW);  // Tắt relay (ACTIVE HIGH)
            run = false;                  // Cập nhật trạng thái
            Serial.println("Off");        // In trạng thái ra Serial
        }
    }

    // Hàm lấy trạng thái máy bơm
    bool getRun() { return run; }

    // Hàm hủy
    ~WaterPump() {}
};