package vn.edu.fpt.model.dto;

public class CheckoutFormDTO {
    private String paymentMethod;
    private String email;
    private String country;
    private String city;
    private String ward;
    private String fullAddress;
    private boolean isAgreePolicy;

    public CheckoutFormDTO() {
    }

    public CheckoutFormDTO(String paymentMethod, String email, String country, String city, String ward, String fullAddress,
                           boolean isAgreePolicy) {
        this.paymentMethod = paymentMethod;
        this.email = email;
        this.country = country;
        this.city = city;
        this.ward = ward;
        this.fullAddress = fullAddress;
        this.isAgreePolicy = isAgreePolicy;
    }

    public boolean isAgreePolicy() {
        return isAgreePolicy;
    }

    public void setAgreePolicy(boolean agreePolicy) {
        isAgreePolicy = agreePolicy;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}

