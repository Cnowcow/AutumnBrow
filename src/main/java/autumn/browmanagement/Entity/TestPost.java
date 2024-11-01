package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class TestPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TestCategory mainCategory; // 대분류

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private TestCategory subCategory; // 소분류

    private String title;

}


