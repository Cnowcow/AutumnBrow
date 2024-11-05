package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.UserDTO;
import autumn.browmanagement.Entity.User;
import autumn.browmanagement.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    public String userRegisterForm(Model model){

        return "user/userRegister";
    }


    // 가입 요청
    @PostMapping("/user/register")
    public String userRegister(@RequestParam("name") String name,
                           @RequestParam("phone") String phone,
                           @RequestParam("birthDay") @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDay,
                           Model model){

        try {
            userService.userRegister(name, phone, birthDay);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/userRegister";
        }

        return "redirect:/";
    }


    // 로그인 페이지
    @GetMapping("/user/login")
    public String userLoginForm(Model model) {
        System.out.println("로그인 페이지");

        return "user/userLogin";
    }


    // 로그인 요청
    @PostMapping("/user/login")
    public String userLogin(@RequestParam String name,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) throws Exception {

        User user = userService.userLogin(name, password);
        if (user != null) {
            session.setAttribute("user", user);
            model.addAttribute("userInfo", user);
            System.out.println("로그인 성공" +name+" "+password);
            return "redirect:/";
        } else {
            System.out.println("로그인 실패"+name+" "+password);
            return "redirect:/user/login?loginFailed=true";
        }
    }


    // 로그아웃
    @GetMapping("/user/logout")
    public String userLogout(Model model, HttpSession session){
        session.invalidate();
        System.out.println("로그아웃");

        return "redirect:/";
    }


    // 회원목록
    @GetMapping("/user/list")
    public String userList(Model model){
        List<UserDTO> userDTOS = userService.userList(2L, "N");
        model.addAttribute("users", userDTOS);

        return "user/userList";
    }


    // 사용자 수정 페이지
    @GetMapping("/user/{userId}/update")
    public String userUpdateForm(@PathVariable Long userId, Model model){
        User user = userService.userFindById(userId);
        model.addAttribute("user", user);

        return "user/userUpdateForm";
    }


    // 사용자 수정 요청
    @PostMapping("/user/{userId}/update")
    public String userUpdate(@PathVariable Long userId, @ModelAttribute("user") UserDTO form) throws Exception {
        userService.updateUser(userId, form.getName(), form.getPhone(), form.getBirthDay(), form.getFirstVisitDate());
        return "redirect:/user/list";
    }


    // 사용자 삭제 요청
    @PostMapping("/user/{userId}/delete")
    public String userDelete(@PathVariable Long userId, Model model){

        return userService.userDelete(userId);
    }


    /*
        // 헤더에 보낼 userInfo
        User userInfo = (User) session.getAttribute("user");
        if (userInfo == null){
            return "redirect:/user/login";
        }

    */

}