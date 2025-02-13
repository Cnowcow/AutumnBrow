package autumn.browmanagement.controller;

import autumn.browmanagement.Entity.User;
import autumn.browmanagement.service.LikeyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LikeyController {

    private final LikeyService likeyService;


    // 공지사항 좋아요
    @PostMapping("/notice/like/{noticeId}")
    public ResponseEntity<?> likeNotice(@PathVariable Long noticeId, HttpSession session) {

        User user = (User) session.getAttribute("user"); // 세션에서 User 객체 가져오기

        if (user == null) {
            return ResponseEntity.badRequest().body("로그인 후 이용 가능합니다."); // 세션이 없을 때 에러 메시지
        }

        Long userId = user.getUserId();

        likeyService.likeNotice(noticeId, userId);

        return ResponseEntity.ok("좋아요");
    }

    
    // 이벤트 좋아요
    @PostMapping("/event/like/{eventId}")
    public ResponseEntity<?> likeEvent(@PathVariable Long eventId, HttpSession session) {

        User user = (User) session.getAttribute("user"); // 세션에서 User 객체 가져오기

        if (user == null) {
            return ResponseEntity.badRequest().body("로그인 후 이용 가능합니다."); // 세션이 없을 때 에러 메시지
        }

        Long userId = user.getUserId();

        likeyService.likeEvent(eventId, userId);

        return ResponseEntity.ok("좋아요");
    }
}
