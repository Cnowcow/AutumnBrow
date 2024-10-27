package autumn.browmanagement.repository;

import autumn.browmanagement.Entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByNotice_NoticeId(Long noticeId);

    List<Image> findByEvent_EventId(Long eventId);


    void deleteByImageUrl(String imageUrl); // id로 시술 내용을 찾는 메서드

}
