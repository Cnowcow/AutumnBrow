package autumn.browmanagement.controller;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.service.UserService;
import java.text.ParseException;

import jakarta.servlet.http.HttpSession;
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
            userService.create(name, phone, birthDay);
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
                        HttpSession session,
                        Model model) throws Exception {
        System.out.println("Received Name: " + name + "Received Phone: " + phone);

        User user = userService.login(name, phone);

        if (user != null) {
            session.setAttribute("user", user);
            model.addAttribute("userName", user.getName());
            System.out.println(name + " 로그인 성공, 역할: " + user.getRoleId());
            System.out.println("user.getName() = " + user.getName());
            System.out.println("저나버노" + user.getPhone());
            String decryptedPhone = EncryptionUtil.decrypt(user.getPhone());
            System.out.println("복호화된 전화번호 = " + decryptedPhone);

            return "index";
        } else {
            System.out.println("로그인 실패"+name+" "+phone);
            return "redirect:/user/login?loginFailed=true";
        }
    }

    @GetMapping("/user/logout")
    public String Logout(HttpSession session){
        session.invalidate();
        System.out.println("로그아웃");
        return "index";
    }

}
