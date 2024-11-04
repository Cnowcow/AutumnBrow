package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Treatment2 {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long treatmentId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_treatmentId")
    private Treatment2 parent; // 부모 시술


}
