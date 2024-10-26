package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    List<Treatment> findByParentIsNullOrderByTreatmentIdDesc(); // parentId가 null인 시술을 조회하는 메서드

    Optional<Treatment> findByTreatmentId(Long treatmentId); // id로 시술 내용을 찾는 메서드

    List<Treatment> findByParent_TreatmentId(Long parentId); // id로 시술 내용을 찾는 메서드

}
