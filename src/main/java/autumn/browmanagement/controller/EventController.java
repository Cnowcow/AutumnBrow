package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.EventDTO;
import autumn.browmanagement.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;
    private final ImageService imageService;


    // 이벤트 등록 폼
    @GetMapping("/event/create")
    public String eventCreateForm(){

        return "event/eventCreateForm";
    }


    // 이벤트 등록 요청
    @PostMapping("/event/create")
    public String eventCreate(@ModelAttribute EventDTO eventDTO, @RequestParam("eventImages") MultipartFile[] eventImages){
        eventService.eventCreate(eventDTO, eventImages);
        Long eventId = eventDTO.getEventId();

        return "redirect:/event/" + eventId + "/detail";
    }


    // 이벤트 페이지 중요도순
    @GetMapping("/event/list")
    public String eventList(Model model){

        List<EventDTO> eventDTOS = eventService.eventListImportant();
        model.addAttribute("events", eventDTOS);

        return "event/eventList";
    }


    // 이벤트 관리페이지
    @GetMapping("/event/update")
    public String eventUpdate(Model model){

        List<EventDTO> eventDTO = eventService.eventListImportant();
        model.addAttribute("events", eventDTO);

        return "event/eventUpdate";
    }


    // 이벤트 자세히 보기
    @GetMapping("/event/{eventId}/detail")
    public String eventDetail(@PathVariable Long eventId, Model model){

        eventService.increaseEventHits(eventId);

        EventDTO eventDTO = eventService.eventDetail(eventId);
        model.addAttribute("events", eventDTO);

        return "event/eventDetail";
    }


    // 이벤트 수정 폼
    @GetMapping("/event/{eventId}/update")
    public String eventUpdateForm(@PathVariable Long eventId, Model model){

        EventDTO eventDTO = eventService.eventUpdateForm(eventId);
        model.addAttribute("events", eventDTO);

        return "event/eventUpdateForm";
    }


    // 이벤트 수정 요청
    @PostMapping("/event/{eventId}/update")
    public String eventUpdate(@PathVariable Long eventId, @ModelAttribute EventDTO eventDTO, @RequestParam("eventImages") MultipartFile[] eventImages) {

        eventService.eventUpdate(eventDTO, eventImages);

        return "redirect:/event/"+ eventId + "/detail";
    }


    // 이벤트 사진 삭제요청
    @PostMapping("/event/deleteImage/{imageUrl}")
    @ResponseBody
    public void eventImageDelete(@PathVariable String imageUrl){
        imageService.eventImageDelete(imageUrl);
    }


    // 이벤트 삭제 요청
    @DeleteMapping("/event/{eventId}/delete")
    public ResponseEntity<Map<String, String>> eventDelete(@PathVariable Long eventId) {

        eventService.eventDelete(eventId);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/event/update");
        return ResponseEntity.ok(response);
    }
}
