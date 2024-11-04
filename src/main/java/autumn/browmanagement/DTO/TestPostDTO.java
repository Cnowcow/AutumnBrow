package autumn.browmanagement.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestPostDTO {

    private Long mainCategoryId;
    private Long subCategoryId;

    private String directMainInput;
    private String directSubInput;

    private Long id;
    private String title;

}
