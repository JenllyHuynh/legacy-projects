package vn.edu.fpt.model.dto;

/**
 * ProfileDTO — Trả về thông tin profile cho client.
 * Không chứa các field nhạy cảm (passwordHash, isActive...).
 * Email readonly vì đã được xác minh khi đăng ký.
 */
public class ProfileDTO {

    private Integer customerId;
    private String  fullName;
    private String  email;       // readonly — đã verify, không cho sửa
    private String  phone;
    private String  avatarUrl;
    private String  createdAt;   // format: "dd/MM/yyyy"

    // ── Constructors ──────────────────────────────────────────────
    public ProfileDTO() {}

    public ProfileDTO(Integer customerId, String fullName, String email,
                      String phone, String avatarUrl, String createdAt) {
        this.customerId = customerId;
        this.fullName   = fullName;
        this.email      = email;
        this.phone      = phone;
        this.avatarUrl  = avatarUrl;
        this.createdAt  = createdAt;
    }

    // ── Getters & Setters ─────────────────────────────────────────
    public Integer getCustomerId()          { return customerId; }
    public void setCustomerId(Integer v)    { this.customerId = v; }

    public String getFullName()             { return fullName; }
    public void setFullName(String v)       { this.fullName = v; }

    public String getEmail()                { return email; }
    public void setEmail(String v)          { this.email = v; }

    public String getPhone()                { return phone; }
    public void setPhone(String v)          { this.phone = v; }

    public String getAvatarUrl()            { return avatarUrl; }
    public void setAvatarUrl(String v)      { this.avatarUrl = v; }

    public String getCreatedAt()            { return createdAt; }
    public void setCreatedAt(String v)      { this.createdAt = v; }
}