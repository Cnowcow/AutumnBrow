package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Likey;
import autumn.browmanagement.Entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeyRepository extends JpaRepository<Likey, Long> {

    Optional<Likey> findByNotice_NoticeIdAndUser_UserId(Long postId, Long userId);

    Long countByNotice(Notice notice);

}
