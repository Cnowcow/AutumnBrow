package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class TestCustomCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String categoryType; // "대분류" or "소분류"

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TestCategory parent; // 연결된 대분류 (직접입력 시 소분류일 경우 연결)

    // 직접입력 데이터의 저장 여부 체크
    private boolean isApproved = false;

}
