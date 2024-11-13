package autumn.browmanagement.repository;


import autumn.browmanagement.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserUserIdOrderByReservationIdDesc(Long userId);

    List<Reservation> findAllByOrderByReservationIdDesc();


    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.reservationDate = :date " +
            "AND ((r.reservationStartTime < :endTime AND r.reservationEndTime > :startTime))")
    boolean existsOverlappingReservation(@Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

}
