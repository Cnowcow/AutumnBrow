package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class EventDTO {

    private Long eventId;

    private String title;

    private String content;

    private String eventUrl;

    private String important;

    // 이벤트 등록 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date eventDate;


    // 사용자 정보
    private Long UserId;
}
