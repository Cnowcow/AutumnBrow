package autumn.browmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class NoticeController {


    // 시술 전 주의사항
    @GetMapping("/notice/before")
    public String beforeNotice(Model model){

        return "/notice/beforeNotice";
    }

    // 시술 후 주의사항
    @GetMapping("/notice/after")
    public String afterNotice(Model model){

        return "/notice/afterNotice";
    }
    
}
