package autumn.browmanagement.service;

import autumn.browmanagement.DTO.ReservationDTO;
import autumn.browmanagement.Entity.Reservation;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.Entity.User;
import autumn.browmanagement.repository.ReservationRepository;
import autumn.browmanagement.repository.TreatmentRepository;
import autumn.browmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
