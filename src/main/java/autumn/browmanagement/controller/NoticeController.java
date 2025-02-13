package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.NoticeDTO;
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
public class NoticeController {

    private final NoticeService noticeService;
    private final ImageService imageService;


    // 공지사항 등록 폼
    @GetMapping("/notice/create")
    public String noticeCreateForm(){

        return "notice/noticeCreateForm";
    }


    // 공지사항 등록 요청
    @PostMapping("/notice/create")
    public String noticeCreate(@ModelAttribute NoticeDTO noticeDTO, @RequestParam("noticeImages") MultipartFile[] noticeImages){
        noticeService.noticeCreate(noticeDTO, noticeImages);
        Long noticeId = noticeDTO.getNoticeId();

        return "redirect:/notice/" + noticeId + "/detail";
    }


    // 공지사항 페이지 중요도순
    @GetMapping("/notice/list")
    public String noticeList(Model model){

        List<NoticeDTO> noticeDTOS = noticeService.noticeListImportant();
        model.addAttribute("notices", noticeDTOS);

        return "notice/noticeList";
    }


    // 공지사항 자세히 보기
    @GetMapping("/notice/{noticeId}/detail")
    public String noticeDetail(@PathVariable Long noticeId, Model model){

        noticeService.increaseNoticeHits(noticeId);

        NoticeDTO noticeDTO = noticeService.noticeDetail(noticeId);
        model.addAttribute("notices", noticeDTO);

        return "notice/noticeDetail";
    }


    // 공지사항 관리페이지
    @GetMapping("/notice/update")
    public String noticeUpdate(Model model){

        List<NoticeDTO> noticeDTO = noticeService.noticeListImportant();
        model.addAttribute("notices", noticeDTO);

        return "notice/noticeUpdate";
    }


    // 공지사항 수정 폼
    @GetMapping("/notice/{noticeId}/update")
    public String noticeUpdateForm(@PathVariable Long noticeId, Model model){

        NoticeDTO noticeDTO = noticeService.noticeUpdateForm(noticeId);
        model.addAttribute("notices", noticeDTO);

        return "notice/noticeUpdateForm";
    }


    // 공지사항 수정 요청
    @PostMapping("/notice/{noticeId}/update")
    public String noticeUpdate(@PathVariable Long noticeId, @ModelAttribute NoticeDTO noticeDTO, @RequestParam("noticeImages") MultipartFile[] noticeImages) {

        noticeService.noticeUpdate(noticeDTO, noticeImages);

        return "redirect:/notice/"+ noticeId + "/detail";
    }


    // 공지사항 사진 삭제요청
    @PostMapping("/notice/deleteImage/{imageUrl}")
    @ResponseBody
    public void noticeImageDelete(@PathVariable String imageUrl){
        imageService.noticeImageDelete(imageUrl);
    }


    // 공지사항 삭제 요청
    @DeleteMapping("/notice/{noticeId}/delete")
    public ResponseEntity<Map<String, String>> noticeDelete(@PathVariable Long noticeId) {

        noticeService.noticeDelete(noticeId);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/notice/update");
        return ResponseEntity.ok(response);
    }
}
