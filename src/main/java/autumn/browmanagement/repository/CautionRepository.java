package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Caution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CautionRepository extends JpaRepository<Caution, Long> {
}
