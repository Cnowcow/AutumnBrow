package autumn.browmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Treatment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "treatment_id")
    private Long treatmentId;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Treatment parent; // 부모 시술

    private Long depth; // 계층을 나타내는 필드

    // 기본 생성자
    public Treatment() {}

    // 시술 종류 생성자를 위한 생성자
    public Treatment(String name) {
        this.name = name;
        this.depth = 0L;  // 기본 depth는 0 (최상위 카테고리)
    }

}
