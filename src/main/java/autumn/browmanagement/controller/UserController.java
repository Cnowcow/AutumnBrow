package autumn.browmanagement.controller;

import autumn.browmanagement.domain.User;
import autumn.browmanagement.service.UserService;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.text.SimpleDateFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;


@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 가입 페이지
    @GetMapping("/user/new")
    public String createForm(Model model){

        return "user/createUserForm";
    }

    // 가입 요청
    @PostMapping("/user/new")
    public String create(@RequestParam("name") String name,
                         @RequestParam("phone") String phone,
                         @RequestParam("birthDay") String birthDayStr,
                         Model model) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDay = null;
        try {
            birthDay = dateFormat.parse(birthDayStr);
        } catch (ParseException e) {
            // 에러 메시지 모델에 추가
            model.addAttribute("errorMessage", "잘못된 날짜 형식입니다.");
            return "user/createUserForm";
        }

        try {
            userService.join(name, phone, birthDay);
        } catch (IllegalStateException e) {
            // 에러 메시지 모델에 추가
            model.addAttribute("errorMessage", e.getMessage());
            return "user/createUserForm";
        }

        return "redirect:/";
    }

    // 로그인 페이지
    @GetMapping("/user/login")
    public String LoginForm() {
        System.out.println("로그인 페이지");
        return "user/userLogin";
    }

    @PostMapping("/user/login")
    public String Login(@RequestParam String name,
                        @RequestParam String phone,
                        Model model) {
        System.out.println("Received Name: " + name + "Received Phone: " + phone);

        boolean isLoginSuccessful = userService.login(name, phone);

        if (isLoginSuccessful) {
            System.out.println(name + " " + phone);
            model.addAttribute("name", name);
            return "index2";
        } else {
            model.addAttribute("error", "이름 또는 전화번호가 일치하지 않습니다.");
            return "user/userLogin";
        }
    }

}
