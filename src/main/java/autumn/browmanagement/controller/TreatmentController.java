package autumn.browmanagement.controller;

import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;


    // 세부내용 불러오기
    @GetMapping("/find/child/{parentId}")
    @ResponseBody
    public List<TreatmentForm> getChildTreatments(@PathVariable Long parentId) {
        List<TreatmentForm> childTreatments = treatmentService.findChildTreatments(parentId);
        return childTreatments;
    }


    // 직접입력된 시술내용 추가
    @PostMapping("/treatment/creat")
    public String creatTreatment(String name, Model model){

        return "redirect:/treatment/list";
    }

    // 시술내용 추가 페이지
    @GetMapping("/treatment/add")
    public String treatmentAdd(){

        return "/treatment/treatmentAdd";
    }


}
