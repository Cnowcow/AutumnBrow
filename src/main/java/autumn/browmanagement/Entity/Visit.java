package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Visit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitId;

    private String visitPath;

}
