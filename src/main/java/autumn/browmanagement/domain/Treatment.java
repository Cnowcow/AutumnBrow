package autumn.browmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Treatment parent; // 부모 시술

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Treatment> children = new ArrayList<>(); // 자식 시술 리스트

    @OneToMany(mappedBy = "treatment")
    private List<Post> posts = new ArrayList<>();

    // 기본 생성자
    public Treatment() {}

    // 시술 종류 생성자를 위한 생성자
    public Treatment(String name) {
        this.name = name;
    }

    // 자식 시술 추가 메소드
    public void addChild(Treatment child) {
        children.add(child);
        child.setParent(this); // 부모 설정
    }

}
