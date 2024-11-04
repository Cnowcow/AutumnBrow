package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestPostDTO {
    private Long id;

    private Long mainCategoryId;
    private Long subCategoryId;

    private String directMainInput;
    private String directSubInput;

    private String title;

}
