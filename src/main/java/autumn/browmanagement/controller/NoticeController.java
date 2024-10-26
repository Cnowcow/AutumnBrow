package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.NoticeDTO;
import autumn.browmanagement.Entity.Notice;
import autumn.browmanagement.service.ImageService;
import autumn.browmanagement.service.LikeyService;
import autumn.browmanagement.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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


    // 공지사항 관리페이지
    @GetMapping("/notice/update")
    public String noticeUpdate(Model model){

        List<NoticeDTO> noticeDTO = noticeService.noticeListImportant();
        model.addAttribute("notices", noticeDTO);

        return "notice/noticeUpdate";
    }


    // 공지사항 자세히 보기
    @GetMapping("/notice/{noticeId}/detail")
    public String noticeDetail(@PathVariable Long noticeId, Model model){

        noticeService.increaseNoticeHits(noticeId);

        NoticeDTO noticeDTO = noticeService.noticeDetail(noticeId);
        model.addAttribute("notices", noticeDTO);

        return "notice/noticeDetail";
    }


    // 공지사항 수정 폼
    @GetMapping("/notice/{noticeId}/update")
    public String noticeUpdateForm(@PathVariable Long noticeId, Model model){

        NoticeDTO noticeDTO = noticeService.noticeUpdateForm(noticeId);
        model.addAttribute("notices", noticeDTO);

        return "notice/noticeUpdateForm";
    }


    // 공지사항 수정 요청
    @PostMapping("/notice/update")
    public String noticeUpdate(@ModelAttribute NoticeDTO noticeDTO, @RequestParam("noticeImages") MultipartFile[] noticeImages) {

        noticeService.noticeUpdate(noticeDTO, noticeImages);
        Long noticeId = noticeDTO.getNoticeId();

        return "redirect:/notice/"+ noticeId + "/detail";
    }


    // 공지사항 사진 삭제요청
    @PostMapping("/notice/deleteImage/{imageUrl}")
    @ResponseBody
    public void noticeImageDelete(@PathVariable String imageUrl){
        imageService.noticeImageDelete(imageUrl);
    }


}
