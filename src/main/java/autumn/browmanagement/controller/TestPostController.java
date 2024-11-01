package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.DTO.TestPostDTO;
import autumn.browmanagement.Entity.TestCategory;
import autumn.browmanagement.Entity.TestPost;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.service.TestCategoryService;
import autumn.browmanagement.service.TestPostService;
import autumn.browmanagement.service.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TestPostController {

    @Autowired
    private TestCategoryService testCategoryService;
    @Autowired
    private TreatmentService treatmentService;
    @Autowired
    private TestPostService testPostService;

    // 특정 대분류에 대한 소분류 목록 불러오기
    @GetMapping("/testPost/new")
    public String showPostForm(Model model) {

        List<TestCategory> categories = testCategoryService.findMainCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("testPost", new TestPostDTO());

        List<Treatment> treatments = treatmentService.findMainCategories();
        model.addAttribute("treatments", treatments);

        return "test/postForm";
    }


    // 대분류 ID로 소분류 목록 불러오기
    @GetMapping("/testPost/{mainCategoryId}/subcategories")
    @ResponseBody
    public List<TestCategory> getSubcategories(@PathVariable Long mainCategoryId) {
        return testCategoryService.findSubCategories(mainCategoryId); // 소분류 목록 반환
    }


    // 새 시술 저장
    @PostMapping("/testPost/save")
    public String savePost(@ModelAttribute TestPostDTO testPostDTO) {

        testPostService.savePost(testPostDTO);


        return "null"; // 저장 후 목록 페이지로 리다이렉트
    }


}
