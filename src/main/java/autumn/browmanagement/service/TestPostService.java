package autumn.browmanagement.service;

import autumn.browmanagement.DTO.TestPostDTO;
import autumn.browmanagement.Entity.*;
import autumn.browmanagement.repository.TestCategoryRepository;
import autumn.browmanagement.repository.TestPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestPostService {

    @Autowired
    private TestCategoryRepository testCategoryRepository;
    @Autowired
    private TestPostRepository testPostRepository;


    public void savePost(TestPostDTO testPostDTO) {

        TestCategory mainCategory = null;
        TestCategory subCategory = null;

        // 1. 대분류가 null일 때
        if (testPostDTO.getMainCategoryId() == null) {
            String directMainInput = testPostDTO.getDirectMainInput();
            String directSubInput = testPostDTO.getDirectSubInput();

            if (directMainInput != null && !directMainInput.isEmpty() && directSubInput != null && !directSubInput.isEmpty()) {
                // 새 대분류 생성
                mainCategory = new TestCategory();
                mainCategory.setName(directMainInput);
                testCategoryRepository.save(mainCategory); // 대분류 저장

                // 새 소분류 생성
                subCategory = new TestCategory();
                subCategory.setName(directSubInput);
                subCategory.setParent(mainCategory); // 소분류의 부모를 대분류로 설정
                testCategoryRepository.save(subCategory); // 소분류 저장
            }
        }
        // 2. 대분류는 기존 값일 때
        else {
            mainCategory = testCategoryRepository.findById(testPostDTO.getMainCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대분류 ID"));

            if (testPostDTO.getSubCategoryId() == null) {
                String directSubInput = testPostDTO.getDirectSubInput();
                if (directSubInput != null && !directSubInput.isEmpty()) {
                    // 새 소분류 생성
                    subCategory = new TestCategory();
                    subCategory.setName(directSubInput);
                    subCategory.setParent(mainCategory); // 소분류의 부모를 대분류로 설정
                    testCategoryRepository.save(subCategory); // 소분류 저장
                }
            }
        }

        // 3. 둘 다 null이 아닐 때
        if (testPostDTO.getMainCategoryId() != null && testPostDTO.getSubCategoryId() != null) {
            TestCategory existingSubCategory = testCategoryRepository.findById(testPostDTO.getSubCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 소분류 ID"));
            subCategory = existingSubCategory; // 기존 소분류 사용
        }

        // TestPost 엔티티 생성 및 설정
        TestPost testPost = new TestPost();
        testPost.setTitle(testPostDTO.getTitle()); // 제목 설정

        if (mainCategory != null) {
            testPost.setMainCategory(mainCategory); // 대분류 설정
        }
        if (subCategory != null) {
            testPost.setSubCategory(subCategory); // 소분류 설정
        }

        testPostRepository.save(testPost); // TestPost 저장
    }



    public void updatePost(Long postId, TestPostDTO testPostDTO){

        TestPost testPost = testPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));




        TestCategory mainCategory = null;
        TestCategory subCategory = null;

        // 1. 대분류가 null일 때
        if (testPostDTO.getMainCategoryId() == null) {
            String directMainInput = testPostDTO.getDirectMainInput();
            String directSubInput = testPostDTO.getDirectSubInput();

            if (directMainInput != null && !directMainInput.isEmpty() && directSubInput != null && !directSubInput.isEmpty()) {
                // 새 대분류 생성
                mainCategory = new TestCategory();
                mainCategory.setName(directMainInput);
                testCategoryRepository.save(mainCategory); // 대분류 저장

                // 새 소분류 생성
                subCategory = new TestCategory();
                subCategory.setName(directSubInput);
                subCategory.setParent(mainCategory); // 소분류의 부모를 대분류로 설정
                testCategoryRepository.save(subCategory); // 소분류 저장
            }
        }
        // 2. 대분류는 기존 값일 때
        else {
            mainCategory = testCategoryRepository.findById(testPostDTO.getMainCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대분류 ID"));

            if (testPostDTO.getSubCategoryId() == null) {
                String directSubInput = testPostDTO.getDirectSubInput();
                if (directSubInput != null && !directSubInput.isEmpty()) {
                    // 새 소분류 생성
                    subCategory = new TestCategory();
                    subCategory.setName(directSubInput);
                    subCategory.setParent(mainCategory); // 소분류의 부모를 대분류로 설정
                    testCategoryRepository.save(subCategory); // 소분류 저장
                }
            }
        }

        // 3. 둘 다 null이 아닐 때
        if (testPostDTO.getMainCategoryId() != null && testPostDTO.getSubCategoryId() != null) {
            TestCategory existingSubCategory = testCategoryRepository.findById(testPostDTO.getSubCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 소분류 ID"));
            subCategory = existingSubCategory; // 기존 소분류 사용
        }




        // TestPost 엔티티 생성 및 설정
        testPost.setTitle(testPostDTO.getTitle()); // 제목 설정

        if (mainCategory != null) {
            testPost.setMainCategory(mainCategory); // 대분류 설정
        }
        if (subCategory != null) {
            testPost.setSubCategory(subCategory); // 소분류 설정
        }

        testPostRepository.save(testPost); // TestPost 저장

    }






    public List<TestPostDTO> findAll(){
        List<TestPost> testPosts = testPostRepository.findAll();
        List<TestPostDTO> testPostDTOS = new ArrayList<>();

        for (TestPost testPost : testPosts){
            TestPostDTO testPostDTO = new TestPostDTO();
            testPostDTO.setId(testPost.getId());
            testPostDTO.setTitle(testPost.getTitle());

            if (testPost.getMainCategory() == null || testPost.getMainCategory().getName().isEmpty()) {
                testPostDTO.setDirectMainInput("");
            }else {
                testPostDTO.setDirectMainInput(testPost.getMainCategory().getName());
            }

            if (testPost.getSubCategory() == null || testPost.getSubCategory().getName().isEmpty()) {
                testPostDTO.setDirectSubInput("");
            }else {
                testPostDTO.setDirectSubInput(testPost.getSubCategory().getName());
            }


            testPostDTOS.add(testPostDTO);
        }

        return testPostDTOS;
    }


    public TestPost findById(Long postId) {
        TestPost testPost = testPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        return testPost;
    }

    /*public void savePost(TestPostDTO testPostDTO){

        TestCategory testCategory = null;

        String directInput = testPostDTO.getDirectSubInput();
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

    }*/




}

