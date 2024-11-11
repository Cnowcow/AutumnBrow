package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Post;
import autumn.browmanagement.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserUserId(Long userId);



}
