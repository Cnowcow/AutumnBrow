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

    private String beforeTitle;
    private String beforeUrl;
    private String beforeText;

    private String afterTitle;
    private String afterUrl;
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
