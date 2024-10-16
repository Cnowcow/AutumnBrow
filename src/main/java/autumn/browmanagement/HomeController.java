package autumn.browmanagement;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String Home(Model model) {

        return "index";
    }

    @GetMapping("/test")
    public String testHome(Model model) {

        return "test/index";
    }

    @GetMapping("/list")
    public String PostList(Model model) {

        return "/post/postList";
    }

}
