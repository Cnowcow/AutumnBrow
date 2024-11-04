package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.TestCategory;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.Entity.Treatment2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository2 extends JpaRepository<Treatment2, Long> {

    List<Treatment2> findAllByParentIsNull(); // 부모가 없는 경우 대분류로 가정

    List<Treatment2> findAllByParent_TreatmentId(Long parentId); // 특정 대분류에 연결된 소분류 목록


}
