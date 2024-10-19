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

    // 전화번호를 이용한 비밀번호
    private String password;

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
    private Long treatmentCount;

    // 삭제여부
    private String isDeleted;


    @ManyToOne // 각 사용자는 하나의 역할만 가질 수 있다.
    @JoinColumn(name = "role_id")
    private Role role; // 역할 추가

    @PrePersist
    protected void roleId() {
        if (this.role == null) {
            Role defaultRole = new Role();
            defaultRole.setId(2L); // 기본 역할의 ID 설정
            this.role = defaultRole; // Role 객체 설정
        }
    }

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();
}
