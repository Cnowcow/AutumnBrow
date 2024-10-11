package autumn.browmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public class Post {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    // 방문경로
    private String visitPath;

    // 시술 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE) // 날짜만 저장
    private Date treatmentDate;

    // 시술 전 사진
    private String beforeImageUrl;

    // 시술 후 사진
    private String afterImageUrl;

    // 리터치 여부
    private String retouch;

    // 리터치 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date retouchDate;

    // 비고
    private String info;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;


    // 기본 생성자
    public Post() {}

    // 필요한 정보를 포함하는 생성자
    public Post(String visitPath, Date treatmentDate, String beforeImageUrl, String afterImageUrl, String retouch, Date retouchDate, String info, User user, Treatment treatment) {
        this.visitPath = visitPath;
        this.treatmentDate = treatmentDate;
        this.beforeImageUrl = beforeImageUrl;
        this.afterImageUrl = afterImageUrl;
        this.retouch = retouch;
        this.retouchDate = retouchDate;
        this.info = info;
        this.user = user;
        this.treatment = treatment;
    }

}
