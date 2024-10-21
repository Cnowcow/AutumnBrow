package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Caution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CautionRepository extends JpaRepository<Caution, Long> {

    List<Caution> findAllOrderByAfterTitleIsNull();

    List<Caution> findAllOrderByBeforeTitleIsNull();


}
