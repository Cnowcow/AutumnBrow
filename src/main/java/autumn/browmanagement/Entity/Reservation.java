package autumn.browmanagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter @Setter
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    // 예약 날짜
    private LocalDate reservationDate;

    // 예약 시작 시간
    private LocalTime reservationStartTime;

    // 예약 종료 시간
    private LocalTime reservationEndTime;

    // 예약 상태
    private String status = "예약대기";

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "parent_treatment")
    private Treatment parent;

    // 세부내용
    @ManyToOne
    @JoinColumn(name = "child_treatment")
    private Treatment child;

}
