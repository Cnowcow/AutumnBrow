package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Treatment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long treatmentId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_treatmentId")
    private Treatment parent; // 부모 시술


/*
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Treatment> child = new ArrayList<>(); // 자식 시술
*/



}
