package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.DTO.ReservationDTO;
import autumn.browmanagement.Entity.Reservation;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.Entity.User;
import autumn.browmanagement.config.MailService;
import autumn.browmanagement.service.ReservationService;
import autumn.browmanagement.service.TreatmentService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;
import java.util.SimpleTimeZone;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final TreatmentService treatmentService;
    private final MailService mailService;


    // 예약하기 폼
    @GetMapping("/reservation/create")  // 로그인 후 이용 가능하게 로직 추가하기
    public String reservationCreateForm(Model model, HttpSession session){
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser == null){
            return "redirect:/user/login?loginPlz=true";
        }
        List<Treatment> treatments = treatmentService.treatmentFindParent();
        model.addAttribute("treatments", treatments);

        return "reservation/reservationCreateForm";
    }


    // 예약하기 요청        날짜, 시간 예외처리 추가
    @PostMapping("/reservation/create")  // 리턴 페이지 만들기
    public String reservationCreate(@ModelAttribute ReservationDTO reservationDTO, HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        Long userId = sessionUser.getUserId();

        reservationService.reservationCreate(reservationDTO);

        try {
            String to = "hhjnn92@icloud.com";
            String subject = "예약요청 메일입니다.";

            String name = reservationDTO.getName();
            String parentName = reservationDTO.getParentName();
            String childName = reservationDTO.getChildName();
            String date = String.valueOf(reservationDTO.getReservationDate());
            String startTime = String.valueOf(reservationDTO.getReservationStartTime());
            String endTime = String.valueOf(reservationDTO.getReservationEndTime());

            String text = name + "님  " + date + "  " + startTime + "~" + endTime + "  " + parentName + " " + childName + " ";

            mailService.sendMail(to, subject, text);
            return "redirect:/reservation/"+ userId + "/ownList";
        } catch (MessagingException e){
            return "실패" + e.getMessage();
        }
    }


    // 모든 예약 목록
    @GetMapping("/reservation/list")
    public String reservationList(Model model){

        List<ReservationDTO> reservations = reservationService.reservationList();
        model.addAttribute("reservations", reservations);

        return "reservation/reservationList";
    }


    // 내 예약 목록
    @GetMapping("/reservation/{userId}/ownList")
    public String reservationOwnList(@PathVariable Long userId, Model model, HttpSession session){

        User sessionUser = (User) session.getAttribute("user");

        List<ReservationDTO> reservations = reservationService.reservationOwnList(userId);
        model.addAttribute("name",sessionUser.getName());
        model.addAttribute("reservations", reservations);

        return "reservation/reservationOwnList";
    }


    // 예약상태 변경 요청
    @PostMapping("/reservation/{reservationId}/stateUpdate")
    public String reservationStateUpdate(@PathVariable Long reservationId, ReservationDTO reservationDTO){

        reservationService.reservationStateUpdate(reservationId, reservationDTO);

        return "redirect:/reservation/list";
    }


/* 지금은 사용 안함
    // 예약상태 변경
    @GetMapping("/reservation/{reservationId}/update")
    public String reservationUpdateForm(@PathVariable Long reservationId, Model model) {

        ReservationDTO reservations = reservationService.reservationUpdateForm(reservationId);
        model.addAttribute("reservations", reservations);

        return "reservation/reservationUpdateForm";
    }
    */

}
