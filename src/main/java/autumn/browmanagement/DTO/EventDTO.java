package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class EventDTO {

    private Long eventId;

    // 이벤트 제목
    private String title;

    // 이벤트 내용
    private String content;

    // 이벤트 중요도
    private String important;

    // 이벤트 조회수
    private Long eventHits;

    // 이벤트 등록 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime eventDate;
    
    // 이벤트 좋아요
    private Long likesCount;
    
    // 이벤트 작성자
    private Long userId;
    
    // 이벤트 작성자
    private String userName;

    // 이벤트 사진
    private MultipartFile eventImage;

    //이벤트 사진
    private List<String> imageUrls;
}
