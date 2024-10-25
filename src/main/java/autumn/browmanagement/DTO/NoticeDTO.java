package autumn.browmanagement.DTO;

import autumn.browmanagement.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class NoticeDTO {

    private Long noticeId;

    private String title;

    private String content;

    private String noticeUrl;

    private String important;

    private Long noticeHits;

    private String userName;

    // 공지사항 등록 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime noticeDate;

    // 좋아요
    private Long likesCount;

    // 사용자 정보
    private Long userId;

    private MultipartFile noticeImage;
}
