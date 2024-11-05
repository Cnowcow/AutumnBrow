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

    // 비포 애프터 사진
    private String beforeImageUrl;
    private String afterImageUrl;

    // 리터치 유무
    private Boolean retouch;

    // 리터치 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date retouchDate;

    // 비고
    private String info;

    // 사용자 탈퇴여부
    private String isDeletedUser;


    /* user Entity */
    private Long userId; // 사용자 id
    private String name; // 이름
    private String phone;// 전화번호

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstVisitDate; // 첫방문 날짜

    private Long treatmentCount; // 시술횟수

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay; // 생년월일


    /* visit Entity*/
    private Long visitId; // 방문경로 id
    private String visitPath; // 방문경로


    /* treatment Entity*/
    private Long parentTreatment; // 시술내용
    private Long childTreatment; // 세부내용
    private String directParentTreatment; // 시술내용 이름
    private String directChildTreatment; // 세부내용 이름

    
    /* 파일명 변환용 필드 */
    private MultipartFile beforeImageFile;
    private MultipartFile afterImageFile;

    // 파일 업로드 및 URL 생성 메서드
    public void setImageUrls(String beforeFileName, String afterFileName) {
        this.beforeImageUrl = beforeFileName;
        this.afterImageUrl = afterFileName;
    }

}
