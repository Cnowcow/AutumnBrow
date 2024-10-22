package autumn.browmanagement.DTO;

import autumn.browmanagement.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class NoticeDTO {

    private Long noticeId;

    private String title;

    private String content;

    private String noticeUrl;

    private String important;

    // 공지사항 등록 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date noticeDate;


    // 사용자 정보
    private Long UserId;
}
