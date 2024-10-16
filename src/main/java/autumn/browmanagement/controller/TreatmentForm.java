package autumn.browmanagement.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TreatmentForm {

    private Long id;
    private String name;

    // 부모 ID 또는 이름을 포함할 수 있음 (선택적)
    private Long parentId; // 부모의 ID
}
