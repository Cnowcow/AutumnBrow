package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.DTO.ReservationDTO;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.service.ReservationService;
import autumn.browmanagement.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final TreatmentService treatmentService;

    @GetMapping("/reservation/create")
    public String reservationCreateForm(Model model){

        List<Treatment> treatments = treatmentService.treatmentFindParent();
        model.addAttribute("treatments", treatments);

        return "reservation/reservationCreate";
    }

    @PostMapping("/reservation/create")
    public String reservationCreate(@ModelAttribute ReservationDTO reservationDTO){

        System.out.println("1111111111111111111 = " + reservationDTO.getName());
        System.out.println("2222222222222222222 = " + reservationDTO.getPhone());
        System.out.println("3333333333333333333 = " + reservationDTO.getParentTreatment());
        System.out.println("4444444444444444444 = " + reservationDTO.getParentName());
        System.out.println("5555555555555555555 = " + reservationDTO.getChildTreatment());
        System.out.println("6666666666666666666 = " + reservationDTO.getChildName());
        System.out.println("7777777777777777777 = " + reservationDTO.getReservationDate());
        System.out.println("8888888888888888888 = " + reservationDTO.getReservationStartTime());
        System.out.println("9999999999999999999 = " + reservationDTO.getReservationEndTime());

        reservationService.reservationCreate(reservationDTO);

        return "null";
    }
}
