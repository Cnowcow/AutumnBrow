package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.TestPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestPostRepository extends JpaRepository<TestPost, Long> {
}
