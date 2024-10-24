package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.NoticeDTO;
import autumn.browmanagement.domain.Notice;
import autumn.browmanagement.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;


    // 공지사항
    @GetMapping("/notice/list")
    public String noticeList(Model model){

        List<NoticeDTO> noticeDTOS = noticeService.noticeListImportant();
        model.addAttribute("notices", noticeDTOS);

        return "notice/noticeList";
    }


    // 공지사항 자세히
    @GetMapping("/notice/{noticeId}/detail")
    public String noticeDetail(@PathVariable Long noticeId, Model model){

        noticeService.increaseNoticeHits(noticeId);

        NoticeDTO noticeDTO = noticeService.findById(noticeId);
        model.addAttribute("notices", noticeDTO);

        return "notice/noticeDetail";
    }


    // 공지사항 좋아요
    @PostMapping("/notice/like/{noticeId}")
    public ResponseEntity<String> noticeLike(@PathVariable Long noticeId){
        System.out.println("좋아요 눌렀다 " + noticeId);
        noticeService.increaseNoticeLike(noticeId);
        return ResponseEntity.ok("좋아요");
    }


}
