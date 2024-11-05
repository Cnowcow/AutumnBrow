package autumn.browmanagement.controller;

import autumn.browmanagement.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class VisitPathController {

    private final VisitService visitService;

    // 세부내용 불러오기
    @GetMapping("/find/visitPath/{selectedOptionId}")
    @ResponseBody
    public String visitList(@PathVariable Long selectedOptionId) {
        return visitService.visitList(selectedOptionId);
    }

}
