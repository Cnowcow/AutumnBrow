package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    List<Treatment> findByParentIsNull(); // parentId가 null인 시술을 조회하는 메서드

    Optional<Treatment> findById(Long id); // id로 시술 내용을 찾는 메서드

    List<Treatment> findByParent_TreatmentId(Long parentId); // id로 시술 내용을 찾는 메서드

}
