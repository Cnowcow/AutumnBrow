package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.NoticeDTO;
import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.DTO.TreatmentDTO;
import autumn.browmanagement.domain.Notice;
import autumn.browmanagement.domain.Post;
import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.domain.Visit;
import autumn.browmanagement.service.LikeyService;
import autumn.browmanagement.service.NoticeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final LikeyService likeyService;


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


    // 공지사항 관리
    @GetMapping("/notice/update")
    public String noticeUpdate(Model model){

        List<NoticeDTO> noticeDTO = noticeService.noticeListImportant();
        model.addAttribute("notices", noticeDTO);

        return "notice/noticeUpdate";
    }


    // 공지사항 수정 폼
    @GetMapping("/notice/{noticeId}/update")
    public String noticeUpdateForm(@PathVariable Long noticeId, Model model){

        Notice notice = noticeService.noticeFindById(noticeId);
        model.addAttribute("notices", notice);

        return "notice/noticeUpdateForm";
    }


    // 공지사항 수정 요청
    @PostMapping("/notice/{noticeId}/update")
    public String noticeUpdate(@PathVariable Long noticeId, @ModelAttribute("notices") NoticeDTO noticeDTO, Model model ) {

        try {
            // 서비스에 파일 처리 및 업로드 요청
            noticeService.handleFileUpload(noticeDTO);
            noticeService.noticeUpdate(noticeId, noticeDTO);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            return "error";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/notice/update";
    }

}
