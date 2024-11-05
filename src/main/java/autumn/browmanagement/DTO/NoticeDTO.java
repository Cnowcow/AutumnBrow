package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NoticeDTO {

    private Long noticeId;

    // 공지사항 제목
    private String title;

    // 공지사항 내용
    private String content;

    // 공지사항 중요도
    private String important;

    // 공지사항 조회수
    private Long noticeHits;
    
    // 공지사항 등록 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime noticeDate;

    // 공지사항 좋아요
    private Long likesCount;

    // 공지사항 작성자
    private Long userId;

    // 공지사항 작성자
    private String userName;

    // 공지사항 사진
    private MultipartFile noticeImage;

    // 공지사항 사진
    private List<String> imageUrls;

}
