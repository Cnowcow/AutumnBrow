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

    private String title;

    private String content;

    private String important;

    private Long eventHits;

    private String userName;

    // 이벤트 등록 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime eventDate;

    // 좋아요
    private Long likesCount;

    // 사용자 정보
    private Long userId;


    private MultipartFile eventImage;

    private List<String> imageUrls;
}
