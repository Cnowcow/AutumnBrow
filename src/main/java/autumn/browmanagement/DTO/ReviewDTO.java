package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ReviewDTO {

    private Long reviewId;

    private String title;

    private String content;

    private String reviewUrl;

    // 리뷰 작성 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date revieDate;


    // 사용자 정보
    private Long UserId;
}
