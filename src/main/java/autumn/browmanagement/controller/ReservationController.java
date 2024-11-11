package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.DTO.ReservationDTO;
import autumn.browmanagement.Entity.Reservation;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.Entity.User;
import autumn.browmanagement.service.ReservationService;
import autumn.browmanagement.service.TreatmentService;
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

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final TreatmentService treatmentService;


    // 예약하기
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


    // 예약하기 요청
    @PostMapping("/reservation/create")  // 리턴 페이지 만들기
    public String reservationCreate(@ModelAttribute ReservationDTO reservationDTO){

        reservationService.reservationCreate(reservationDTO);

        reservationService.reservationConfirm(reservationDTO);

        return "redirect:/reservation/ownList";
    }


    // 예약 목록
    @GetMapping("/reservation/list")
    public String reservationList(Model model){

        List<ReservationDTO> reservations = reservationService.reservationList();
        model.addAttribute("reservations", reservations);

        return "reservation/reservationList";
    }


    // 내 예약 목록
    @GetMapping("/reservation/ownList")
    public String reservationOwnList(Model model, HttpSession session){
        User sessionUser = (User) session.getAttribute("user");

        List<ReservationDTO> reservations = reservationService.reservationOwnList(sessionUser.getUserId());
        model.addAttribute("reservations", reservations);

        return "reservation/reservationOwnList";
    }



    @GetMapping("reservation/{reservationId}/update")
    public String reservationUpdateForm(@PathVariable Long reservationId, Model model) {

        ReservationDTO reservations = reservationService.reservationUpdateForm(reservationId);
        model.addAttribute("reservations", reservations);


        return "reservation/reservationUpdateForm";
    }

}
