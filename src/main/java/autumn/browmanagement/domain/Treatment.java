package autumn.browmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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


    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Treatment> child = new ArrayList<>(); // 자식 시술


    // 기본 생성자
    public Treatment() {}

    // 시술 종류 생성자를 위한 생성자
    public Treatment(String name) {
        this.name = name;
    }

}
