package vn.edu.fpt.model.dto;

import java.util.Arrays;

public class ProvinceDTO {
    private String name;
    private int code;
    private String division_type;
    private String codename;
    private String phone_code;
    private WardDTO[] wards;

    public ProvinceDTO() {
    }

    public ProvinceDTO(String name, WardDTO[] wards, String phone_code, String codename, String division_type, int code) {
        this.name = name;
        this.wards = wards;
        this.phone_code = phone_code;
        this.codename = codename;
        this.division_type = division_type;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDivision_type() {
        return division_type;
    }

    public void setDivision_type(String division_type) {
        this.division_type = division_type;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public WardDTO[] getWards() {
        return wards;
    }

    public void setWards(WardDTO[] wards) {
        this.wards = wards;
    }

    @Override
    public String toString() {
        return "ProvinceDTO{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", division_type='" + division_type + '\'' +
                ", codename='" + codename + '\'' +
                ", phone_code='" + phone_code + '\'' +
                ", ward=" + Arrays.toString(wards) +
                '}';
    }
}
