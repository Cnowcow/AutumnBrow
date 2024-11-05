package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.TreatmentDTO;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;


    // 시술내용 추가 페이지
    @GetMapping("/treatment/add")
    public String addTreatmentFrom(Model model) {

        List<Treatment> treatments = treatmentService.treatmentList();
        model.addAttribute("treatments", treatments);

        return "treatment/treatmentAddForm";
    }


/*    // 시술목록 관리 리스트
    @GetMapping("/treatment/list")
    public String treatmentList(Model model) {

        List<TreatmentDTO> treatmentList = treatmentService.getAllTreatments();
        model.addAttribute("treatments", treatmentList);

        return "treatment/treatmentList";
    }*/


    // 세부내용 불러오기
    @GetMapping("/treatment/{parentId}/childTreatment")
    @ResponseBody
    public List<Treatment> getChildTreatment(@PathVariable Long parentId) {
        return treatmentService.findChildTreatment(parentId);
    }


    /*
    // 시술내용 불러오기
    @GetMapping("/find/parent/{parentId}")
    @ResponseBody
    public String getParentTreatments(@PathVariable Long parentId) {
        return treatmentService.findParentTreatments(parentId);
    }


    // 세부내용 불러오기
    @GetMapping("/find/child/{parentId}")
    @ResponseBody
    public List<TreatmentDTO> getChildTreatments(@PathVariable Long parentId) {
        return treatmentService.findChildTreatments(parentId);
    }





    // 시술내용 삭제 요청
    @PostMapping("/treatment/delete/{treatmentId}")
    public ResponseEntity<String> deleteTreatment(@PathVariable Long treatmentId) {
        treatmentService.deleteTreatment(treatmentId); // 서비스 호출
        return ResponseEntity.ok("삭제 성공"); // 성공 메시지 응답
    }*/
}