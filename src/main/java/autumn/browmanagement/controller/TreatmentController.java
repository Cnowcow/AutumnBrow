package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.TreatmentDTO;
import autumn.browmanagement.Entity.TestCategory;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.Entity.Treatment2;
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



    @GetMapping("/treatment/{parentId}/childTreatment")
    @ResponseBody
    public List<Treatment2> getChildTreatment(@PathVariable Long parentId) {
        return treatmentService.findChildTreatment(parentId); // 소분류 목록 반환
    }







    @GetMapping("/testPost/{mainCategoryId}/subcategories2")
    @ResponseBody
    public List<Treatment> getSubcategories(@PathVariable Long mainCategoryId) {
        return treatmentService.findSubCategories(mainCategoryId); // 소분류 목록 반환
    }




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


    // 시술내용 추가 페이지
    @GetMapping("/treatment/add")
    public String addTreatmentFrom(Model model) {

        List<Treatment> treatments = treatmentService.treatmentList(); // 모든 시술 조회
        model.addAttribute("treatments", treatments);

        return "treatment/treatmentAddForm";
    }


    // 직접입력된 시술내용 추가
    @PostMapping("/treatment/add")
    public String addTreatment(TreatmentDTO treatmentDTO) {

        treatmentService.addTreatment(treatmentDTO);

        return "redirect:/treatment/list";
    }


    // 시술목록 관리 리스트
    @GetMapping("/treatment/list")
    public String treatmentList(Model model) {
        // 시술 목록을 서비스에서 받아서 모델에 추가
        List<TreatmentDTO> treatmentList = treatmentService.getAllTreatments();
        model.addAttribute("treatments", treatmentList);

        return "treatment/treatmentList";
    }


    // 시술내용 삭제 요청
    @PostMapping("/treatment/delete/{treatmentId}")
    public ResponseEntity<String> deleteTreatment(@PathVariable Long treatmentId) {
        treatmentService.deleteTreatment(treatmentId); // 서비스 호출
        return ResponseEntity.ok("삭제 성공"); // 성공 메시지 응답
    }
}