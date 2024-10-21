package autumn.browmanagement.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostDTO {

    private Long postId;

    // 시술 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime treatmentDate;

    // 비포 애프터
    private String beforeImageUrl;
    private String afterImageUrl;

    // 리터치 유무
    private Boolean retouch;

    // 리터치 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date retouchDate;

    // 비고
    private String info;

    // 시술 내용
    private Long parentTreatment;
    private String parentName;
    private Long childTreatment;
    private String childName;
    private String childView;

    /* user Entity */
    // 사용자 id
    private Long userId;

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

    private String isDeletedUser;

    /* visit Entity*/
    // 방문 경로
    private Long visitPath; // 방문경로 id
    private String visitName; // 방문경로

    /* 파일명 변환용 필드 */
    private MultipartFile beforeImageFile;
    private MultipartFile afterImageFile;


    // 파일 업로드 및 URL 생성 메서드
    public void setImageUrls(String beforeFileName, String afterFileName) {
        this.beforeImageUrl = beforeFileName;
        this.afterImageUrl = afterFileName;
    }

}
