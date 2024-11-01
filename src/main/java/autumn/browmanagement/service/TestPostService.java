package autumn.browmanagement.service;

import autumn.browmanagement.DTO.TestPostDTO;
import autumn.browmanagement.Entity.TestCategory;
import autumn.browmanagement.Entity.TestPost;
import autumn.browmanagement.repository.TestCategoryRepository;
import autumn.browmanagement.repository.TestPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestPostService {

    @Autowired
    private TestCategoryRepository testCategoryRepository;
    @Autowired
    private TestPostRepository testPostRepository;

    public void savePost(TestPostDTO testPostDTO){

        TestCategory testCategory = null;

        String directInput = testPostDTO.getDirectInput();
        if (directInput != null && !directInput.isEmpty()) {
            // 먼저 기존 카테고리 조회
            testCategory = testCategoryRepository.findByNameAndParentId(directInput, testPostDTO.getMainCategoryId());

            // 카테고리가 없으면 새로 생성
            if (testCategory == null) {
                testCategory = new TestCategory();
                testCategory.setName(directInput);

                if (testPostDTO.getMainCategoryId() != null) {
                    TestCategory parentCategory = testCategoryRepository.findById(testPostDTO.getMainCategoryId())
                            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대분류 ID"));
                    testCategory.setParent(parentCategory);
                }
                // 새로 생성된 카테고리를 저장
                testCategoryRepository.save(testCategory);
            }
        }

        TestPost testPost = new TestPost();
        testPost.setTitle(testPostDTO.getTitle()); // 제목 설정

        if (testPostDTO.getMainCategoryId() != null) {
            TestCategory mainCategory = testCategoryRepository.findById(testPostDTO.getMainCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대분류 ID"));
            testPost.setMainCategory(mainCategory); // 대분류 설정
        }

        if (testCategory != null) {
            testPost.setSubCategory(testCategory); // 직접 입력한 카테고리 설정
        } else if (testPostDTO.getSubCategoryId() != null) {
            TestCategory subCategory = testCategoryRepository.findById(testPostDTO.getSubCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 소분류 ID"));
            testPost.setSubCategory(subCategory); // 기존 소분류 설정
        }

        testPostRepository.save(testPost); // TestPost 저장

    }


        /*
            TestPost testPost = new TestPost();
    TestCategory mainCategory = testCategoryRepository.findById(testPostDTO.getMainCategoryId()).orElse(null);
    TestCategory subCategory = testCategoryRepository.findById(testPostDTO.getSubCategoryId()).orElse(null);

        testPost.setMainCategory(mainCategory);
        testPost.setSubCategory(subCategory);
        testPost.setTitle(testPostDTO.getTitle());
}

        */

}

