package vn.edu.fpt.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OrganizerDTO {
    @NotBlank(message = "Organizer name is required")
    @Size(max = 100)
    private String name;

    private String logoUrl;

    @Size(max = 500)
    private String description;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9+\\-() ]{8,15}$",
            message = "Phone number format is invalid"
    )
    private String phone;

    private String website;

    private String status;

    public OrganizerDTO(String name, String logoUrl, String description, String email, String phone, String website, String status) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
