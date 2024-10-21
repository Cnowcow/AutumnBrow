package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.TreatmentDTO;
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


    // 시술내용 불러오기
    @GetMapping("/find/parent/{parentId}")
    @ResponseBody
    public String getParentTreatments(@PathVariable Long parentId) {
        String parentTreatments = treatmentService.findParentTreatments(parentId);
        return parentTreatments;
    }


    // 세부내용 불러오기
    @GetMapping("/find/child/{parentId}")
    @ResponseBody
    public List<TreatmentDTO> getChildTreatments(@PathVariable Long parentId) {
        List<TreatmentDTO> childTreatments = treatmentService.findChildTreatments(parentId);
        return childTreatments;
    }


    // 시술내용 추가 페이지
    @GetMapping("/treatment/add")
    public String treatmentAdd(Model model){

        List<Treatment> treatments = treatmentService.treatmentList(); // 모든 시술 조회
        model.addAttribute("treatments", treatments);

        return "treatment/treatmentAdd";
    }


    // 직접입력된 시술내용 추가
    @PostMapping("/treatment/creat")
    public String creatTreatment(TreatmentDTO treatmentDTO){

        treatmentService.addTreatment(treatmentDTO);

        return "redirect:/";
    }


    @GetMapping("/treatment/list")
    public String treatmentList(Model model){

        List<Treatment> treatmentList = treatmentService.findAll();
        model.addAttribute("treatmentList", treatmentList); // 모델에 게시물 목록 추가

        return "treatment/treatmentList";
    }

}
