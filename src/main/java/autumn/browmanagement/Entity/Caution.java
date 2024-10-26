package autumn.browmanagement.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Caution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CautionId;

    private String beforeTitle;
    private String beforeUrl;
    private String beforeText;

    private String afterTitle;
    private String afterUrl;
    private String afterText;

}
