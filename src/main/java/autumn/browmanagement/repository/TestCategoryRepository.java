package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.TestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCategoryRepository extends JpaRepository<TestCategory, Long> {

    List<TestCategory> findAllByParentIsNull(); // 부모가 없는 경우 대분류로 가정
    List<TestCategory> findAllByParentId(Long parentId); // 특정 대분류에 연결된 소분류 목록

    TestCategory findByNameAndParentId(String directInput, Long parentId); // 부모가 없는 경우 대분류로 가정

}
