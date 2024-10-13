package autumn.browmanagement.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter @Setter

public class UserForm {

    private Long id;

    private String name;

    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstVisitDate;

    private Long treatmentCount;

}
