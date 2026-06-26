package vn.edu.fpt.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileUpdateDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 200, message = "Họ tên phải từ 2 đến 200 ký tự")
    @Pattern(
            regexp = "^[\\p{L}\\s\\-'.]+$",
            message = "Họ tên chỉ được chứa chữ cái và dấu cách"
    )
    private String fullName;

    // Phone bắt buộc — frontend đã normalize về dạng 0xxxxxxxxx trước khi gửi
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^0\\d{9}$",
            message = "Số điện thoại không hợp lệ (phải đủ 10 số, bắt đầu bằng 0)"
    )
    private String phone;

    public String getFullName()       { return fullName; }
    public void setFullName(String v) { this.fullName = v != null ? v.trim() : null; }

    public String getPhone()          { return phone; }
    public void setPhone(String v)    { this.phone = v != null ? v.trim() : null; }
}