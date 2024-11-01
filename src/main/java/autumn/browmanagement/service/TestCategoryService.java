package autumn.browmanagement.service;

import autumn.browmanagement.DTO.TestPostDTO;
import autumn.browmanagement.Entity.TestCategory;
import autumn.browmanagement.Entity.TestPost;
import autumn.browmanagement.repository.TestCategoryRepository;
import autumn.browmanagement.repository.TestPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestCategoryService {

    @Autowired
    private TestCategoryRepository testCategoryRepository;

    @Autowired
    private TestPostRepository testPostRepository;

    // 대분류 목록 불러오기
    public List<TestCategory> findMainCategories() {
        return testCategoryRepository.findAllByParentIsNull(); // 대분류는 부모가 없는 경우로 가정
    }


    // 특정 대분류에 대한 소분류 목록 불러오기
    public List<TestCategory> findSubCategories(Long mainCategoryId) {
        return testCategoryRepository.findAllByParentId(mainCategoryId);
    }


    // 새로운 시술 기록 저장
    public void savePost(TestPostDTO testPostDTO) {
        TestPost testPost = new TestPost();
        TestCategory mainCategory = testCategoryRepository.findById(testPostDTO.getMainCategoryId()).orElse(null);
        TestCategory subCategory = testCategoryRepository.findById(testPostDTO.getSubCategoryId()).orElse(null);

        testPost.setMainCategory(mainCategory);
        testPost.setSubCategory(subCategory);
        testPost.setTitle(testPostDTO.getTitle());
        testPostRepository.save(testPost); // Post 저장
    }



}
