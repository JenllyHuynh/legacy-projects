package vn.edu.fpt.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.dto.ProvinceDTO;
import vn.edu.fpt.model.dto.WardDTO;

import java.util.List;

@FeignClient(name = "AddressClient", url = "provinces.open-api.vn")
public interface AddressClient {

    @GetMapping(value = "/api/v2/p")
    List<ProvinceDTO> getAllProvinces();

    @GetMapping(value = "/api/v2/p/{code}")
    ProvinceDTO getProvinceByProvinceCode(@PathVariable("code") String code,
                                          @RequestParam("depth") int depth
    );

    @GetMapping(value = "/api/v2/w/{code}")
    WardDTO getWardByWardCode(@PathVariable("code") String code);

}
