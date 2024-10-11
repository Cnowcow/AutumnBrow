package autumn.browmanagement.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter @Setter
public class PostForm {

    private Long id;
    private String name;
    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date treatmentDate;
    private String visitPath;
    private String treatmentName;
    private String beforeImageUrl;
    private String afterImageUrl;
    private Boolean retouch;
    private Date retouchDate;
    private String info;
    private MultipartFile beforeImageFile;
    private MultipartFile afterImageFile;

}
