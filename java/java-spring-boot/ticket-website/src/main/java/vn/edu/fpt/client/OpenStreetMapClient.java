package vn.edu.fpt.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.model.dto.OpenStreetDTO;

import java.util.List;

@FeignClient(name = "OpenStreetMapClient", url = "https://nominatim.openstreetmap.org")
public interface OpenStreetMapClient {

    @GetMapping(value = "/search", headers = "User-Agent=EventHubApplication/1.0 (kaizpinglaz@gmail.com)")
    public List<OpenStreetDTO> getOpenStreetByName(@RequestParam("format") String format,
                                                   @RequestParam("q") String name);

    @GetMapping(value = "/search", headers = "User-Agent=EventHubApplication/1.0 (kaizpinglaz@gmail.com)")
    public List<OpenStreetDTO> getOpenStreetByLatLon(@RequestParam("format") String format,
                                               @RequestParam("lat") Double lat,
                                               @RequestParam("lon") Double lon);

}
