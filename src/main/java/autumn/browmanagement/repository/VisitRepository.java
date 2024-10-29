package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findAllByOrderByVisitIdDesc(); // id로 시술 내용을 찾는 메서드

    Optional<Visit> findByVisitId(Long visitId); // id로 시술 내용을 찾는 메서드

    Optional<Visit> findByVisitPath(String visitPath); // id로 시술 내용을 찾는 메서드

}
