package autumn.browmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class NoticeController {
    
    // 시술 전 주의사항
    @GetMapping("/notice/before")
    public String beforeNotice(){
        return "/notice/beforeNotice";
    }

    // 시술 후 주의사항
    @GetMapping("/notice/after")
    public String afterNotice(){
        return "/notice/afterNotice";
    }
    
}
