package autumn.browmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
public class Role {

    @Id @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    private String name;

}
