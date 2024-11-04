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
import java.util.Optional;

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

        System.out.println("11111111111 = " + testPostDTO.getMainCategoryId());
        System.out.println("22222222222 = " + testPostDTO.getDirectMainInput());
        System.out.println("33333333333 = " + testPostDTO.getSubCategoryId());
        System.out.println("44444444444 = " + testPostDTO.getDirectSubInput());
        System.out.println("55555555555 = " + testPostDTO.getTitle());

        testPostService.savePost(testPostDTO);


        return "redirect:/testPost/list";
    }


    @GetMapping("/testPost/list")
    public String listPost(Model model) {

        List<TestPostDTO> testPostDTOS = testPostService.findAll();
        model.addAttribute("posts", testPostDTOS);

        return "test/postList";
    }


    @GetMapping("/testPost/{postId}/update")
    public String updatePostForm(@PathVariable Long postId, Model model) {

        List<TestCategory> categories = testCategoryService.findMainCategories();
        model.addAttribute("categories", categories);

        TestPost testPost = testPostService.findById(postId);
        model.addAttribute("posts", testPost);

        return "test/postUpdate";
    }

    @PostMapping("/testPost/{postId}/update")
    public String updatePost(@PathVariable Long postId, @ModelAttribute TestPostDTO testPostDTO) {
        System.out.println("aaaaaaaaaaaaaaaa = " + testPostDTO.getTitle());
        System.out.println("bbbbbbbbbbbbbbbb = " + testPostDTO.getMainCategoryId());
        System.out.println("cccccccccccccccc = " + testPostDTO.getDirectMainInput());
        System.out.println("dddddddddddddddd = " + testPostDTO.getSubCategoryId());
        System.out.println("eeeeeeeeeeeeeeee = " + testPostDTO.getDirectSubInput());

        testPostService.updatePost(postId, testPostDTO);

        return "redirect:/testPost/list";
    }

}
