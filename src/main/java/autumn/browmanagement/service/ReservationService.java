package autumn.browmanagement.service;

import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.DTO.ReservationDTO;
import autumn.browmanagement.Entity.*;
import autumn.browmanagement.repository.ReservationRepository;
import autumn.browmanagement.repository.TreatmentRepository;
import autumn.browmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TreatmentRepository treatmentRepository;

    @Transactional
    public void reservationCreate(ReservationDTO reservationDTO){
        Reservation reservation = new Reservation();

        Optional<User> findUser = userRepository.findByNameAndPhone(reservationDTO.getName(), reservationDTO.getPhone());
        User user;
        if(findUser.isPresent()){
            user = findUser.get();
            reservation.setUser(user);
        }

        Treatment parentTreatment = null;
        Treatment childTreatment = null;

        if (reservationDTO.getParentTreatment() != null && reservationDTO.getChildTreatment() != null){
            Treatment parentTreatments = treatmentRepository.findById(reservationDTO.getParentTreatment())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 값입니다."));
            Treatment childTreatments = treatmentRepository.findById(reservationDTO.getChildTreatment())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 값입니다."));

            parentTreatment = parentTreatments;
            childTreatment = childTreatments;
        }

        if (parentTreatment != null) {
            reservation.setParent(parentTreatment); // 대분류 설정
        }

        if (childTreatment != null) {
            reservation.setChild(childTreatment); // 소분류 설정
        }


        reservation.setReservationDate(reservationDTO.getReservationDate());
        reservation.setReservationStartTime(reservationDTO.getReservationStartTime());
        reservation.setReservationEndTime(reservationDTO.getReservationEndTime());

        reservationRepository.save(reservation);
    }


    public List<ReservationDTO> reservationList() {
        List<Reservation> reservations = reservationRepository.findAll(); // 모든 게시물 조회
        List<ReservationDTO> reservationDTOS = new ArrayList<>();

        for (Reservation reservation : reservations) {
            ReservationDTO reservationDTO = new ReservationDTO();
            reservationDTO.setReservationId(reservation.getReservationId());
            reservationDTO.setUserId(reservation.getUser() != null ? reservation.getUser().getUserId() : null);
            reservationDTO.setName(reservation.getUser() != null ? reservation.getUser().getName() : null);
            reservationDTO.setPhone(reservation.getUser() != null ? reservation.getUser().getPhone() : null);
            reservationDTO.setReservationDate(reservation.getReservationDate());
            reservationDTO.setParentName(reservation.getParent() != null ? reservation.getParent().getName() : null);
            reservationDTO.setChildName(reservation.getChild() != null ?  reservation.getChild().getName() : null);
            reservationDTO.setReservationStartTime(reservation.getReservationStartTime());
            reservationDTO.setStatus(reservation.getStatus());

            reservationDTOS.add(reservationDTO);
        }
        return reservationDTOS;
    }


    public List<ReservationDTO> reservationOwnList(Long userId){
            List<Reservation> reservations = reservationRepository.findByUserUserId(userId);
            List<ReservationDTO> reservationDTOS = new ArrayList<>();

            for (Reservation reservation : reservations) {
                ReservationDTO reservationDTO = new ReservationDTO();
                reservationDTO.setReservationId(reservation.getReservationId());
                reservationDTO.setReservationDate(reservation.getReservationDate());


                reservationDTOS.add(reservationDTO);
            }

            return reservationDTOS;
    }


    
    public void reservationConfirm(ReservationDTO reservationDTO){

        System.out.println("예약문자 보내기 로직");
        System.out.println("1111111111111111111 = " + reservationDTO.getName());
        System.out.println("2222222222222222222 = " + reservationDTO.getPhone());
        System.out.println("3333333333333333333 = " + reservationDTO.getParentTreatment());
        System.out.println("4444444444444444444 = " + reservationDTO.getParentName());
        System.out.println("5555555555555555555 = " + reservationDTO.getChildTreatment());
        System.out.println("6666666666666666666 = " + reservationDTO.getChildName());
        System.out.println("7777777777777777777 = " + reservationDTO.getReservationDate());
        System.out.println("8888888888888888888 = " + reservationDTO.getReservationStartTime());
        System.out.println("9999999999999999999 = " + reservationDTO.getReservationEndTime());

    }


    // 수정할 게시물 가져오기
    public ReservationDTO reservationUpdateForm(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약내역을 찾을 수 없습니다. :" + reservationId));

        ReservationDTO reservationDTO = new ReservationDTO();

        reservationDTO.setName(reservation.getUser().getName());
        reservationDTO.setPhone(reservation.getUser().getPhone());
        reservationDTO.setParentTreatment(reservation.getParent() != null ? reservation.getParent().getTreatmentId() : null); // 시술내용 id
        reservationDTO.setParentName(reservation.getParent().getName());
        reservationDTO.setChildName(reservation.getChild().getName());
        reservationDTO.setChildName(reservation.getChild().getName());

        reservationDTO.setStatus(reservation.getStatus());

        return reservationDTO;
    }
}
