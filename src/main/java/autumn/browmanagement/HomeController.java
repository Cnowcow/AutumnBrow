package autumn.browmanagement;

import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.repository.TreatmentRepository;
import autumn.browmanagement.service.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;


@Controller
public class HomeController {

    @Autowired
    private TreatmentService treatmentService;

    @GetMapping("/")
    public String Home(Model model) {

        return "index2";
    }

    @GetMapping("/createPost")
    public String PostWrite(Model model) {
        Map<String, List<String>> treatments = treatmentService.getTreatments();
        model.addAttribute("treatments", treatments);
        System.out.println("컨트롤러 treatments 값 = " + treatments);
        return "/post/createPostForm";
    }

    @GetMapping("/registerPost")
    public String RegisterPost(Model model) {

        return "/post/register";
    }

    @GetMapping("/updatePost")
    public String PostList(Model model) {

        return "/post/updatePostForm";
    }

}
