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

import java.time.LocalDate;
import java.time.LocalTime;
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


    // 예약하기 요청
    @Transactional
    public void reservationCreate(ReservationDTO reservationDTO){

        LocalDate date = reservationDTO.getReservationDate();
        LocalTime startTime = reservationDTO.getReservationStartTime();
        LocalTime endTime = reservationDTO.getReservationEndTime();

        if (isReservationOverlapping(date, startTime, endTime)) {
            throw new IllegalArgumentException("해당 시간에 이미 다른 예약이 있습니다.");
        }

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


    // 예약 시간 겹침 확인 메서드
    public boolean isReservationOverlapping(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return reservationRepository.existsOverlappingReservation(date, startTime, endTime);
    }


    // 모든 예약 목록 가져오기
    public List<ReservationDTO> reservationList() {
        List<Reservation> reservations = reservationRepository.findAllByOrderByReservationIdDesc(); // 모든 게시물 조회
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
            reservationDTO.setState(reservation.getState());

            reservationDTOS.add(reservationDTO);
        }
        return reservationDTOS;
    }

    
    // 내 예약 목록 가져오기
    public List<ReservationDTO> reservationOwnList(Long userId){
            List<Reservation> reservations = reservationRepository.findByUserUserIdOrderByReservationIdDesc(userId);
            List<ReservationDTO> reservationDTOS = new ArrayList<>();

            for (Reservation reservation : reservations) {
                ReservationDTO reservationDTO = new ReservationDTO();
                reservationDTO.setReservationId(reservation.getReservationId());
                reservationDTO.setReservationDate(reservation.getReservationDate());
                reservationDTO.setReservationStartTime(reservation.getReservationStartTime());
                reservationDTO.setName(reservation.getUser().getName());
                reservationDTO.setPhone(reservation.getUser().getPhone());
                reservationDTO.setParentName(reservation.getParent().getName());
                reservationDTO.setChildName(reservation.getChild().getName());
                reservationDTO.setState(reservation.getState());


                reservationDTOS.add(reservationDTO);
            }

            return reservationDTOS;
    }


    // 예약상태 변경
    @Transactional
    public void reservationStateUpdate(Long reservationId, ReservationDTO reservationDTO) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약내역을 찾을 수 없습니다. :" + reservationId));
        
        reservation.setState(reservationDTO.getModalReservationState());

        reservationRepository.save(reservation);
    }



    /* 지금은 사용 안함
    // 예약상태 변경폼
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

        reservationDTO.setState(reservation.getState());

        return reservationDTO;
    }
    */
}
