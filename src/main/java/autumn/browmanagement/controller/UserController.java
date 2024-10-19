package autumn.browmanagement.controller;

import autumn.browmanagement.domain.User;
import autumn.browmanagement.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // 가입 페이지
    @GetMapping("/user/register")
    public String registerForm(Model model){

        return "user/userRegister";
    }


    // 가입 요청
    @PostMapping("/user/register")
    public String register(@RequestParam("name") String name,
                           @RequestParam("phone") String phone,
                           @RequestParam("birthDay") @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDay,
                           Model model){

        try {
            userService.register(name, phone, birthDay);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/userRegister";
        }

        return "index";
    }


    // 로그인 페이지
    @GetMapping("/user/login")
    public String LoginForm(Model model) {
        System.out.println("로그인 페이지");

        return "user/userLogin";
    }


    // 로그인 요청
    @PostMapping("/user/login")
    public String Login(@RequestParam String name,
                        @RequestParam String phone,
                        HttpSession session,
                        Model model) throws Exception {

        User user = userService.login(name, phone);

        if (user != null) {
            session.setAttribute("user", user);
            model.addAttribute("userInfo", user);
            System.out.println("로그인 성공" +name+" "+phone);
            return "redirect:/";
        } else {
            System.out.println("로그인 실패"+name+" "+phone);
            return "redirect:/user/login?loginFailed=true";
        }
    }


    // 로그아웃
    @GetMapping("/user/logout")
    public String Logout(Model model, HttpSession session){
        session.invalidate();
        System.out.println("로그아웃");

        return "redirect:/";
    }


    // 회원목록
    @GetMapping("/user/list")
    public String List(Model model){
        List<UserForm> userForms = userService.findAll(2L);
        model.addAttribute("users", userForms);

        return "user/userList";
    }


    // 사용자 수정 페이지
    @GetMapping("/user/{userId}/edit")
    public String updateUserForm(@PathVariable Long userId, Model model){
        User user = userService.findById(userId);
        model.addAttribute("user", user);

        return "user/userUpdateForm";
    }


    // 사용자 수정 요청
    @PostMapping("/user/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") UserForm form) throws Exception {
        userService.updateUser(id, form.getName(), form.getPhone(), form.getBirthDay(), form.getFirstVisitDate());
        return "redirect:/user/list";
    }

    /*
        // 헤더에 보낼 userInfo
        User userInfo = (User) session.getAttribute("user");
        if (userInfo == null){
            return "redirect:/user/login";
        }

    // 로그인 페이지
    @GetMapping("/user/login")
    public String LoginForm() {
        System.out.println("로그인 페이지");
        return "user/userLogin";
    }

    // 로그인 요청
    @PostMapping("/user/login")
    public String Login(@RequestParam String name,
                        @RequestParam String phone,
                        HttpSession session,
                        Model model) throws Exception {

        User user = userService.login(name, phone);

        if (user != null) {
            session.setAttribute("user", user);
            model.addAttribute("userInfo", user);

            return "index";
        } else {
            System.out.println("로그인 실패"+name+" "+phone);
            return "redirect:/user/login?loginFailed=true";
        }

    }

    // 로그아웃
    @GetMapping("/user/logout")
    public String Logout(HttpSession session){
        session.invalidate();
        System.out.println("로그아웃");
        return "index";
    }
*/

}