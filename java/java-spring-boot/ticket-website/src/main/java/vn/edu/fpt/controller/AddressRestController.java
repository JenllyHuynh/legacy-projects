package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.client.AddressClient;
import vn.edu.fpt.model.dto.OpenStreetDTO;
import vn.edu.fpt.model.dto.ProvinceDTO;
import vn.edu.fpt.model.dto.WardDTO;
import vn.edu.fpt.service.address.AddressService;
import vn.edu.fpt.service.address.OpenStreetService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/address")
public class AddressRestController {

    @Value("${api.vietmap.key}")
    private String vietMapKey;

    private final AddressService addressService;
    private final OpenStreetService openStreetService;

    public AddressRestController(AddressService addressService,
                                 OpenStreetService openStreetService) {
        this.addressService = addressService;
        this.openStreetService = openStreetService;
    }

    @GetMapping("/p")
    public ResponseEntity<?> getProvinces() {

        List<ProvinceDTO> respone = addressService.getAllProvinces();

        return ResponseEntity.ok(respone);
    }

    @GetMapping("/p/{code}")
    public ResponseEntity<?> getWardsByProvinceCode(@PathVariable("code") String code) {

        ProvinceDTO province = addressService.getProvinceByProvinceCode(code, 2);
        System.out.println(province);
        List<WardDTO> respone = Arrays.stream(province.getWards()).toList();

        for(WardDTO e: respone) {
            System.out.println(e.getName());
        }

        return ResponseEntity.ok(respone);
    }

    @GetMapping("/p/findCode/{name}")
    public ResponseEntity<?> getCodeOfProvince(@PathVariable("name") String name) {
        List<ProvinceDTO> provinces = addressService.getAllProvinces();
        for(ProvinceDTO e: provinces) {
            if(name.replace("_"," ").equals(e.getName())) {
                return ResponseEntity.ok(e);
            }
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/w/findCode/{provinceCode}/{name}")
    public ResponseEntity<?> getCodeOfWard(@PathVariable("name") String name,
                                               @PathVariable("provinceCode") String provinceCode) {
        ProvinceDTO provinces = addressService.getProvinceByProvinceCode(provinceCode, 2);
        List<WardDTO> wards = Arrays.stream(provinces.getWards()).toList();
        for(WardDTO e: wards) {
            if(name.replace("_"," ").equals(e.getName())) {
                return ResponseEntity.ok(e);
            }
        }
        return ResponseEntity.ok(null);
    }


    @GetMapping(value = "/openStreet/{name}")
    public ResponseEntity<?> getOpenStreetByName(@PathVariable("name") String name) {
        System.err.println("Đã gọi openStreet tìm kiếm bằng tên");
        List<OpenStreetDTO> openStreets = openStreetService.getOpenStreetByName(name);
        for (int i = 0; i < openStreets.size(); i++) {
            StringBuilder street = new StringBuilder();
            String[] address = openStreets.get(i).getDisplay_name().split(",");
            System.out.println(openStreets.get(i).getDisplay_name());
            for (int j = 1; j < address.length; j++) {
                if (address[j].contains("Phường")) {
                    break;
                }
                System.err.println("Street" + address[j]);
                street.append(address[j].trim()).append(", ");
            }
            openStreets.get(i).setStreet(street.toString().trim().substring(0,street.length() - 2));
        }
        return ResponseEntity.ok(openStreets);
    }
}
