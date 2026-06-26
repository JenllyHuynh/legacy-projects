package vn.edu.fpt.model.dto;

public class OpenStreetDTO {
    private String name;
    private String display_name;
    private Double lat;
    private Double lon;
    private String licence;
    private String Street;

    public OpenStreetDTO() {
    }

    public OpenStreetDTO(Double lon, Double lat, String name, String display_name, String licence, String Street) {
        this.lon = lon;
        this.lat = lat;
        this.name = name;
        this.display_name = display_name;
        this.licence = licence;
        this.Street = Street;
    }



    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }
}
