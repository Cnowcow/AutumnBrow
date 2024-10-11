package autumn.browmanagement.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter

public class UserForm {

    @NotEmpty(message = "회원 일름은 필수 입니다.")
    private String name;

    private String password;

    private String role;


}
