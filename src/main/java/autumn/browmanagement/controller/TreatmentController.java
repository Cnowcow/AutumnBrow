package autumn.browmanagement.controller;

import autumn.browmanagement.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;


    @GetMapping("/find/child/{parentId}")
    @ResponseBody
    public List<TreatmentForm> getChildTreatments(@PathVariable Long parentId) {
        List<TreatmentForm> childTreatments = treatmentService.findChildTreatments(parentId);
        return childTreatments;  // parentId가 1인 세부시술
    }


}
