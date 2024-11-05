package autumn.browmanagement.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VisitDTO {

    private Long visitId;

    // 방문경로
    private String visitPath;

}
