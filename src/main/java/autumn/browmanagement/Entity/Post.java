package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter @Setter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    // 시술 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime treatmentDate;

    // 시술 전 사진
    private String beforeImageUrl;

    // 시술 후 사진
    private String afterImageUrl;

    // 리터치 여부
    private Boolean retouch = false;

    // 리터치 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date retouchDate;

    // 비고
    private String info;

    // 삭제여부
    private String isDeleted;

    // 사용자 정보 - 이름, 전화번호 등은 User 엔티티에서 참조
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 방문경로
    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;

    // 시술내용
    @ManyToOne
    @JoinColumn(name = "parent_treatment")
    private Treatment parent;

    // 세부내용
    @ManyToOne
    @JoinColumn(name = "child_treatment")
    private Treatment child;

}
