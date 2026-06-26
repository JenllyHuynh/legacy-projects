package vn.edu.fpt.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import vn.edu.fpt.service.VietMap.VietMapService;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/vietmap")
@CrossOrigin(origins = "*")
public class VietMapProxyController {



    private final VietMapService vietMapService;

    public VietMapProxyController(VietMapService vietMapService) {
        this.vietMapService = vietMapService;
    }

    @GetMapping("/fetch-data")
    public ResponseEntity<?> fetchData(@RequestParam Map<String, String> allParams) {
        return vietMapService.forwardGetRequest(allParams);
    }


    @GetMapping(value = "/tileMap", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTileMap() {
        Object response = vietMapService.getMapStyle();

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .body(response);
    }

    @GetMapping("/tiles/{z}/{x}/{y}.pbf")
    public ResponseEntity<byte[]> proxyTiles(@PathVariable int z, @PathVariable int x, @PathVariable int y) {
        return vietMapService.proxyTiles(z, x, y);
    }


}
