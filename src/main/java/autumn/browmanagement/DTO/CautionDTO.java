package autumn.browmanagement.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CautionDTO {

    private Long CautionId;

    // 시술 전 주의사항 제목
    private String beforeTitle;

    // 시술 전 주의사항 사진
    private String beforeUrl;

    // 시술 전 주의사항 내용
    private String beforeText;

    // 시술 후 주의사항 제목
    private String afterTitle;

    // 시술 후 주의사항 사진
    private String afterUrl;

    // 시술 후 주의사항 내용
    private String afterText;


    /* 파일명 변환용 필드 */
    private MultipartFile beforeImage;
    private MultipartFile afterImage;


    // 파일 업로드 및 URL 생성 메서드
    public void setImageUrls(String beforeFileName, String afterFileName) {
        this.beforeUrl = beforeFileName;
        this.afterUrl = afterFileName;
    }

}
