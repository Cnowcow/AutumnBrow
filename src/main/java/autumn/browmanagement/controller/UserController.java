package autumn.browmanagement.controller;

import autumn.browmanagement.domain.User;
import autumn.browmanagement.service.UserService;
import java.text.ParseException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.text.SimpleDateFormat;

import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 가입 페이지
    @GetMapping("/test/user/register")
    public String registerForm(Model model){

        return "test/user/userRegister";
    }

    // 가입 요청
    @PostMapping("/test/user/register")
    public String register(@RequestParam("name") String name,
                           @RequestParam("phone") String phone,
                           @RequestParam("birthDay") @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDay,
                           Model model){

        try {
            userService.register(name, phone, birthDay);
        } catch (IllegalStateException e) {
            // 에러 메시지 모델에 추가
            model.addAttribute("errorMessage", e.getMessage());
            return "test/user/userRegister";
        }
        return "test/index";
    }

    // 로그인 페이지
    @GetMapping("/test/user/login")
    public String LoginForm() {
        System.out.println("로그인 페이지");
        return "test/user/userLogin";
    }

    // 회원목록
    @GetMapping("/test/user/list")
    public String List(Model model){
        List<UserForm> userForms = userService.findAll(2L);
        model.addAttribute("users", userForms);

        return "test/user/userList";
    }

    // 사용자 수정 페이지
    @GetMapping("/test/user/{userId}/edit")
    public String updateUserForm(@PathVariable Long userId, Model model){
        User user = userService.findById(userId); //
        model.addAttribute("user", user); // 사용자 정보를 모델에 추가합니다.

        return "test/user/userUpdateForm";
    }

    // 사용자 수정 요청
    @PostMapping("/test/user/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") UserForm form) throws Exception {
        userService.updateUser(id, form.getName(), form.getPhone(), form.getBirthDay(), form.getFirstVisitDate());
        return "redirect:/test/user/list";
    }

    /*

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
            return "user/userCreateForm";
        }

        try {
            userService.create(name, phone, birthDay);
        } catch (IllegalStateException e) {
            // 에러 메시지 모델에 추가
            model.addAttribute("errorMessage", e.getMessage());
            return "user/userCreateForm";
        }

        return "redirect:/";
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

    // 전체 사용자 조회
    @GetMapping("/user/findAll")
    public String List(Model model, HttpSession session){
        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        // 헤더에 보낼 userInfo
        User userInfo = (User) session.getAttribute("user");
        if (userInfo == null){
            return "redirect:/user/login";
        }
        return "user/userList";
    }


    // 사용자 수정 페이지
    @GetMapping("/user/{userId}/edit")
    public String updateUserForm(@PathVariable("userId") Long userId, Model model, HttpSession session){
        User user = (User) userService.findOne(userId);

        UserForm form = new UserForm();
        user.setId(userId);
        form.setName(user.getName());
        form.setPhone(user.getPhone());
        form.setBirthDay(user.getBirthDay());
        form.setFirstVisitDate(user.getFirstVisitDate());
        form.setTreatmentCount(user.getTreatmentCount());

        model.addAttribute("userUpdateForm", form);

        // 헤더에 보낼 userInfo
        User userInfo = (User) session.getAttribute("user");
        if (userInfo == null){
            return "redirect:/user/login";
        }
        return "user/userUpdateForm";
    }

    // 사용자 수정 요청
    @PostMapping("/user/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute("userUpdateForm") UserForm form){
        userService.updateUser(id, form.getName(), form.getPhone(), form.getBirthDay(), form.getFirstVisitDate());
        return "redirect:/user/findAll";
    }
*/

}