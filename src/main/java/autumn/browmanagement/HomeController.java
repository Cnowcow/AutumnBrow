package autumn.browmanagement;

import autumn.browmanagement.DTO.EventDTO;
import autumn.browmanagement.DTO.NoticeDTO;
import autumn.browmanagement.DTO.ReviewDTO;
import autumn.browmanagement.service.EventService;
import autumn.browmanagement.service.NoticeService;
import autumn.browmanagement.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;
    private final EventService eventService;
    private final ReviewService reviewService;

    @GetMapping("/")
    public String Home(Model model) {

        List<NoticeDTO> noticeDTOS = noticeService.noticeListIndex();
        model.addAttribute("notices", noticeDTOS);

        List<EventDTO> eventDTOS = eventService.eventListIndex();
        model.addAttribute("events", eventDTOS);

        List<ReviewDTO> reviewDTOS = reviewService.reviewList();
        model.addAttribute("reviews", reviewDTOS);

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
