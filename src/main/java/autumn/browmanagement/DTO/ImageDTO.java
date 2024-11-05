package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class ImageDTO {

    private Long imageId;

    // 사진 주소
    private String imageUrl;

    // 공지사항
    private Long noticeId;

    // 주의사항
    private Long eventId;
}
