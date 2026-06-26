package vn.edu.fpt.service.address;

import org.springframework.stereotype.Service;
import vn.edu.fpt.client.AddressClient;
import vn.edu.fpt.model.dto.ProvinceDTO;

import java.util.List;

@Service
public class AddressService {

    private final AddressClient addressClient;

    public AddressService (AddressClient addressClient) {
        this.addressClient = addressClient;
    }

    public ProvinceDTO getProvinceByProvinceCode(String code, int depth) {
        return addressClient.getProvinceByProvinceCode(code, depth);
    }

    public List<ProvinceDTO> getAllProvinces() {
        return addressClient.getAllProvinces();
    }
}
