package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class ImageDTO {

    private Long imageId;

    private String imageUrl;

    private Long noticeId;
}
