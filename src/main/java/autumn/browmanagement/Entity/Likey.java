package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Likey {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeyId;

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
