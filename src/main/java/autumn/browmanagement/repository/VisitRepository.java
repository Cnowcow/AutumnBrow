package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findByVisitId(Long visitId); // id로 시술 내용을 찾는 메서드

}
