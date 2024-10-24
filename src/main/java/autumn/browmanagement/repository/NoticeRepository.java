package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByOrderByNoticeIdDesc();

    List<Notice> findAllByOrderByNoticeDateDesc();



}
