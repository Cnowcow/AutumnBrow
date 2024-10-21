package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter @Setter

public class UserDTO {

    private Long userId;

    private String name;

    private String password;

    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstVisitDate;

    private Long treatmentCount;

    private Long roleId;

}
