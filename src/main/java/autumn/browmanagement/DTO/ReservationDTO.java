package autumn.browmanagement.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationDTO {

    private Long reservationId;

    // 예약 날짜
    private LocalDate reservationDate;

    // 예약 시작 시간
    private LocalTime reservationStartTime;

    // 예약 종료 시간
    private LocalTime reservationEndTime;

    // 예약 상태
    private String state;

    // 예약 상태 변경
    private String modalReservationState;

    /* user Entity */
    private Long userId; // 사용자 id

    @NotEmpty(message = "사용자 이름은 필수입니다.")
    private String name; // 이름

    @NotEmpty(message = "사용자 전화번호는 필수입니다.")
    private String phone;// 전화번호

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay; // 생년월일


    /* treatment Entity*/
    @NotNull(message = "시술내용은 필수입니다.")
    private Long parentTreatment; // 시술내용

    @NotNull(message = "시술내용은 필수입니다.")
    private Long childTreatment; // 세부내용

    private String parentName; // 시술내용 이름

    private String childName; // 세부내용 이름

}
