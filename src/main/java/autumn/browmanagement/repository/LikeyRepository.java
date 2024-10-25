package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Likey;
import autumn.browmanagement.domain.Notice;
import autumn.browmanagement.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeyRepository extends JpaRepository<Likey, Long> {

    Optional<Likey> findByNotice_NoticeIdAndUser_UserId(Long postId, Long userId);

    Long countByNotice(Notice notice);

}
