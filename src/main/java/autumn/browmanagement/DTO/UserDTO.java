package autumn.browmanagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter @Setter

public class UserDTO {

    private Long userId;

    // 사용자 이름
    @NotNull(message = "사용자 이름은 필수입니다.")
    private String name;

    // 사용자 비밀번호
    private String password;

    // 사용자 전화번호
    @NotNull(message = "사용자 전화번호는 필수입니다.")
    private String phone;

    // 사용자 생년월일
    @NotNull(message = "사용자 생년월일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;

    // 사용자 첫방문 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstVisitDate;

    // 사용자 방문횟수
    private Long treatmentCount;

    // 사용자 역할
    private Long roleId;

}
