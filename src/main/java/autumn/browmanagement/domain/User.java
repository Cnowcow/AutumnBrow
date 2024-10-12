package autumn.browmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 이름
    private String name;

    // 전화번호
    private String phone;

    // 생일
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE) // 날짜만 저장
    private Date birthDay;

    // 첫 방문 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date firstVisitDate;

    // 시술 횟수
    @Column(name = "treatmentCount", nullable = false)
    private Long treatmentCount = 1L;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    @Column(name = "role_id", nullable = false)
    private Long roleId = 2L; // 기본값으로 customer 역할 설정

    @PrePersist
    protected void onCreate() {
        firstVisitDate = new Date(); // 현재 시간으로 설정
    }

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();
}
