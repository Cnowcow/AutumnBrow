package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByOrderByReviewIdDesc();


}
