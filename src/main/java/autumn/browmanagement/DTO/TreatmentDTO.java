package autumn.browmanagement.DTO;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TreatmentDTO {

    private Long treatmentId;

    // 시술 이름
    private String name;


}
