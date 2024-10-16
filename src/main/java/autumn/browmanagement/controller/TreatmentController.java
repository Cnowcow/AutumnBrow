package autumn.browmanagement.controller;

import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

/*    @GetMapping("/find/parent")
    public String findParentTreatment(Model model) {
        List<Treatment> parentTreatments = treatmentService.findParentTreatments();
        model.addAttribute("parent", parentTreatments); // 모델에 부모 시술 정보 추가

        return "find/treatment";
    }

    @GetMapping("/find/child/{parentId}")
    @ResponseBody
    public List<TreatmentForm> getChildTreatments(@PathVariable Long parentId) {
        List<TreatmentForm> childTreatments = treatmentService.findChildTreatments(parentId);
        System.out.println("Parent ID: " + parentId + " - Child Treatments: " + childTreatments);  // 로그 추가
        return childTreatments;  // parentId가 1인 자식 카테고리를 반환
    }*/

}
