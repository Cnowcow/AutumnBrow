package autumn.browmanagement.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TreatmentDTO {

    private Long treatmentId;
    private String name;

    private Long parentId;

    private String parentName;
    private String childName;
}
