package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Event;
import autumn.browmanagement.Entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByEventDateDesc();

}
