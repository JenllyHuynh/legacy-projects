package vn.edu.fpt.service.address;

import org.springframework.stereotype.Service;
import vn.edu.fpt.client.OpenStreetMapClient;
import vn.edu.fpt.model.dto.OpenStreetDTO;

import java.util.List;

@Service
public class OpenStreetService {

    private final OpenStreetMapClient openStreetMapClient;

    public OpenStreetService (OpenStreetMapClient openStreetMapClient) {
        this.openStreetMapClient = openStreetMapClient;
    }

    public List<OpenStreetDTO> getOpenStreetByName(String name) {
        return openStreetMapClient.getOpenStreetByName("json",name);
    }

    public List<OpenStreetDTO> getOpenStreetByLatLon(double lat, double lon) {
        return openStreetMapClient.getOpenStreetByLatLon("json", lat, lon);
    }

}
