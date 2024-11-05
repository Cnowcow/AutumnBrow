package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ReviewDTO {

    private Long reviewId;

    // 리뷰 제목
    private String title;

    // 리뷰 내용
    private String content;

    // 리뷰 작성 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date revieDate;

    // 리뷰 작성자
    private Long UserId;

    // 리뷰 작성자
    private String UserName;

    // 리뷰 사진
    private MultipartFile reviewImage;

    // 리뷰 사진
    private List<String> imageUrls;

    private String reviewUrl;

}
