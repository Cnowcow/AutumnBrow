package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {


    List<Treatment> findAllByParentIsNull();

    List<Treatment> findAllByParent_TreatmentId(Long parentId);

    List<Treatment> findByParentIsNullOrderByTreatmentIdDesc();


}
