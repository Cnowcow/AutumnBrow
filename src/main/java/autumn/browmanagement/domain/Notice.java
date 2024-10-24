package autumn.browmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    private String title;

    private String content;

    private String noticeUrl;

    private String important;

    private Long noticeHits = 0L;

    private Long noticeLike = 0L;

    // 공지사항 등록 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime noticeDate;


    // 사용자 정보 - 이름, 전화번호 등은 User 엔티티에서 참조
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
