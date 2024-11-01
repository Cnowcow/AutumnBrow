package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class TestCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 시술명 (ex. 속눈썹, 자연눈썹 등)

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TestCategory parent; // 대분류 ID (대분류일 경우 null)

}
