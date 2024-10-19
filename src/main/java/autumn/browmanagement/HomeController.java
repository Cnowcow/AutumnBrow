package autumn.browmanagement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

    @GetMapping("/")
    public String Home(Model model) {

        model.addAttribute("kakaoMapKey", kakaoMapKey);
        return "index";
    }

    @GetMapping("/map-data")
    public ResponseEntity<String> getMapData() {
        String apiUrl = "https://dapi.kakao.com/v2/maps/sdk.js?appkey=" + kakaoMapKey + "&libraries=services";

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        return ResponseEntity.ok(response);
    }


    @Value("${kakao.map.key}")
    private String kakaoMapKey;

}
