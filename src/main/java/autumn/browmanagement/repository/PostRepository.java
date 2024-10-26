package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByIsDeletedOrderByTreatmentDateDesc(String isDeleted);

    Optional<Post> findByPostId(Long postId); // ID로 사용자 조회

    List<Post> findByUser_UserIdAndIsDeletedOrderByTreatmentDateDesc(Long postId, String isDeleted); // ID로 사용자 조회


}
