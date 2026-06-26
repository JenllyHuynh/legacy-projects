package vn.edu.fpt.service.VietMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class VietMapService {

    @Value("api.vietmap.key")
    private String API_VIETMAP_KEY;

    private final RestClient restClient;

    public VietMapService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Object getMapStyle() {
        // 1. Lấy JSON gốc từ VietMap
        JsonNode root = restClient.get()
                .uri("/maps/styles/tm/style.json?apikey=" + API_VIETMAP_KEY)
                .retrieve()
                .body(JsonNode.class);

        // 2. Ép kiểu sang ObjectNode để có thể sửa đổi dữ liệu
        if (root instanceof ObjectNode objectNode) {
            ObjectNode sources = (ObjectNode) objectNode.get("sources");
            ObjectNode openmaptiles = (ObjectNode) sources.get("openmaptiles");
            ArrayNode tiles = (ArrayNode) openmaptiles.get("tiles");

            // Xóa link VietMap cũ và thay bằng link Proxy của bạn
            tiles.removeAll();
            // Giả sử API proxy tiles của bạn là /api/vietmap/tiles/...
            tiles.add("http://localhost:8080/api/vietmap/tiles/{z}/{x}/{y}.pbf");

            // Làm tương tự cho sprite và glyphs nếu cần giấu sạch dấu vết
        }
        return root;
    }

    public ResponseEntity<byte[]> proxyTiles(int z, int x,int y) {
        return restClient.get()
                .uri("/maps/tiles/vlc-20260325/" + z + "/" + x + "/" + y + ".pbf?apikey=" + API_VIETMAP_KEY)
                .retrieve()
                .toEntity(byte[].class); // Trả về mảng byte dữ liệu bản đồ
    }


    public ResponseEntity<String> forwardGetRequest(Map<String, String> params) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/geocode/v4")
                        .queryParam("apikey", API_VIETMAP_KEY)
                        .queryParam("text", params)
                        .build())
                .retrieve()
                .toEntity(String.class);
    }
}
