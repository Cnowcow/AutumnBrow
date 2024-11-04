package autumn.browmanagement.DTO;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TreatmentDTO2 {

    private Long treatmentId;

    private String name;

    private Long parentId;
    private Long childId;

    private String parentName;
    private String childName;

}
