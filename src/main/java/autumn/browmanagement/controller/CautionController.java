package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.CautionDTO;
import autumn.browmanagement.service.CautionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CautionController {

    private final CautionService cautionService;

    // 시술 전 주의사항
    @GetMapping("/caution/before")
    public String noticeBefore(Model model){

        List<CautionDTO> caution = cautionService.cautionFindBefore();
        model.addAttribute("cautions", caution);

        return "caution/beforeCaution";
    }


    // 시술 후 주의사항
    @GetMapping("/caution/after")
    public String noticeAfter(Model model){

        List<CautionDTO> caution = cautionService.cautionFindAfter();
        model.addAttribute("cautions", caution);

        return "caution/afterCaution";
    }


    // 주의사항 등록 폼
    @GetMapping("/caution/create")
    public String cautionCreateForm(){

        return "caution/cautionCreate";
    }


    // 주의사항 등록 요청
    @PostMapping("/caution/create")
    public String cautionCreate(@ModelAttribute CautionDTO cautionDTO, Model model) throws IOException {
        cautionService.handleImageUpload(cautionDTO);

        cautionService.cautionCreate(cautionDTO);

        return "redirect:/";
    }


}
