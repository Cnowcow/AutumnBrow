package autumn.browmanagement.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter @Setter
public class PostForm {

    private Long id;

    // 이름
    private String name;

    // 전화번호
    private String phone;

    // 첫방문 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstVisitDate;

    // 시술횟수
    private Long treatmentCount;

    // 생년월일
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;

    // 시술 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date treatmentDate;

    // 방문 경로
    private String visitPath;

    // 시술 내용
    private String parentTreatment;
    private String childTreatment;

    // 비포 애프터
    private String beforeImageUrl;
    private String afterImageUrl;

    // 리터치 유무
    private Boolean retouch;

    // 리터치 날짜
    private Date retouchDate;

    // 비고
    private String info;

    private MultipartFile beforeImageFile;
    private MultipartFile afterImageFile;

}
